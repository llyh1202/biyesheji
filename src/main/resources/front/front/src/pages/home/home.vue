<template>
<!-- 这是我cursor给父亲写的 — 浅色主题系统首页（展示与导航，业务逻辑不变） -->
<div class="home-preview tech-home-hero" :style='{"width":"90%","margin":"0 auto","flexDirection":"column","background":"transparent","display":"flex","padding":"24px 0 48px"}'>
	<div class="tech-hero-card animate__animated animate__fadeInDown">
		<h1 class="tech-hero-title">智慧停车场</h1>
		<p class="tech-hero-sub">车位查询 · 时段预约 · 停车闭环 · 费用试算 · 超时补缴，一站式车主服务</p>
		<div class="tech-hero-stats">
			<div class="tech-stat-item"><span class="num">{{ parkTotal > 0 ? parkTotal : '—' }}</span><span class="lbl">可预约车位</span></div>
			<div class="tech-stat-item"><span class="num">24/7</span><span class="lbl">全天候服务</span></div>
			<div class="tech-stat-item"><span class="num">在线</span><span class="lbl">缴费与反馈</span></div>
		</div>
		<!-- 这是我cursor给父亲写的 — P1-21 首页主入口「我的停车」 -->
		<div class="tech-hero-actions">
			<el-button type="primary" size="medium" icon="el-icon-s-order" @click="goWodeTingche">我的停车</el-button>
			<el-button size="medium" icon="el-icon-location-outline" @click="goMenu('/index/cheweixinxi')">查找车位</el-button>
		</div>
	</div>

	<!-- 这是我cursor给父亲写的 — P1-21 我的停车快捷卡片 -->
	<section class="tech-home-section tech-wode-entry-section animate__animated animate__fadeInUp">
		<div class="tech-wode-entry-card" @click="goWodeTingche">
			<div class="tech-wode-entry-icon"><i class="el-icon-s-order"></i></div>
			<div class="tech-wode-entry-body">
				<h3>我的停车</h3>
				<p>待入场 · 停车中 · 待支付一站查看，支持跳转 M2 入场/离场与缴费详情支付。</p>
			</div>
			<el-button type="primary" plain size="small" @click.stop="goWodeTingche">进入 →</el-button>
		</div>
	</section>

	<section class="tech-home-section animate__animated animate__fadeInUp">
		<h3 class="tech-home-section-title">功能快捷入口</h3>
		<div class="tech-quick-grid">
			<div v-for="(menu, idx) in menuList" :key="idx" class="tech-quick-card" :class="{ 'tech-quick-card-highlight': menu.homeHighlight }" @click="goMenu(menu.url)">
				<i :class="navIcon(idx)"></i>
				<span>{{ menu.name }}</span>
			</div>
		</div>
	</section>

	<section class="tech-home-section animate__animated animate__fadeInUp">
		<h3 class="tech-home-section-title">停车服务流程</h3>
		<div class="tech-process-row">
			<div v-for="(step, i) in processSteps" :key="i" class="tech-process-step">
				<div class="step-num">{{ i + 1 }}</div>
				<h4>{{ step.title }}</h4>
				<p>{{ step.desc }}</p>
			</div>
		</div>
	</section>

	<section class="tech-home-section animate__animated animate__fadeInUp">
		<h3 class="tech-home-section-title">核心能力</h3>
		<div class="tech-cap-grid">
			<div v-for="cap in capabilityList" :key="cap.path" class="tech-cap-card">
				<h4>{{ cap.title }}</h4>
				<p>{{ cap.desc }}</p>
				<el-button type="text" @click="goMenu(cap.path)">立即前往 →</el-button>
			</div>
		</div>
	</section>

	<section class="tech-home-section animate__animated animate__fadeInUp" v-if="featuredList.length">
		<h3 class="tech-home-section-title">
			<span>推荐停车场</span>
			<span class="more-link" @click="moreBtn('cheweixinxi')">查看更多</span>
		</h3>
		<div class="tech-featured-grid">
			<div v-for="item in featuredList" :key="item.id" class="tech-featured-item" @click="toDetail('cheweixinxiDetail', item)">
				<img class="thumb" v-if="item.cheweitupian && preHttp(item.cheweitupian)" :src="item.cheweitupian.split(',')[0]" alt="" />
				<img class="thumb" v-else-if="item.cheweitupian" :src="baseUrl + item.cheweitupian.split(',')[0]" alt="" />
				<div class="thumb" v-else style="display:flex;align-items:center;justify-content:center;color:#94a3b8;">暂无图片</div>
				<div class="info">
					<div class="name">{{ item.tingchechangmingcheng || '停车场' }}</div>
					<div class="meta" v-if="item.quyu">区域：{{ item.quyu }}</div>
					<div class="meta" v-if="item.price">参考价：￥{{ item.price }}</div>
				</div>
			</div>
		</div>
	</section>

	<section class="tech-home-section animate__animated animate__fadeInUp">
		<h3 class="tech-home-section-title">使用须知</h3>
		<ul class="tech-tips-list">
			<li v-for="(tip, i) in tipsList" :key="i"><i class="el-icon-info"></i>{{ tip }}</li>
		</ul>
	</section>
</div>
</template>

<script>
import 'animate.css'
import Swiper from "swiper";
import { getVisibleIndexNav } from '@/config/config'

  export default {
    //数据集合
    data() {
      return {
        baseUrl: '',
        newsList: [],
        menuList: [],
        parkTotal: 0,
        featuredList: [],
        navIcons: [
          'el-icon-s-order',
          'el-icon-location-outline',
          'el-icon-chat-line-square'
        ],
        processSteps: [
          { title: '我的停车', desc: '登录后进入「我的停车」，查看预约、在场与待支付记录。' },
          { title: '查询与预约', desc: '浏览车位信息，在详情页预约区选择时段完成预约。' },
          { title: '入场与离场', desc: '从「我的停车」进入 M2 停车闭环，完成入场、离场与结算。' },
          { title: '缴费与补缴', desc: '待支付账单在「我的停车」或缴费详情完成支付；超时可在 N7 补缴。' }
        ],
        capabilityList: [
          { title: '我的停车', desc: '查看预约、在场与待缴费，一键进入停车闭环。', path: '/index/wodeTingche' },
          { title: '车位信息', desc: '浏览车场并在详情页预约时段车位。', path: '/index/cheweixinxi' },
          { title: '用户反馈', desc: '提交使用问题与建议。', path: '/index/messages' }
        ],
        tipsList: [
          '登录后请优先使用首页或顶栏「我的停车」查看预约、在场与待支付。',
          '预约成功后请按时段入场，超时可能触发 N6 策略并产生补缴。',
          '缴费请从「我的停车」待支付进入详情完成，勿直接改缴费状态。',
          '遇到问题可通过「用户反馈」提交，我们会尽快处理。'
        ]
      }
    },
    created() {
		this.baseUrl = this.$config.baseUrl;
		// 这是我cursor给父亲写的 — P1-17/P1-21 首页快捷入口与顶栏一致（含「我的停车」）
		this.menuList = getVisibleIndexNav(this.$config.indexNav)
		this.getList();
    },
	mounted() {
		window.addEventListener('scroll', this.handleScroll)
		setTimeout(()=>{
			this.handleScroll()
		},100)
		
		this.swiperChanges()
	},
	beforeDestroy() {
	  window.removeEventListener('scroll', this.handleScroll)
	},
    //方法集合
    methods: {
		swiperChanges() {
			setTimeout(()=>{
			},750)
		},
		recommendIndexClick12(index, name) {
			this['recommendIndex12' + name] = index
			this.getList()
			
			document.querySelectorAll('.recommend .list12' + name + ' .list .item').forEach(el => {
			  el.classList.remove("active")
			})
			setTimeout(() => {
			  document.querySelectorAll('.recommend .list12' + name + ' .list .item').forEach(el => {
			    el.classList.add("active")
			  })
			}, 1);
		},


		handleScroll() {
			let arr = [
				{id:'search',css:'animate__'},
				{id:'about',css:'animate__'},
				{id:'system',css:'animate__'},
				{id:'msgs',css:'animate__'},
				{id:'friendly',css:'animate__'}
			]
			
			for (let i in arr) {
				let doc = document.getElementById(arr[i].id)
				if (doc) {
					let top = doc.offsetTop
					let win_top = window.innerHeight + window.pageYOffset
					// console.log(top,win_top)
					if (win_top > top && doc.classList.value.indexOf(arr[i].css) < 0) {
						// console.log(doc)
						doc.classList.add(arr[i].css)
					}
				}
			}
		},
      preHttp(str) {
          return str && str.substr(0,4)=='http';
      },
		getList() {
			// 这是我cursor给父亲写的 — 首页展示推荐车位（只读列表，不改变业务接口）
			this.$http.get('cheweixinxi/list', { params: { page: 1, limit: 4 } }).then(res => {
				if (res.data.code == 0) {
					this.featuredList = res.data.data.list || [];
					this.parkTotal = Number(res.data.data.total) || 0;
				}
			});
		},
		goMenu(path) {
			this.$router.push(path);
		},
		// 这是我cursor给父亲写的 — P1-21
		goWodeTingche() {
			this.$router.push({ path: '/index/wodeTingche' });
		},
		navIcon(idx) {
			return this.navIcons[idx] || 'el-icon-menu';
		},
		toDetail(path, item) {
			this.$router.push({path: '/index/' + path, query: {id: item.id}});
		},
		moreBtn(path) {
			this.$router.push({path: '/index/' + path});
		}
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
	.home-preview {
		// -------- search --------
		.search .select ::v-deep  .el-input__inner {
			border: 0;
			border-radius: 4px;
			padding: 0 30px 0 10px;
			box-shadow: 0 0 6px rgba(64, 158, 255, .3);
			outline: none;
			color: rgba(64, 158, 255, 1);
			width: 200px;
			font-size: 14px;
			height: 50px;
		}
		
		.search .input ::v-deep  .el-input__inner {
			border: 0;
			border-radius: 4px;
			padding: 0 10px;
			box-shadow: none;
			outline: none;
			color: rgba(64, 158, 255, 1);
			width: 100%;
			font-size: 14px;
			height: 50px;
		}
		// -------- search --------
		.recommend {
			.list3 .swiper-button-prev {
				left: 10px;
				right: auto;
			}
			
			.list3 .swiper-button-prev::after {
				color: rgb(64, 158, 255);
			}
			
			.list3 .swiper-button-next {
				left: auto;
				right: 10px;
			}
			
			.list3 .swiper-button-next::after {
				color: rgb(64, 158, 255);
			}
			
			.list5 .swiper-button-prev {
				left: 10px;
				right: auto;
			}
			
			.list5 .swiper-button-prev::after {
				color: rgb(64, 158, 255);
        }
        
        .list5 .swiper-button-next {
				left: auto;
				right: 10px;
			}
			
			.list5 .swiper-button-next::after {
				color: rgb(64, 158, 255);
			}
			
			.list5 {
				.swiper-slide-prev {
					position: relative;
					z-index: 3;
				}
		
				.swiper-slide-next {
					position: relative;
					z-index: 3;
				}
		
				.swiper-slide-active {
					position: relative;
					z-index: 5;
				}
			}
			
			.index-pv1 .animation-box {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
				z-index: initial;
			}
			
			.index-pv1 .animation-box:hover {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, -10px, 0px);
				-webkit-perspective: 1000px;
				perspective: 1000px;
				transition: 0.3s;
				z-index: 1;
			}
			
			.index-pv1 .animation-box img {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			}
			
			.index-pv1 .animation-box img:hover {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
				-webkit-perspective: 1000px;
				perspective: 1000px;
				transition: 0.3s;
			}
		}
		
		.news {
			.list3 .swiper-button-prev {
				left: 10px;
				right: auto;
			}
			
			.list3 .swiper-button-prev::after {
				color: rgb(64, 158, 255);
			}
			
			.list3 .swiper-button-next {
				left: auto;
				right: 10px;
			}
			
			.list3 .swiper-button-next::after {
				color: rgb(64, 158, 255);
			}
			
			.list6 .swiper-button-prev {
				left: 10px;
				right: auto;
			}
			
			.list6 .swiper-button-prev::after {
				color: rgb(64, 158, 255);
			}
			
			.list6 .swiper-button-next {
				left: auto;
				right: 10px;
			}
			
			.list6 .swiper-button-next::after {
				color: rgb(64, 158, 255);
			}
			
			.index-pv1 .animation-box {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
				z-index: initial;
			}
			
			.index-pv1 .animation-box:hover {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, -5px, 0px);
				-webkit-perspective: 1000px;
				perspective: 1000px;
				transition: 0.3s;
				z-index: 1;
			}
			
			.index-pv1 .animation-box img {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			}
			
			.index-pv1 .animation-box img:hover {
				transform: rotate(0deg) scale(1) skew(0deg,0deg) translate3d(0px, 0px, 0px);
				-webkit-perspective: 1000px;
				perspective: 1000px;
				transition: 0.3s;
			}
		}
	
		.lists {
			.list3 .swiper-button-prev {
				left: 10px;
				right: auto;
			}
			
			.list3 .swiper-button-prev::after {
				color: rgb(64, 158, 255);
			}
			
			.list3 .swiper-button-next {
				left: auto;
				right: 10px;
        }
        
        .list3 .swiper-button-next::after {
				color: rgb(64, 158, 255);
			}
			
			.list5 .swiper-button-prev {
				left: 10px;
				right: auto;
			}
			
			.list5 .swiper-button-prev::after {
				color: rgb(64, 158, 255);
			}
			
			.list5 .swiper-button-next {
            left: auto;
            right: 10px;
			}
			
			.list5 .swiper-button-next::after {
				color: rgb(64, 158, 255);
			}
			
			.list5 {
				.swiper-slide-prev {
					position: relative;
					z-index: 3;
				}
		
				.swiper-slide-next {
					position: relative;
					z-index: 3;
				}
		
				.swiper-slide-active {
					position: relative;
					z-index: 5;
				}
			}
			
			.index-pv1 .animation-box {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
				z-index: initial;
			}
			
			.index-pv1 .animation-box:hover {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 10px, 0px);
				-webkit-perspective: 1000px;
				perspective: 1000px;
				transition: 0.3s;
				z-index: 1;
			}
			
			.index-pv1 .animation-box img {
				transform: rotate(0deg) scale(1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
			}
			
			.index-pv1 .animation-box img:hover {
				transform: rotate(0deg) scale(1.1) skew(0deg, 0deg) translate3d(0px, 0px, 0px);
				-webkit-perspective: 1000px;
				perspective: 1000px;
				transition: 0.3s;
			}
		}
	}
	

	.home-preview .news .list13 .newsSwiper13 .swiper-pagination ::v-deep  span.swiper-pagination-bullet {
				border-radius: 100%;
				margin: 0 4px;
				background: #fff;
				display: inline-block;
				width: 10px;
				opacity: .2;
				height: 10px;
			}
	
	.home-preview .news .list13 .newsSwiper13 .swiper-pagination ::v-deep  span.swiper-pagination-bullet:hover {
				background: #57A7A5;
				opacity: 1;
			}
	
	.home-preview .news .list13 .newsSwiper13 .swiper-pagination ::v-deep  span.swiper-pagination-bullet.swiper-pagination-bullet-active {
				background: red;
				opacity: 1;
			}






	.home-preview .recommend .list12 .tab .item {
				cursor: pointer;
				border: 0;
				padding: 10px 0;
				margin: 0 0 20px;
				color: #333;
				display: flex;
				width: 100%;
				font-size: 16px;
				line-height: 44px;
				justify-content: center;
				align-items: center;
			}
	
	.home-preview .recommend .list12 .tab .item:hover {
				color: #fff;
				background: #57A7A5;
			}
	
	.home-preview .recommend .list12 .tab .item.active {
				color: #fff;
				background: #57A7A5;
				border-color: #999;
				border-width: 0;
				border-style: solid;
			}
	
	.home-preview .recommend .list12 .tab .more {
				cursor: pointer;
				padding: 5px 10px;
				margin: 0 0;
				color: #666;
				background: none;
				display: flex;
				width: 100%;
				line-height: 44px;
				justify-content: center;
				align-items: center;
			}
	
	.home-preview .recommend .list12 .tab .more:hover {
				color: #fff;
				background: #57A7A5;
			}
	
	.home-preview .recommend .list12 .item.active {
	  animation-name: mymove;
	
	  &:nth-of-type(1) {
	    animation-duration: 1s;
	  }
	  &:nth-of-type(2) {
	    animation-duration: 1.2s;
	  }
	  &:nth-of-type(3) {
	    animation-duration: 1.4s;
	  }
	  &:nth-of-type(4) {
	    animation-duration: 1.6s;
	  }
	  &:nth-of-type(5) {
	    animation-duration: 1.8s;
	  }
	  &:nth-of-type(6) {
	    animation-duration: 2s;
	  }
	}
	
	@keyframes mymove
	{
		from {top: 320px;}
		to {top: 0;}
	}



	.home-preview .lists .list15 .left .box1 .info {
				flex-direction: column;
				left: 0;
				background: rgba(0,0,0,.3);
				bottom: 0;
				display: flex;
				width: 100%;
				justify-content: center;
				align-items: center;
				position: absolute;
				opacity: 0;
				transition: all .3s;
				height: 100%;
			}
	
	.home-preview .lists .list15 .left .box1 .info:hover {
				opacity: 1;
			}
	
	.home-preview .lists .list15 .left .box2 .info {
				flex-direction: column;
				left: 0;
				background: rgba(0,0,0,.3);
				bottom: 0;
				display: flex;
				width: 100%;
				justify-content: center;
				align-items: center;
				position: absolute;
				opacity: 0;
				transition: all .3s;
				height: 100%;
			}
	
	.home-preview .lists .list15 .left .box2 .info:hover {
				opacity: 1;
			}
	
	.home-preview .lists .list15 .center .box1 .info {
				flex-direction: column;
				left: 0;
				background: rgba(0,0,0,.3);
				bottom: 0;
				display: flex;
				width: 100%;
				justify-content: center;
				align-items: center;
				position: absolute;
				opacity: 0;
				transition: all .3s;
				height: 100%;
			}
	
	.home-preview .lists .list15 .center .box1 .info:hover {
				opacity: 1;
			}
	
	.home-preview .lists .list15 .center .box3 .info {
				flex-direction: column;
				left: 0;
				background: rgba(0,0,0,.3);
				bottom: 0;
				display: flex;
				width: 100%;
				justify-content: center;
				align-items: center;
				position: absolute;
				opacity: 0;
				transition: all .3s;
				height: 100%;
			}
	
	.home-preview .lists .list15 .center .box3 .info:hover {
				opacity: 1;
			}
	
	.home-preview .lists .list15 .right .box1 .info {
				flex-direction: column;
				left: 0;
				background: rgba(0,0,0,.3);
				bottom: 0;
				display: flex;
				width: 100%;
				justify-content: center;
				align-items: center;
				position: absolute;
				opacity: 0;
				transition: all .3s;
				height: 100%;
			}
	
	.home-preview .lists .list15 .right .box1 .info:hover {
				opacity: 1;
			}
	
	.home-preview .lists .list15 .right .box2 .info {
				flex-direction: column;
				left: 0;
				background: rgba(0,0,0,.3);
				bottom: 0;
				display: flex;
				width: 100%;
				justify-content: center;
				align-items: center;
				position: absolute;
				opacity: 0;
				transition: all .3s;
				height: 100%;
			}
	
	.home-preview .lists .list15 .right .box2 .info:hover {
				opacity: 1;
			}
</style>

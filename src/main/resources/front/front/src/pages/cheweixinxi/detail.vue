<template>
<div>
	<div :style='{"padding":"20px","boxShadow":"0px 4px 10px 0px rgba(0,0,0,0.3)","margin":"30px auto","borderRadius":"0","background":"url(),#fff","display":"flex","width":"80%","backgroundSize":"50%","backgroundPosition":"center left","backgroundRepeat":"no-repeat","justifyContent":"flex-end"}' class="breadcrumb-preview">
		<el-breadcrumb :separator="'>'" :style='{"fontSize":"16px","lineHeight":"1"}'>
			<el-breadcrumb-item class="item1" to="/"><a>首页</a></el-breadcrumb-item>
			<el-breadcrumb-item class="item2" v-for="(item, index) in breadcrumbItem" :key="index" to="/index/cheweixinxi"><a>{{item.name}}</a></el-breadcrumb-item>
			<el-breadcrumb-item class="item3"><a href="javascript:void(0);">详情</a></el-breadcrumb-item>
		</el-breadcrumb>
	</div>
	<div :style='{"padding":"20px","boxShadow":"0px 4px 10px 0px rgba(0,0,0,0.3)","margin":"30px auto","borderRadius":"0","background":"url(),#fff","display":"flex","width":"80%","backgroundSize":"50%","backgroundPosition":"center left","backgroundRepeat":"no-repeat","justifyContent":"flex-end"}'>
		<el-button size="mini" @click="backClick">返回</el-button>
	</div>
	<div class="detail-preview" :style='{"width":"80%","margin":"10px auto","position":"relative","flexWrap":"wrap","display":"flex"}'>
		<div class="attr" :style='{"minHeight":"560px","padding":"0 10px","background":"#fff","display":"flex","width":"60%","position":"relative","order":"2"}'>

			<div class="info" :style='{"padding":"10px 0","margin":"0 0 0 10px","background":"#fff","flex":"1"}'>
				<div class="item" :style='{"padding":"10px","margin":"0 0 10px 0","alignItems":"center","flexWrap":"wrap","background":"none","display":"flex","justifyContent":"flex-end"}'>
					<div :style='{"width":"100%","fontSize":"16px","color":"#000","fontWeight":"bold"}'>
                    {{detail.tingchechangmingcheng}}
                    </div>
				</div>
				<div class="item" :style='{"border":"1px solid #D8D8D8","padding":"0 10px","margin":"0 0 10px 0","background":"none","justifyContent":"spaceBetween","display":"flex"}'>
					<div class="lable" :style='{"padding":"0 10px","color":"#818181","textAlign":"right","width":"auto","fontSize":"14px","lineHeight":"40px","minWidth":"100px","height":"40px"}'>区域</div>
					<div  :style='{"padding":"8px 10px 0","fontSize":"14px","lineHeight":"24px","color":"#818181","flex":"1","height":"auto"}'>{{detail.quyu}}</div>
				</div>
				<div class="item" :style='{"border":"1px solid #D8D8D8","padding":"0 10px","margin":"0 0 10px 0","background":"none","justifyContent":"spaceBetween","display":"flex"}'>
					<div class="lable" :style='{"padding":"0 10px","color":"#818181","textAlign":"right","width":"auto","fontSize":"14px","lineHeight":"40px","minWidth":"100px","height":"40px"}'>车位数量</div>
					<div  :style='{"padding":"8px 10px 0","fontSize":"14px","lineHeight":"24px","color":"#818181","flex":"1","height":"auto"}'>{{detail.cheweishuliang}}</div>
				</div>
				<div class="item" :style='{"border":"1px solid #D8D8D8","padding":"0 10px","margin":"0 0 10px 0","background":"none","justifyContent":"spaceBetween","display":"flex"}'>
					<div class="lable" :style='{"padding":"0 10px","color":"#818181","textAlign":"right","width":"auto","fontSize":"14px","lineHeight":"40px","minWidth":"100px","height":"40px"}'>小时单价</div>
					<div  :style='{"padding":"8px 10px 0","fontSize":"14px","lineHeight":"24px","color":"#818181","flex":"1","height":"auto"}'>{{detail.xiaoshidanjia}}</div>
				</div>
				<div class="item" :style='{"border":"1px solid #D8D8D8","padding":"0 10px","margin":"0 0 10px 0","background":"none","justifyContent":"spaceBetween","display":"flex"}'>
					<div class="lable" :style='{"padding":"0 10px","color":"#818181","textAlign":"right","width":"auto","fontSize":"14px","lineHeight":"40px","minWidth":"100px","height":"40px"}'>车位详情</div>
					<div  :style='{"padding":"8px 10px 0","fontSize":"14px","lineHeight":"24px","color":"#818181","flex":"1","height":"auto"}'>{{detail.cheweixiangqing}}</div>
				</div>
				<div class="btn" :style='{"padding":"10px 0","flexWrap":"wrap","justifyContent":"flex-start","display":"flex"}'>
					<el-button :style='{"border":"0","cursor":"pointer","padding":"0 10px","margin":"0 5px 0 0","color":"#fff","borderRadius":"0","background":"#57A7A5","width":"auto","lineHeight":"40px","fontSize":"14px","height":"40px"}' v-if="btnAuth('cheweixinxi','修改')" @click="editClick">修改</el-button>
					<el-button :style='{"border":"1px solid #CCCCCC","cursor":"pointer","padding":"0 10px","margin":"0 5px 10px 0","color":"#CCCCCC","borderRadius":"0","background":"none","width":"auto","lineHeight":"40px","fontSize":"14px","height":"40px"}' v-if="btnAuth('cheweixinxi','删除')" @click="delClick">删除</el-button>
					<el-button :style='{"border":"1px solid #CCCCCC","cursor":"pointer","padding":"0 10px","margin":"0 5px 10px 0","color":"#CCCCCC","borderRadius":"0","background":"none","width":"auto","lineHeight":"40px","fontSize":"14px","height":"40px"}' v-if="btnAuth('cheweixinxi','私聊')&&detail.id!=mid" @click="chatClick">联系TA</el-button>
					<!-- 这是我cursor给父亲写的 — P1-16 原「停车」改为预约停车，跳转本车场预约区，不走 chezijinchang 跨表 -->
					<el-button :style='{"border":"0","cursor":"pointer","padding":"0 10px","margin":"0 5px 10px 0","color":"#fff","borderRadius":"0","background":"#0891b2","width":"auto","lineHeight":"40px","fontSize":"14px","height":"40px"}' v-if="!centerType && showReserveParkingBtn" type="primary" @click="scrollToYuyue">预约停车</el-button>
				</div>
			</div>
		</div>
		
			<el-carousel v-if="detailBanner.length" :style='{"width":"40%","margin":"0","height":"500px","order":"1"}' trigger="click" indicator-position="inside" arrow="always" type="default" direction="horizontal" height="500px" :autoplay="false" :interval="3000" :loop="true">
				<el-carousel-item :style='{"borderRadius":"10px","width":"100%","height":"100%"}' v-for="item in detailBanner" :key="item.id">
					<img :style='{"objectFit":"cover","width":"100%","height":"100%"}' :preview-src-list="[item]" v-if="item.substr(0,4)=='http'" :src="item" class="image">
					<img :style='{"objectFit":"cover","width":"100%","height":"100%"}' :preview-src-list="[baseUrl + item]" v-else :src="baseUrl + item" class="image">
				</el-carousel-item>
			</el-carousel>


		

		
		<el-tabs class="detail" :style='{"border":"none","width":"100%","boxShadow":"none","background":"#FFF","order":"5"}' v-model="activeName" type="border-card">
		</el-tabs>
	</div>

	<!-- 这是我cursor给父亲写的 — P1-03/P1-16 本车场车位预约区（指令 03） -->
	<div id="chewei-yuyue-panel" class="chewei-yuyue-panel" v-if="detail.id && !centerType">
		<div class="chewei-yuyue-inner">
			<h3 class="chewei-yuyue-title">预约停车</h3>
			<p class="chewei-yuyue-desc">选择时段后查询可约车位，点击车位卡片再确认预约（无需填写车位编号）。</p>
			<el-form label-width="88px" class="chewei-yuyue-form" @submit.native.prevent>
				<el-form-item label="开始时间">
					<el-date-picker v-model="yuyueForm.kaishiShijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="预约开始" style="width:220px" />
				</el-form-item>
				<el-form-item label="结束时间">
					<el-date-picker v-model="yuyueForm.jieshuShijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="预约结束" style="width:220px" />
				</el-form-item>
				<el-form-item label=" ">
					<el-button type="primary" native-type="button" :loading="loadingAvail" @click="querySpotAvailability">查询该时段可约车位</el-button>
					<el-button native-type="button" :loading="loadingSpots" @click="loadSpotList">刷新车位列表</el-button>
				</el-form-item>
			</el-form>
			<el-alert v-if="availSummary" :title="availSummaryTitle" :type="availSummary.available > 0 ? 'success' : 'warning'" show-icon :closable="false" class="chewei-yuyue-alert" />
			<div v-loading="loadingSpots || loadingAvail" class="spot-grid-wrap">
				<div v-if="!displaySpots.length && !loadingSpots" class="spot-empty">暂无车位数据，请先在管理端维护车位</div>
				<div v-for="spot in displaySpots" :key="spot.id" class="spot-card" :class="spotCardClass(spot)" @click="selectSpot(spot)">
					<div class="spot-code">{{ spot.cheweibianhao || '—' }}</div>
					<div class="spot-meta">区域：{{ spot.quyu || detail.quyu || '—' }}</div>
					<div class="spot-status">状态：{{ spot.zhuangtai || '—' }}</div>
					<div v-if="spotQueried" class="spot-avail-tag" :class="spot.keyuyue ? 'ok' : 'no'">{{ spot.keyuyue ? '该时段可约' : (spot.reason || '不可约') }}</div>
					<div v-else class="spot-avail-tag hint">请选择时段后查询可约状态</div>
				</div>
			</div>
			<div class="chewei-yuyue-actions" v-if="selectedSpot">
				<span class="selected-tip">已选：<b>{{ selectedSpot.cheweibianhao }}</b>（{{ selectedSpot.quyu }}）</span>
				<el-button type="success" native-type="button" :loading="loadingReserve" @click="submitReserve">确认预约</el-button>
				<el-button type="text" @click="selectedSpot = null">取消选择</el-button>
			</div>
		</div>
	</div>

	<!-- 这是我cursor给父亲写的 — P1-04 预约成功结果 -->
	<el-dialog title="预约成功" :visible.sync="reserveSuccessVisible" width="460px" :close-on-click-modal="false" class="reserve-success-dialog">
		<div class="reserve-success-body" v-if="reserveResult">
			<div class="reserve-success-icon"><i class="el-icon-success"></i></div>
			<p class="reserve-success-lead">您的时段预约已提交成功</p>
			<ul class="reserve-success-list">
				<li><span>预约单号</span><b>{{ reserveResult.yuyueId }}</b></li>
				<li><span>停车场</span><b>{{ reserveResult.tingchechangmingcheng }}</b></li>
				<li v-if="reserveResult.quyu"><span>区域</span><b>{{ reserveResult.quyu }}</b></li>
				<li v-if="reserveResult.cheweibianhao"><span>车位编号</span><b>{{ reserveResult.cheweibianhao }}</b></li>
				<li><span>预约时段</span><b>{{ reserveResult.kaishiShijian }} ~ {{ reserveResult.jieshuShijian }}</b></li>
			</ul>
		</div>
		<span slot="footer" class="reserve-success-footer">
			<el-button @click="closeReserveSuccess">继续浏览</el-button>
			<el-button type="primary" @click="goM2AfterReserve">前往入场（M2）</el-button>
		</span>
	</el-dialog>

	<div class="share_view" :style='{"boxShadow":"0 1px 6px rgba(0,0,0,.3)","position":"fixed","right":"0","bottom":"20%","background":"#fff","zIndex":"11"}'>
	</div>
</div>
</template>

<script>
  import CountDown from '@/components/CountDown';
  import axios from 'axios'
  import Swiper from "swiper";
  
  export default {
    //数据集合
    data() {
      return {
        tablename: 'cheweixinxi',
        baseUrl: '',
        breadcrumbItem: [
          {
            name: '车位信息'
          }
        ],
        title: '',
        detailBanner: [],
		userid: localStorage.getItem('frontUserid'),
		id: 0,
        detail: {},
        activeName: 'first',
        total: 1,
        pageSize: 5,
        totalPage: 1,
        buynumber: 1,
		centerType: false,
		shareUrl: location.href,
		// 这是我cursor给父亲写的 — P1-03 预约停车
		yuyueForm: {
			kaishiShijian: '',
			jieshuShijian: ''
		},
		spotList: [],
		availSpotList: [],
		spotQueried: false,
		availSummary: null,
		selectedSpot: null,
		loadingSpots: false,
		loadingAvail: false,
		loadingReserve: false,
		reserveSuccessVisible: false,
		reserveResult: null
      }
    },
	computed: {
		displaySpots() {
			if (this.spotQueried && this.availSpotList.length) {
				return this.availSpotList
			}
			return this.spotList
		},
		availSummaryTitle() {
			if (!this.availSummary) return ''
			return '总车位 ' + this.availSummary.total + '，该时段可预约 ' + this.availSummary.available + ' 个'
		},
		/** 这是我cursor给父亲写的 — P1-16 沿用原「停车」菜单权限展示预约入口 */
		showReserveParkingBtn() {
			if (typeof this.btnAuth !== 'function') {
				return true
			}
			return this.btnAuth('cheweixinxi', '停车')
		}
	},
    created() {
		if(this.$route.query.centerType) {
			this.centerType = true
		}
		
        this.init();
    },
	mounted() {
	},
    //方法集合
    methods: {
        init() {
		  this.id = this.$route.query.id
          this.baseUrl = this.$config.baseUrl;
          this.$http.get(this.tablename + '/detail/'  + this.id, {}).then(res => {
            if (res.data.code == 0) {
              this.detail = res.data.data;
				this.title = this.detail.tingchechangmingcheng;
				this.detailBanner = this.detail.cheweitupian ? this.detail.cheweitupian.split(",") : [];
				this.$forceUpdate();

				if(localStorage.getItem('frontToken')){
				}
				this.loadSpotList()
            }
          });
        },
		// 这是我cursor给父亲写的 — 加载车场下车位（P1-01 listByLot）
		loadSpotList() {
			if (!this.detail.id) return
			this.loadingSpots = true
			this.spotQueried = false
			this.availSummary = null
			this.availSpotList = []
			this.selectedSpot = null
			this.$http.get('chewei/listByLot', { params: { cheweixinxiId: this.detail.id } }).then(res => {
				if (res.data && res.data.code === 0) {
					this.spotList = (res.data.data && res.data.data.list) || []
				} else {
					this.$message.error((res.data && res.data.msg) || '加载车位失败')
				}
			}).catch(() => this.$message.error('加载车位失败')).finally(() => {
				this.loadingSpots = false
			})
		},
		// 这是我cursor给父亲写的 — 按时段查询每车位可约（P1-02 availabilityBySpot）
		querySpotAvailability() {
			if (!this.yuyueForm.kaishiShijian || !this.yuyueForm.jieshuShijian) {
				this.$message.warning('请选择开始与结束时间')
				return
			}
			this.loadingAvail = true
			this.selectedSpot = null
			this.$http.post('chewei/n4/availabilityBySpot', {
				cheweixinxiId: this.detail.id,
				kaishiShijian: this.yuyueForm.kaishiShijian,
				jieshuShijian: this.yuyueForm.jieshuShijian
			}).then(res => {
				if (res.data && res.data.code === 0) {
					const d = res.data.data || {}
					this.availSpotList = d.list || []
					this.availSummary = d
					this.spotQueried = true
					if ((d.available || 0) < 1) {
						this.$message.warning('该时段无可预约车位')
					}
				} else {
					this.handleM4Error(res.data)
				}
			}).catch(err => this.onHttpFail(err)).finally(() => {
				this.loadingAvail = false
			})
		},
		spotCardClass(spot) {
			const cls = []
			if (this.selectedSpot && this.selectedSpot.id === spot.id) cls.push('is-selected')
			if (this.spotQueried) {
				cls.push(spot.keyuyue ? 'is-available' : 'is-disabled')
			}
			return cls
		},
		selectSpot(spot) {
			if (!this.spotQueried) {
				this.$message.info('请先选择时段并查询可约车位')
				return
			}
			if (!spot.keyuyue) {
				this.$message.warning(spot.reason || '该车位当前时段不可预约')
				return
			}
			this.selectedSpot = spot
		},
		submitReserve() {
			if (!this.selectedSpot || !this.selectedSpot.id) {
				this.$message.warning('请先选择可预约车位')
				return
			}
			if (!this.yuyueForm.kaishiShijian || !this.yuyueForm.jieshuShijian) {
				this.$message.warning('请选择预约时段')
				return
			}
			const cheweiId = this.selectedSpot.id
			this.loadingReserve = true
			this.$http.post('chewei/n4/availability', {
				tingchechangmingcheng: this.detail.tingchechangmingcheng,
				quyu: this.detail.quyu,
				kaishiShijian: this.yuyueForm.kaishiShijian,
				jieshuShijian: this.yuyueForm.jieshuShijian
			}).then(res => {
				if (!(res.data && res.data.code === 0)) {
					this.handleM4Error(res.data)
					return Promise.reject()
				}
				const info = res.data.data
				if (info && info.available < 1) {
					this.$message.error('余位不足')
					return Promise.reject()
				}
				return this.$http.post('chewei/n4/reserve', {
					cheweiId: cheweiId,
					kaishiShijian: this.yuyueForm.kaishiShijian,
					jieshuShijian: this.yuyueForm.jieshuShijian
				})
			}).then(res => {
				if (res && res.data && res.data.code === 0) {
					const d = res.data.data || {}
					const y = d.yuyue
					if (y && y.id) {
						const pickedCode = this.selectedSpot && this.selectedSpot.cheweibianhao
						// 这是我cursor给父亲写的 — P1-04 预约成功结果展示
						this.reserveResult = {
							yuyueId: y.id,
							kaishiShijian: y.kaishiShijian || this.yuyueForm.kaishiShijian,
							jieshuShijian: y.jieshuShijian || this.yuyueForm.jieshuShijian,
							tingchechangmingcheng: y.tingchechangmingcheng || this.detail.tingchechangmingcheng,
							quyu: y.quyu || this.detail.quyu,
							cheweibianhao: (d.chewei && d.chewei.cheweibianhao) || pickedCode
						}
						this.reserveSuccessVisible = true
						this.selectedSpot = null
						this.$message.success('预约成功')
					} else {
						this.$message.success('预约成功')
					}
					this.loadSpotList()
				} else if (res) {
					this.handleM4Error(res.data)
				}
			}).catch(err => {
				if (err) this.onHttpFail(err)
			}).finally(() => {
				this.loadingReserve = false
			})
		},
		handleM4Error(data) {
			if (!data) {
				this.$message.error('操作失败')
				return
			}
			if (data.code === 4601 || data.m4Code === 'YUWEI_BUZU') {
				this.$message.error('余位不足')
				return
			}
			this.$message.error(data.msg || '操作失败')
		},
		onHttpFail(err) {
			const body = err && err.body
			if (body) {
				this.handleM4Error(body)
			} else {
				this.$message.error('请求失败')
			}
		},
		// 这是我cursor给父亲写的 — P1-16 滚动至本页预约区并高亮
		scrollToYuyue() {
			const el = document.getElementById('chewei-yuyue-panel')
			if (!el) {
				this.$message.warning('预约区域未就绪，请刷新页面后重试')
				return
			}
			el.scrollIntoView({ behavior: 'smooth', block: 'start' })
			el.classList.add('chewei-yuyue-panel--focus')
			window.setTimeout(() => el.classList.remove('chewei-yuyue-panel--focus'), 2200)
		},
		// 这是我cursor给父亲写的 — P1-04 跳转 M2 入场（真实 yuyueId）
		goM2AfterReserve() {
			if (!this.reserveResult || !this.reserveResult.yuyueId) {
				this.$message.warning('预约单信息无效')
				return
			}
			this.reserveSuccessVisible = false
			this.$router.push({
				path: '/index/m2TingcheLi',
				query: { yuyueId: String(this.reserveResult.yuyueId) }
			})
		},
		closeReserveSuccess() {
			this.reserveSuccessVisible = false
			this.reserveResult = null
		},
      curChange(page) {
        this.getDiscussList(page);
      },
      prevClick(page) {
        this.getDiscussList(page);
      },
      nextClick(page) {
        this.getDiscussList(page);
      },
		// 返回按钮
		backClick(){
			let params = {}
			if(this.centerType){
				params.centerType = 1
			}
			this.$router.push({path: '/index/cheweixinxi', query: params});
		},
		// 下载
		download(file){
			if(!file) {
				this.$message({
				  type: 'error',
				  message: '文件不存在',
				  duration: 1500,
				});
				return;
			}
		  let arr = file.replace(new RegExp('upload/', "g"), "")
		  axios.get(this.baseUrl + '/file/download?fileName=' + arr, {
		  	headers: {
		  		token: localStorage.getItem("frontToken")
		  	},
		  	responseType: "blob"
		  }).then(({
		  	data
		  }) => {
		  	const binaryData = [];
		  	binaryData.push(data);
		  	const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
		  		type: 'application/pdf;chartset=UTF-8'
		  	}))
		  	const a = document.createElement('a')
		  	a.href = objectUrl
		  	a.download = arr
		  	// a.click()
		  	// 下面这个写法兼容火狐
		  	a.dispatchEvent(new MouseEvent('click', {
		  		bubbles: true,
		  		cancelable: true,
		  		view: window
		  	}))
		  	window.URL.revokeObjectURL(data)
		  },err=>{
			  axios.get((location.href.split(this.$config.name).length>1 ? location.href.split(this.$config.name)[0] :'') + this.$config.name + '/file/download?fileName=' + arr, {
			  	headers: {
			  		token: localStorage.getItem("frontToken")
			  	},
			  	responseType: "blob"
			  }).then(({
			  	data
			  }) => {
			  	const binaryData = [];
			  	binaryData.push(data);
			  	const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
			  		type: 'application/pdf;chartset=UTF-8'
			  	}))
			  	const a = document.createElement('a')
			  	a.href = objectUrl
			  	a.download = arr
			  	// a.click()
			  	// 下面这个写法兼容火狐
			  	a.dispatchEvent(new MouseEvent('click', {
			  		bubbles: true,
			  		cancelable: true,
			  		view: window
			  	}))
			  	window.URL.revokeObjectURL(data)
			  })
		  })
      },


		// 权限判断
		btnAuth(tableName,key){
			if(this.centerType){
				return this.isBackAuth(tableName,key)
			}else{
				return this.isAuth(tableName,key)
			}
		},
		// 修改
		editClick(){
			this.$router.push(`/index/cheweixinxiAdd?type=edit&&id=${this.detail.id}`);
		},
		// 删除
		async delClick(){
			await this.$confirm('是否删除此车位信息？')
			  .then(_ => {
			    this.$http.post('cheweixinxi/delete', [this.detail.id]).then(async res => {
					if (res.data.code == 0) {
						this.$message({
							type: 'success',
							message: '删除成功!',
							duration: 1500,
							onClose: () => {
								history.back()
							}
						});
					}
			    });
			  }).catch(_ => {});
		},
    },
    components: {
      CountDown
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
	.editor ::v-deep  .avatar-uploader {
		height: 0;
		line-height: 0;
	}
	
	.detail-preview {
	
	  .attr {
	    .el-carousel ::v-deep  .el-carousel__indicator button {
	      width: 0;
	      height: 0;
	      display: none;
	    }
	
	    .el-input-number__decrease:hover:not(.is-disabled)~.el-input .el-input__inner:not(.is-disabled), .el-input-number__increase:hover:not(.is-disabled)~.el-input .el-input__inner:not(.is-disabled) {
	      border-color: none;
	    }
	  }
	
	  .detail {
	    & ::v-deep  .el-tabs__header .el-tabs__nav-wrap {
	      margin-bottom: 0;
	    }
	
	    & .add .el-textarea {
	      width: auto;
	    }
	  }
	}
	
	.attr .el-carousel ::v-deep  .el-carousel__container .el-carousel__arrow--left {
		width: 36px;
		font-size: 12px;
		height: 36px;
	}
	
	.attr .el-carousel ::v-deep  .el-carousel__container .el-carousel__arrow--left:hover {
		background: red;
	}
	
	.attr .el-carousel ::v-deep  .el-carousel__container .el-carousel__arrow--right {
		width: 36px;
		font-size: 12px;
		height: 36px;
	}
	
	.attr .el-carousel ::v-deep  .el-carousel__container .el-carousel__arrow--right:hover {
		background: red;
	}

	.attr .el-carousel ::v-deep  .el-carousel__indicators {
		padding: 0;
		margin: 0;
		z-index: 2;
		position: absolute;
		list-style: none;
	}
	
	.attr .el-carousel ::v-deep  .el-carousel__indicators li {
		padding: 0;
		margin: 0 4px;
		background: #fff;
		display: inline-block;
		width: 12px;
		opacity: 0.4;
		transition: 0.3s;
		height: 12px;
	}
	
	.attr .el-carousel ::v-deep  .el-carousel__indicators li:hover {
		padding: 0;
		margin: 0 4px;
		background: #fff;
		display: inline-block;
		width: 24px;
		opacity: 0.7;
		height: 12px;
	}
	
	.attr .el-carousel ::v-deep  .el-carousel__indicators li.is-active {
		padding: 0;
		margin: 0 4px;
		background: #fff;
		display: inline-block;
		width: 24px;
		opacity: 1;
		height: 12px;
	}
	
	.attr .el-input-number ::v-deep  .el-input-number__decrease {
		cursor: pointer;
		z-index: 1;
		display: flex;
		border-color: #DCDFE6;
		border-radius: 4px 0 0 4px;
		top: 1px;
		left: 1px;
		background: #f5f5f5;
		width: 40px;
		justify-content: center;
		border-width: 0 1px 0 0;
		align-items: center;
		position: absolute;
		border-style: solid;
		text-align: center;
		height: 38px;
	}
	
	.attr .el-input-number ::v-deep  .el-input-number__decrease i {
		color: #666;
		font-size: 14px;
	}

	.attr .el-input-number ::v-deep  .el-input-number__increase {
		cursor: pointer;
		z-index: 1;
		display: flex;
		border-color: #DCDFE6;
		right: 1px;
		border-radius: 0 4px 4px 0;
		top: 1px;
		background: #f5f5f5;
		width: 40px;
		justify-content: center;
		border-width: 0 0 0 1px;
		align-items: center;
		position: absolute;
		border-style: solid;
		text-align: center;
		height: 38px;
	}
	
	.attr .el-input-number ::v-deep  .el-input-number__increase i {
		color: #666;
		font-size: 14px;
	}
	
	.attr .el-input-number ::v-deep  .el-input .el-input__inner {
		border: 1px solid #DCDFE6;
		border-radius: 4px;
		padding: 0 40px;
		outline: none;
		color: #666;
		background: #FFF;
		display: inline-block;
		width: 100%;
		font-size: 14px;
		line-height: 40px;
		text-align: center;
		height: 40px;
	}
	
	.detail-preview .detail.el-tabs ::v-deep  .el-tabs__header {
		margin: 10px 0;
		background: none;
		border-color: #E4E7ED;
		border-width: 0;
		border-style: solid;
	}
	
	.detail-preview .detail.el-tabs ::v-deep  .el-tabs__header .el-tabs__item {
		border: 0;
		padding: 0 40px;
		margin: 0;
		color: #000;
		background: transparent;
		font-weight: bold;
		display: inline-block;
		font-size: 20px;
		line-height: 60px;
		position: relative;
		list-style: none;
		height: 60px;
	}
	
	.detail-preview .detail.el-tabs ::v-deep  .el-tabs__header .el-tabs__item:hover {
		border: 0;
		padding: 0 40px;
		color: #57A7A5;
		background: none;
		font-weight: bold;
		font-size: 20px;
	}
	
	.detail-preview .detail.el-tabs ::v-deep  .el-tabs__header .el-tabs__item.is-active {
		border: 0;
		padding: 0 40px;
		color: #57A7A5;
		background: none;
		font-weight: bold;
		font-size: 20px;
	}
	
	.detail-preview .detail.el-tabs ::v-deep  .el-tabs__content {
		padding: 15px;
	}
	
	.detail-preview .detail.el-tabs .add ::v-deep  .el-form-item__label {
		padding: 0 10px 0 0;
		color: #666;
		display: none;
		width: 80px;
		font-size: 14px;
		line-height: 40px;
		text-align: right;
	}
	
	.detail-preview .detail.el-tabs .add ::v-deep  .el-textarea__inner {
	}
	
	.breadcrumb-preview .el-breadcrumb ::v-deep  .el-breadcrumb__separator {
		margin: 0 19px;
		color: #000;
		font-weight: 500;
	}
	
	.breadcrumb-preview .el-breadcrumb .item1 ::v-deep  .el-breadcrumb__inner a {
		color: #000;
		display: inline-block;
	}
	
	.breadcrumb-preview .el-breadcrumb .item2 ::v-deep  .el-breadcrumb__inner a {
		color: #000;
		display: inline-block;
	}
		
	.breadcrumb-preview .el-breadcrumb .item3 ::v-deep  .el-breadcrumb__inner a {
		color: #000;
		display: inline-block;
	}
	
	#pagination.el-pagination ::v-deep  .el-pagination__total {
		margin: 0 10px 0 0;
		color: #666;
		font-weight: 400;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .btn-prev {
		border: none;
		border-radius: 2px;
		padding: 0;
		margin: 0 5px;
		color: #57A7A5;
		background: none;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		min-width: 35px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .btn-next {
		border: none;
		border-radius: 2px;
		padding: 0;
		margin: 0 5px;
		color: #57A7A5;
		background: none;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		min-width: 35px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .btn-prev:disabled {
		border: none;
		cursor: not-allowed;
		border-radius: 2px;
		padding: 0;
		margin: 0 5px;
		color: #57A7A5;
		background: none;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .btn-next:disabled {
		border: none;
		cursor: not-allowed;
		border-radius: 2px;
		padding: 0;
		margin: 0 5px;
		color: #57A7A5;
		background: none;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .el-pager {
		padding: 0;
		margin: 0;
		display: inline-block;
		vertical-align: top;
	}
	
	#pagination.el-pagination ::v-deep  .el-pager .number {
		cursor: pointer;
		border: none;
		padding: 0 4px;
		margin: 0 5px;
		color: #000;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		border-radius: 2px;
		background: none;
		text-align: center;
		min-width: 30px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .el-pager .number:hover {
		cursor: pointer;
		padding: 0 4px;
		margin: 0 5px;
		color: #57A7A5;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		border-radius: 2px;
		background: none;
		text-align: center;
		min-width: 30px;
		height: 28px;
}

#pagination.el-pagination ::v-deep  .el-pager .number.active {
		cursor: default;
		padding: 0 4px;
		margin: 0 5px;
		color: #57A7A5;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		border-radius: 2px;
		background: none;
		text-align: center;
		min-width: 30px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .el-pagination__sizes {
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .el-pagination__sizes .el-input {
		margin: 0 5px;
		width: 100px;
		position: relative;
	}
	
	#pagination.el-pagination ::v-deep  .el-pagination__sizes .el-input .el-input__inner {
		border: 0px solid #DCDFE6;
		cursor: pointer;
		border-radius: 3px;
		padding: 0 25px 0 8px;
		color: #606266;
		background: none;
		display: inline-block;
		width: 100%;
		font-size: 13px;
		line-height: 28px;
		text-align: center;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .el-pagination__sizes .el-input span.el-input__suffix {
		top: 0;
		position: absolute;
		right: 0;
		height: 100%;
	}
	
	#pagination.el-pagination ::v-deep  .el-pagination__sizes .el-input .el-input__suffix .el-select__caret {
		cursor: pointer;
		color: #C0C4CC;
		width: 25px;
		font-size: 14px;
		line-height: 28px;
		text-align: center;
	}

	#pagination.el-pagination ::v-deep  .el-pagination__jump {
		margin: 0 0 0 24px;
		color: #606266;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .el-pagination__jump .el-input {
		border-radius: 3px;
		padding: 0 2px;
		margin: 0 2px;
		display: inline-block;
		width: 50px;
		font-size: 14px;
		line-height: 18px;
		position: relative;
		text-align: center;
		height: 28px;
	}
	
	#pagination.el-pagination ::v-deep  .el-pagination__jump .el-input .el-input__inner {
		border: 1px solid #DCDFE6;
		cursor: pointer;
		padding: 0 3px;
		color: #606266;
		display: inline-block;
		font-size: 14px;
		line-height: 28px;
		border-radius: 3px;
		outline: 0;
		background: #FFF;
		width: 100%;
		text-align: center;
		height: 28px;
	}
	.share_view{
		position: fixed;
		right:0;
		bottom: 20%;
		background: #fff;
		box-shadow: 0 4px 6px rgba(0,0,0,.1);
		.share{
			width: 40px;
			height: 40px;
			display: flex;
			align-items: center;
			justify-content: center;
			border-bottom: 1px solid #eee;
			cursor: pointer;
		}
		.share:last-of-type{
			border:none;
		}
	}


	.detail-preview .el-rate ::v-deep  .el-rate__item {
				cursor: pointer;
				display: inline-block;
				vertical-align: middle;
				font-size: 0;
				position: relative;
			}
	
	.detail-preview .el-rate ::v-deep  .el-rate__item .el-rate__icon {
				margin: 0 3px;
				display: block;
				font-size: 18px;
				position: relative;
				transition: .3s;
			}

/* 这是我cursor给父亲写的 — P1-03/P1-16 预约停车区域 */
.chewei-yuyue-panel {
	width: 80%;
	margin: 0 auto 40px;
	scroll-margin-top: 72px;
	transition: box-shadow 0.35s ease;
}
.chewei-yuyue-panel--focus .chewei-yuyue-inner {
	box-shadow: 0 0 0 3px rgba(8, 145, 178, 0.45), 0 8px 28px rgba(6, 182, 212, 0.18);
}
.chewei-yuyue-inner {
	padding: 24px 28px;
	background: rgba(255, 255, 255, 0.96);
	border: 1px solid rgba(6, 182, 212, 0.3);
	border-radius: 14px;
	box-shadow: 0 8px 28px rgba(6, 182, 212, 0.1);
}
.chewei-yuyue-title {
	margin: 0 0 8px;
	font-size: 20px;
	font-weight: 600;
	color: #0e7490;
}
.chewei-yuyue-desc {
	margin: 0 0 20px;
	font-size: 14px;
	color: #64748b;
	line-height: 1.6;
}
.chewei-yuyue-alert {
	margin-bottom: 16px;
}
.spot-grid-wrap {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
	gap: 14px;
	min-height: 80px;
}
.spot-empty {
	grid-column: 1 / -1;
	text-align: center;
	color: #94a3b8;
	padding: 24px;
}
.spot-card {
	padding: 14px 16px;
	border-radius: 10px;
	border: 1px solid #e2e8f0;
	background: #f8fafc;
	cursor: pointer;
	transition: all 0.25s;
}
.spot-card:hover {
	border-color: #06b6d4;
	box-shadow: 0 4px 12px rgba(6, 182, 212, 0.15);
}
.spot-card.is-selected {
	border-color: #06b6d4;
	background: #ecfeff;
	box-shadow: 0 0 0 2px rgba(6, 182, 212, 0.35);
}
.spot-card.is-disabled {
	opacity: 0.65;
	cursor: not-allowed;
}
.spot-card.is-available:not(.is-selected):hover {
	transform: translateY(-2px);
}
.spot-code {
	font-size: 16px;
	font-weight: 600;
	color: #1e293b;
	margin-bottom: 6px;
}
.spot-meta,
.spot-status {
	font-size: 13px;
	color: #64748b;
	line-height: 1.5;
}
.spot-avail-tag {
	margin-top: 10px;
	font-size: 12px;
	padding: 4px 8px;
	border-radius: 6px;
	display: inline-block;
}
.spot-avail-tag.ok {
	background: rgba(16, 185, 129, 0.12);
	color: #059669;
}
.spot-avail-tag.no {
	background: rgba(239, 68, 68, 0.1);
	color: #dc2626;
}
.spot-avail-tag.hint {
	background: rgba(100, 116, 139, 0.1);
	color: #64748b;
}
.chewei-yuyue-actions {
	margin-top: 20px;
	padding-top: 16px;
	border-top: 1px solid #e2e8f0;
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 12px;
}
.selected-tip {
	font-size: 14px;
	color: #334155;
}

/* 这是我cursor给父亲写的 — P1-04 预约成功弹窗 */
.reserve-success-body {
	text-align: center;
	padding: 8px 4px 0;
}
.reserve-success-icon {
	font-size: 52px;
	color: #10b981;
	margin-bottom: 12px;
}
.reserve-success-lead {
	margin: 0 0 16px;
	font-size: 16px;
	color: #1e293b;
}
.reserve-success-list {
	list-style: none;
	margin: 0;
	padding: 0;
	text-align: left;
	background: #f8fafc;
	border-radius: 10px;
	border: 1px solid #e2e8f0;
}
.reserve-success-list li {
	display: flex;
	justify-content: space-between;
	gap: 12px;
	padding: 10px 14px;
	font-size: 14px;
	border-bottom: 1px solid #e2e8f0;
}
.reserve-success-list li:last-child {
	border-bottom: none;
}
.reserve-success-list span {
	color: #64748b;
	flex-shrink: 0;
}
.reserve-success-list b {
	color: #0e7490;
	font-weight: 600;
	text-align: right;
	word-break: break-all;
}
</style>

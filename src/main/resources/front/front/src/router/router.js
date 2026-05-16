import VueRouter from 'vue-router'

//引入组件
import Index from '../pages'
import Home from '../pages/home/home'
import Login from '../pages/login/login'
import Register from '../pages/register/register'
import Center from '../pages/center/center'
import Messages from '../pages/messages/list'
import payList from '../pages/pay'

import yonghuList from '../pages/yonghu/list'
import yonghuDetail from '../pages/yonghu/detail'
import yonghuAdd from '../pages/yonghu/add'
import cheweixinxiList from '../pages/cheweixinxi/list'
import cheweixinxiDetail from '../pages/cheweixinxi/detail'
import cheweixinxiAdd from '../pages/cheweixinxi/add'
import chezijinchangList from '../pages/chezijinchang/list'
import chezijinchangDetail from '../pages/chezijinchang/detail'
import chezijinchangAdd from '../pages/chezijinchang/add'
import tingchejiaofeiList from '../pages/tingchejiaofei/list'
import tingchejiaofeiDetail from '../pages/tingchejiaofei/detail'
import tingchejiaofeiAdd from '../pages/tingchejiaofei/add'
import m2TingcheLi from '../pages/m2TingcheLi/index'
// 这是我cursor给父亲写的 — N6 超时策略
import n6Chaoshi from '../pages/n6Chaoshi/index'
// 这是我cursor给父亲写的 — N7 补缴
import n7Bujiao from '../pages/n7Bujiao/index'
// 这是我cursor给父亲写的 — M4 预约校验
import m4Yuyue from '../pages/m4Yuyue/index'
// 这是我cursor给父亲写的 — M5 费用试算
import m5Jifei from '../pages/m5Jifei/index'

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
	return originalPush.call(this, location).catch(err => err)
}

//配置路由
export default new VueRouter({
	routes:[
		{
      path: '/',
      redirect: '/index/home'
    },
		{
			path: '/index',
			component: Index,
			children:[
				{
					path: 'home',
					component: Home
				},
				{
					path: 'center',
					component: Center,
				},
				{
					path: 'pay',
					component: payList,
				},
				{
					path: 'messages',
					component: Messages
				},
				{
					path: 'yonghu',
					component: yonghuList
				},
				{
					path: 'yonghuDetail',
					component: yonghuDetail
				},
				{
					path: 'yonghuAdd',
					component: yonghuAdd
				},
				{
					path: 'cheweixinxi',
					component: cheweixinxiList
				},
				{
					path: 'cheweixinxiDetail',
					component: cheweixinxiDetail
				},
				{
					path: 'cheweixinxiAdd',
					component: cheweixinxiAdd
				},
				{
					path: 'chezijinchang',
					component: chezijinchangList
				},
				{
					path: 'chezijinchangDetail',
					component: chezijinchangDetail
				},
				{
					path: 'chezijinchangAdd',
					component: chezijinchangAdd
				},
				{
					path: 'tingchejiaofei',
					component: tingchejiaofeiList
				},
				{
					path: 'tingchejiaofeiDetail',
					component: tingchejiaofeiDetail
				},
				{
					path: 'tingchejiaofeiAdd',
					component: tingchejiaofeiAdd
				},
				{
					path: 'm2TingcheLi',
					component: m2TingcheLi
				},
				{
					path: 'n6Chaoshi',
					component: n6Chaoshi
				},
				{
					path: 'n7Bujiao',
					component: n7Bujiao
				},
				{
					path: 'm4Yuyue',
					component: m4Yuyue
				},
				{
					path: 'm5Jifei',
					component: m5Jifei
				},
			]
		},
		{
			path: '/login',
			component: Login
		},
		{
			path: '/register',
			component: Register
		},
	]
})

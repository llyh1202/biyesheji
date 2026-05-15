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

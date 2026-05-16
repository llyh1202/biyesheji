import Vue from 'vue';
//配置路由
import VueRouter from 'vue-router'
Vue.use(VueRouter);
//1.创建组件
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import pay from '@/views/pay'
import register from '@/views/register'
import center from '@/views/center'
    import cheweixinxi from '@/views/modules/cheweixinxi/list'
    // 这是我cursor给父亲写的 — N1 车位主数据
    import chewei from '@/views/modules/chewei/list'
    // 这是我cursor给父亲写的 — N6 超时策略
    import cheweichaoshiguize from '@/views/modules/cheweichaoshiguize/list'
    // 这是我cursor给父亲写的 — M5 计费规则
    import tingchejifeiguize from '@/views/modules/tingchejifeiguize/list'
    // 这是我cursor给父亲写的 — N7 补缴单
    import tingchebujiao from '@/views/modules/tingchebujiao/list'
    // 这是我cursor给父亲写的 — N8 运营总览
    import yunyingkanban from '@/views/modules/yunyingkanban/index'
    // 这是我cursor给父亲写的 — N9 多维报表
    import duoweibaobiao from '@/views/modules/duoweibaobiao/index'
    // 这是我cursor给父亲写的 — N10 异常报表
    import yichangbaobiao from '@/views/modules/yichangbaobiao/index'
    import chezijinchang from '@/views/modules/chezijinchang/list'
    import yonghu from '@/views/modules/yonghu/list'
    import tingchejiaofei from '@/views/modules/tingchejiaofei/list'
    import messages from '@/views/modules/messages/list'
    import config from '@/views/modules/config/list'


//2.配置路由   注意：名字
export const routes = [{
    path: '/',
    name: '系统首页',
    component: Index,
    children: [{
      // 这里不设置值，是把main作为默认页面
      path: '/',
      name: '系统首页',
      component: Home,
      meta: {icon:'', title:'center', affix: true}
    }, {
      path: '/yunyingkanban',
      name: '运营总览',
      component: yunyingkanban
    }, {
      path: '/duoweibaobiao',
      name: '多维统计',
      component: duoweibaobiao
    }, {
      path: '/yichangbaobiao',
      name: '异常报表',
      component: yichangbaobiao
    }, {
      path: '/updatePassword',
      name: '修改密码',
      component: UpdatePassword,
      meta: {icon:'', title:'updatePassword'}
    }, {
      path: '/pay',
      name: '支付',
      component: pay,
      meta: {icon:'', title:'pay'}
    }, {
      path: '/center',
      name: '个人信息',
      component: center,
      meta: {icon:'', title:'center'}
    }
      ,{
	path: '/cheweixinxi',
        name: '车位信息',
        component: cheweixinxi
      }
      /* 这是我cursor给父亲写的 — N1 路由 /chewei */
      ,{
	path: '/chewei',
        name: '车位主数据',
        component: chewei
      }
      ,{
	path: '/cheweichaoshiguize',
        name: '超时策略',
        component: cheweichaoshiguize
      }
      ,{
	path: '/tingchejifeiguize',
        name: '计费规则',
        component: tingchejifeiguize
      }
      ,{
	path: '/tingchebujiao',
        name: '补缴管理',
        component: tingchebujiao
      }
      ,{
	path: '/chezijinchang',
        name: '车子进场',
        component: chezijinchang
      }
      ,{
	path: '/yonghu',
        name: '用户',
        component: yonghu
      }
      ,{
	path: '/tingchejiaofei',
        name: '停车缴费',
        component: tingchejiaofei
      }
      ,{
	path: '/messages',
        name: '用户反馈',
        component: messages
      }
      ,{
	path: '/config',
        name: '轮播图管理',
        component: config
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {icon:'', title:'login'}
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: {icon:'', title:'register'}
  },
  {
    path: '*',
    component: NotFound
  }
]
//3.实例化VueRouter  注意：名字
const router = new VueRouter({
  mode: 'hash',
  /*hash模式改为history*/
  routes // （缩写）相当于 routes: routes
})
const originalPush = VueRouter.prototype.push
//修改原型对象中的push方法
VueRouter.prototype.push = function push(location) {
   return originalPush.call(this, location).catch(err => err)
}
export default router;

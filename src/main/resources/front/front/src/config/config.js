// 这是我cursor给父亲写的 — P1-09/P1-21 顶部导航（首项「我的停车」）；P1-17 隐藏易误导入口
export default {
    baseUrl: 'http://localhost:8080/springboot673i609q/',
	name: '/springboot673i609q',
    /**
     * 用户端顶栏菜单：hidden 为 true 的项路由仍注册，但不展示（勿删路由配置）。
     * 普通用户停车请走：我的停车 → M2；预约请走：车位信息详情预约区。
     */
    indexNav: [
        {
            name: '我的停车',
            url: '/index/wodeTingche',
            homeHighlight: true
        },
        {
            name: '车位信息',
            url: '/index/cheweixinxi'
        },
        {
            name: '用户反馈',
            url: '/index/messages'
        },
        {
            name: '停车闭环',
            url: '/index/m2TingcheLi',
            hidden: true
        },
        {
            name: '时段预约',
            url: '/index/m4Yuyue',
            hidden: true
        },
        {
            name: '费用试算',
            url: '/index/m5Jifei',
            hidden: true
        },
        {
            name: '超时策略',
            url: '/index/n6Chaoshi',
            hidden: true
        },
        {
            name: '超时补缴',
            url: '/index/n7Bujiao',
            hidden: true
        },
        {
            name: '车子进场',
            url: '/index/chezijinchang',
            hidden: true
        },
        {
            name: '车子进场新增',
            url: '/index/chezijinchangAdd',
            hidden: true
        },
        {
            name: '停车缴费',
            url: '/index/tingchejiaofei',
            hidden: true
        }
    ]
}

/** 这是我cursor给父亲写的 — P1-17 顶栏仅展示未隐藏的导航项 */
export function getVisibleIndexNav(nav) {
    const list = nav || []
    return list.filter(item => item && !item.hidden)
}

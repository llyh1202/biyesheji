const base = {
    get() {
        return {
            url : "http://localhost:8080/springboot673i609q/",
            name: "springboot673i609q",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/springboot673i609q/front/dist/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "基于spring Boot+vue的停车场管理系统"
        } 
    }
}
export default base

/**
 * 这是我cursor给父亲写的 — P1-10 用户端 frontToken 登录校验（与 main.js Token 头一致）
 */

/** 未登录时提示并跳转登录页，已登录返回 true */
export function requireFrontLogin(vm) {
	if (localStorage.getItem('frontToken')) {
		return true
	}
	if (vm && vm.$message) {
		vm.$message.warning('请先登录')
	}
	if (vm && vm.$router) {
		vm.$router.push('/login').catch(() => {})
	}
	return false
}

/** 接口返回 401/403 时处理（与全局拦截器一致，补充页面内 catch） */
export function handleAuthFail(vm, dataOrErr) {
	const body = dataOrErr && dataOrErr.body ? dataOrErr.body : dataOrErr
	const code = body && body.code
	if (code === 401 || code === 403) {
		if (vm && vm.$message) {
			vm.$message.warning((body && body.msg) || '请先登录')
		}
		if (vm && vm.$router) {
			vm.$router.push('/login').catch(() => {})
		}
		return true
	}
	return false
}

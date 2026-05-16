/**
 * 这是我cursor给父亲写的 — P1-10 用户端 frontToken 登录校验（与 main.js Token 头一致）
 * P1-15 M2 入场表单从 localStorage / session 接口补全用户信息
 */

/** 从 localStorage 读取 sessionForm（index 页 getSession 写入） */
export function readSessionFormUser() {
	try {
		const raw = localStorage.getItem('sessionForm')
		if (!raw) return null
		return JSON.parse(raw)
	} catch (e) {
		return null
	}
}

/** 登录后拉取最新用户资料并写回 localStorage */
export function fetchFrontUserSession(vm) {
	if (!localStorage.getItem('frontToken') || !vm || !vm.$http) {
		return Promise.resolve(readSessionFormUser())
	}
	const table = localStorage.getItem('UserTableName') || localStorage.getItem('frontSessionTable') || 'yonghu'
	return vm.$http.get(table + '/session', { emulateJSON: true }).then(res => {
		if (res.data && res.data.code === 0 && res.data.data) {
			const data = res.data.data
			localStorage.setItem('sessionForm', JSON.stringify(data))
			if (data.id != null) {
				localStorage.setItem('frontUserid', String(data.id))
			}
			if (data.yonghuzhanghao) {
				localStorage.setItem('username', data.yonghuzhanghao)
			}
			return data
		}
		return readSessionFormUser()
	}).catch(() => readSessionFormUser())
}

/**
 * 这是我cursor给父亲写的 — P1-15 将用户资料写入入场表单（onlyEmpty 为 true 时不覆盖已填项）
 */
export function fillRuchangUserFields(form, user, onlyEmpty) {
	if (!form) return
	const keep = onlyEmpty !== false
	const assign = (key, val) => {
		if (val === undefined || val === null) return
		const s = String(val).trim()
		if (!s) return
		if (keep && String(form[key] || '').trim()) return
		form[key] = s
	}
	const u = user || {}
	assign('yonghuzhanghao', u.yonghuzhanghao || localStorage.getItem('username'))
	assign('xingming', u.xingming)
	assign('shouji', u.shouji)
	assign('chepaihao', u.chepaihao)
}

/** 这是我cursor给父亲写的 — P1-15 M2 入场：先 sessionForm，必要时请求 /session */
export function autofillM2RuchangForm(vm, form, onlyEmpty) {
	if (!form) return Promise.resolve()
	let user = readSessionFormUser()
	const hasProfile = user && (user.xingming || user.shouji || user.chepaihao)
	if (hasProfile) {
		fillRuchangUserFields(form, user, onlyEmpty)
		return Promise.resolve(user)
	}
	return fetchFrontUserSession(vm).then(fresh => {
		fillRuchangUserFields(form, fresh || user, onlyEmpty)
		return fresh || user
	})
}

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

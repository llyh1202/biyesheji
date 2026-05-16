/**
 * 这是我cursor给父亲写的 — P1-18 用户端停车费支付路径校验
 */

export function isTingchejiaofeiPayTable(table) {
	return table === 'tingchejiaofei'
}

/** 须已离场（有离场时间）才允许支付停车费 */
export function hasLichangForPay(order) {
	if (!order) return false
	const t = order.lichangshijian
	return t !== null && t !== undefined && String(t).trim() !== ''
}

export function isParkingFeeUnpaid(order) {
	if (!order) return false
	const s = order.ispay
	return s !== '已支付'
}

export function readPayLocalObject() {
	try {
		const raw = localStorage.getItem('payObject')
		if (!raw) return null
		return JSON.parse(raw)
	} catch (e) {
		return null
	}
}

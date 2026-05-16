<template>
	<!-- 这是我cursor给父亲写的 — N10 异常报表 -->
	<div class="main-content" :style='{"padding":"24px","background":"#f5f7fa","minHeight":"100vh"}'>
		<div :style='{"display":"flex","justifyContent":"space-between","alignItems":"center","marginBottom":"16px"}'>
			<div>
				<h2 :style='{"margin":"0","fontSize":"20px"}'>异常报表（N10）</h2>
				<p :style='{"margin":"6px 0 0","color":"#909399","fontSize":"13px"}'>快照：{{ report.snapshotAt || '—' }}（与 N6 超时 / N7 补缴联动处理）</p>
			</div>
			<div>
				<el-button type="warning" :loading="scanning" @click="runN6Scan">执行 N6 超时扫描</el-button>
				<el-button type="primary" :loading="loading" @click="loadReport">刷新</el-button>
			</div>
		</div>

		<el-row :gutter="12" :style='{"marginBottom":"16px"}'>
			<el-col :span="4"><el-card shadow="hover"><div class="n">未入场预约</div><div class="v">{{ report.weiRuchangCount }}</div></el-card></el-col>
			<el-col :span="4"><el-card shadow="hover"><div class="n">未支付离场</div><div class="v warn">{{ report.weiZhifuLichangCount }}</div></el-card></el-col>
			<el-col :span="4"><el-card shadow="hover"><div class="n">待支付补缴</div><div class="v warn">{{ report.daiBujiaoCount }}</div></el-card></el-col>
			<el-col :span="4"><el-card shadow="hover"><div class="n">超时未续费</div><div class="v warn">{{ report.chaoshiWeiXufeiCount }}</div></el-card></el-col>
			<el-col :span="4"><el-card shadow="hover"><div class="n">违约待付(N6)</div><div class="v">{{ report.weiyueWeiRuchangCount }}</div></el-card></el-col>
		</el-row>

		<el-tabs v-model="activeTab" type="border-card">
			<el-tab-pane label="未入场预约" name="weiRuchang">
				<yichang-table :rows="report.weiRuchangList" />
			</el-tab-pane>
			<el-tab-pane label="未支付离场" name="weiZhifu">
				<yichang-table :rows="report.weiZhifuLichangList" />
			</el-tab-pane>
			<el-tab-pane label="待支付补缴(N7)" name="bujiao">
				<yichang-table :rows="report.daiBujiaoList" />
			</el-tab-pane>
			<el-tab-pane label="超时未续费" name="xufei">
				<yichang-table :rows="report.chaoshiWeiXufeiList" />
			</el-tab-pane>
			<el-tab-pane label="违约待付(N6)" name="weiyue">
				<yichang-table :rows="report.weiyueWeiRuchangList" />
			</el-tab-pane>
		</el-tabs>
	</div>
</template>

<script>
const YichangTable = {
	props: { rows: { type: Array, default: () => [] } },
	template: `
		<el-table :data="rows" border stripe size="small" v-loading="false">
			<el-table-column prop="bizId" label="业务ID" width="70" />
			<el-table-column prop="tingchechangmingcheng" label="停车场" min-width="110" />
			<el-table-column prop="quyu" label="区域" width="80" />
			<el-table-column prop="chepaihao" label="车牌" width="90" />
			<el-table-column prop="yonghuzhanghao" label="用户" width="90" />
			<el-table-column prop="jine" label="金额" width="70" />
			<el-table-column prop="shijian" label="关键时间" width="150" />
			<el-table-column prop="liuchengJiedian" label="节点/类型" width="120" show-overflow-tooltip />
			<el-table-column prop="chuliJianyi" label="处理建议" min-width="200" show-overflow-tooltip />
		</el-table>
	`
}

export default {
	components: { YichangTable },
	data() {
		return {
			loading: false,
			scanning: false,
			activeTab: 'weiRuchang',
			report: {
				snapshotAt: '',
				weiRuchangCount: 0,
				weiZhifuLichangCount: 0,
				daiBujiaoCount: 0,
				chaoshiWeiXufeiCount: 0,
				weiyueWeiRuchangCount: 0,
				weiRuchangList: [],
				weiZhifuLichangList: [],
				daiBujiaoList: [],
				chaoshiWeiXufeiList: [],
				weiyueWeiRuchangList: []
			}
		}
	},
	created() {
		this.loadReport()
	},
	methods: {
		loadReport() {
			this.loading = true
			this.$http.get('chewei/n10/yichang/report').then(({ data }) => {
				if (data && data.code === 0) {
					this.report = data.data || this.report
				} else {
					this.$message.error((data && data.msg) || '加载失败')
				}
			}).finally(() => { this.loading = false })
		},
		runN6Scan() {
			this.scanning = true
			this.$http.get('chewei/n6/timeout/run').then(({ data }) => {
				if (data && data.code === 0) {
					const d = data.data || {}
					this.$message.success('N6 扫描完成：取消 ' + (d.cancelled || 0) + ' 条，违约单 ' + (d.penaltyOrders || 0) + ' 条')
					this.loadReport()
				} else {
					this.$message.error((data && data.msg) || '扫描失败')
				}
			}).finally(() => { this.scanning = false })
		}
	}
}
</script>

<style scoped>
.n { font-size: 13px; color: #909399; }
.v { font-size: 22px; font-weight: bold; margin-top: 6px; color: #303133; }
.v.warn { color: #E6A23C; }
</style>

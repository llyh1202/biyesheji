<template>
	<!-- 这是我cursor给父亲写的 — N8 运营总览看板 -->
	<div class="main-content" :style='{"padding":"24px","background":"#f5f7fa","minHeight":"100vh"}'>
		<div :style='{"display":"flex","justifyContent":"space-between","alignItems":"center","marginBottom":"16px"}'>
			<div>
				<h2 :style='{"margin":"0","fontSize":"20px"}'>运营总览看板（N8）</h2>
				<p :style='{"margin":"6px 0 0","color":"#909399","fontSize":"13px"}'>统计时点：{{ overview.snapshotAt || '—' }}</p>
			</div>
			<el-button type="primary" :loading="loading" icon="el-icon-refresh" @click="loadData">刷新</el-button>
		</div>

		<el-row :gutter="16" :style='{"marginBottom":"16px"}'>
			<el-col :span="8">
				<el-card shadow="hover">
					<div class="kpi-title">车位利用率（当前）</div>
					<div class="kpi-value primary">{{ overview.utilizationRate || 0 }}%</div>
					<div class="kpi-sub">占用 {{ overview.cheweiOccupied || 0 }} / 共 {{ overview.cheweiTotal || 0 }}</div>
				</el-card>
			</el-col>
		</el-row>

		<el-table v-loading="loading" :data="tableRows" border stripe :style='{"background":"#fff"}'>
			<el-table-column prop="label" label="指标" width="160" fixed />
			<el-table-column label="今日" align="center">
				<template slot-scope="s">
					<span :class="s.row.highlight ? 'num-warn' : ''">{{ formatCell(s.row.today, s.row.type) }}</span>
				</template>
			</el-table-column>
			<el-table-column label="本周（周一至当前）" align="center">
				<template slot-scope="s">
					<span>{{ formatCell(s.row.week, s.row.type) }}</span>
				</template>
			</el-table-column>
		</el-table>

		<p :style='{"marginTop":"12px","color":"#909399","fontSize":"12px"}'>
			说明：收入=已支付停车缴费（按离场时间）+ 已支付补缴单；超时笔数=预约取消数 + 超时补缴单创建数。
		</p>
	</div>
</template>

<script>
export default {
	data() {
		return {
			loading: false,
			overview: {
				snapshotAt: '',
				today: {},
				week: {},
				cheweiTotal: 0,
				cheweiOccupied: 0,
				utilizationRate: 0
			}
		}
	},
	computed: {
		tableRows() {
			const t = this.overview.today || {}
			const w = this.overview.week || {}
			return [
				{ label: '预约量', today: t.yuyueCount, week: w.yuyueCount, type: 'int' },
				{ label: '入场量', today: t.ruchangCount, week: w.ruchangCount, type: 'int' },
				{ label: '离场量', today: t.lichangCount, week: w.lichangCount, type: 'int' },
				{ label: '收入合计(元)', today: t.revenue, week: w.revenue, type: 'money' },
				{ label: '　停车收入', today: t.parkingRevenue, week: w.parkingRevenue, type: 'money' },
				{ label: '　补缴收入', today: t.bujiaoRevenue, week: w.bujiaoRevenue, type: 'money' },
				{ label: '超时笔数', today: t.chaoshiTotal, week: w.chaoshiTotal, type: 'int', highlight: true },
				{ label: '　预约取消', today: t.chaoshiCancelCount, week: w.chaoshiCancelCount, type: 'int' },
				{ label: '　超时补缴单', today: t.chaoshiBujiaoCount, week: w.chaoshiBujiaoCount, type: 'int' }
			]
		}
	},
	created() {
		this.loadData()
	},
	methods: {
		loadData() {
			this.loading = true
			this.$http.get('chewei/n8/kanban/overview').then(({ data }) => {
				if (data && data.code === 0) {
					this.overview = data.data || {}
				} else {
					this.$message.error((data && data.msg) || '加载失败')
				}
			}).finally(() => { this.loading = false })
		},
		formatCell(v, type) {
			if (v === null || v === undefined) return '0'
			if (type === 'money') return Number(v).toFixed(2)
			return String(v)
		}
	}
}
</script>

<style scoped>
.kpi-title { color: #909399; font-size: 13px; }
.kpi-value { font-size: 28px; font-weight: bold; margin: 8px 0; color: #303133; }
.kpi-value.primary { color: #409EFF; }
.kpi-sub { font-size: 12px; color: #909399; }
.num-warn { color: #E6A23C; font-weight: 600; }
</style>

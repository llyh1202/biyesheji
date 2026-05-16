<template>
	<!-- 这是我cursor给父亲写的 — N9 多维度统计报表 -->
	<div class="main-content" :style='{"padding":"24px","background":"#f5f7fa","minHeight":"100vh"}'>
		<h2 :style='{"margin":"0 0 16px","fontSize":"20px"}'>多维度统计报表（N9）</h2>
		<el-card shadow="never" :style='{"marginBottom":"16px"}'>
			<el-form :inline="true" :model="query">
				<el-form-item label="停车场">
					<el-input v-model="query.tingchechangmingcheng" placeholder="空=全部" clearable style="width:160px" />
				</el-form-item>
				<el-form-item label="区域">
					<el-input v-model="query.quyu" placeholder="空=全部" clearable style="width:120px" />
				</el-form-item>
				<el-form-item label="开始日期">
					<el-date-picker v-model="query.kaishiRiqi" type="date" value-format="yyyy-MM-dd" style="width:150px" />
				</el-form-item>
				<el-form-item label="结束日期">
					<el-date-picker v-model="query.jieshuRiqi" type="date" value-format="yyyy-MM-dd" style="width:150px" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" :loading="loading" @click="loadReport">查询</el-button>
					<el-button type="success" :loading="exporting" @click="exportExcel">导出 Excel</el-button>
				</el-form-item>
			</el-form>
			<p :style='{"margin":"0","color":"#909399","fontSize":"12px"}'>
				统计区间：{{ report.kaishiRiqi }} ~ {{ report.jieshuRiqi }}（{{ report.statDays }} 天）；
				收入合计：<b>{{ report.totalRevenue }}</b> 元。
				周转率 = 离场数 ÷ 车位数 ÷ 天数。
			</p>
		</el-card>

		<el-card shadow="never" :style='{"marginBottom":"16px"}'>
			<div slot="header">收入趋势</div>
			<div id="n9RevenueChart" :style='{"width":"100%","height":"320px"}' />
		</el-card>

		<el-table v-loading="loading" :data="report.dimensions" border stripe>
			<el-table-column prop="tingchechangmingcheng" label="停车场" min-width="120" />
			<el-table-column prop="quyu" label="区域" width="90" />
			<el-table-column prop="cheweiCount" label="车位数" width="80" align="center" />
			<el-table-column prop="ruchangCount" label="入场量" width="80" align="center" />
			<el-table-column prop="lichangCount" label="离场量" width="80" align="center" />
			<el-table-column label="周转率" width="110" align="center">
				<template slot-scope="s">{{ s.row.turnoverRate }}</template>
			</el-table-column>
			<el-table-column label="平均时长(h)" width="110" align="center">
				<template slot-scope="s">{{ s.row.avgParkingHours }}</template>
			</el-table-column>
			<el-table-column label="收入(元)" width="100" align="right">
				<template slot-scope="s">{{ s.row.revenue }}</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<script>
import axios from 'axios'
import * as echarts from 'echarts'

export default {
	data() {
		const end = new Date()
		const start = new Date()
		start.setDate(start.getDate() - 6)
		const fmt = d => {
			const p = n => (n < 10 ? '0' : '') + n
			return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate())
		}
		return {
			loading: false,
			exporting: false,
			query: {
				tingchechangmingcheng: '',
				quyu: '',
				kaishiRiqi: fmt(start),
				jieshuRiqi: fmt(end)
			},
			report: {
				kaishiRiqi: '',
				jieshuRiqi: '',
				statDays: 0,
				totalRevenue: 0,
				dimensions: [],
				revenueTrend: []
			},
			chart: null
		}
	},
	mounted() {
		this.loadReport()
	},
	beforeDestroy() {
		if (this.chart) {
			this.chart.dispose()
		}
	},
	methods: {
		loadReport() {
			this.loading = true
			this.$http.post('chewei/n9/baobiao/query', this.query).then(({ data }) => {
				if (data && data.code === 0) {
					this.report = data.data || this.report
					this.$nextTick(() => this.renderChart())
				} else {
					this.$message.error((data && data.msg) || '查询失败')
				}
			}).finally(() => { this.loading = false })
		},
		renderChart() {
			const el = document.getElementById('n9RevenueChart')
			if (!el) return
			if (!this.chart) {
				this.chart = echarts.init(el)
			}
			const trend = this.report.revenueTrend || []
			this.chart.setOption({
				tooltip: { trigger: 'axis' },
				legend: { data: ['收入', '离场笔数'] },
				xAxis: { type: 'category', data: trend.map(t => t.riqi) },
				yAxis: [{ type: 'value', name: '收入(元)' }, { type: 'value', name: '笔数' }],
				series: [
					{ name: '收入', type: 'line', smooth: true, data: trend.map(t => t.revenue) },
					{ name: '离场笔数', type: 'bar', yAxisIndex: 1, data: trend.map(t => t.lichangCount) }
				]
			})
		},
		exportExcel() {
			const q = this.query
			const params = new URLSearchParams()
			if (q.tingchechangmingcheng) params.append('tingchechangmingcheng', q.tingchechangmingcheng)
			if (q.quyu) params.append('quyu', q.quyu)
			if (q.kaishiRiqi) params.append('kaishiRiqi', q.kaishiRiqi)
			if (q.jieshuRiqi) params.append('jieshuRiqi', q.jieshuRiqi)
			this.exporting = true
			axios.get(this.$base.url + 'chewei/n9/baobiao/exportExcel?' + params.toString(), {
				headers: { Token: this.$storage.get('Token') },
				responseType: 'blob'
			}).then(res => {
				const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
				const url = window.URL.createObjectURL(blob)
				const a = document.createElement('a')
				a.href = url
				a.download = 'n9_baobiao_' + (q.kaishiRiqi || '') + '_' + (q.jieshuRiqi || '') + '.xlsx'
				a.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }))
				window.URL.revokeObjectURL(url)
				this.$message.success('导出成功')
			}).catch(() => {
				this.$message.error('导出失败')
			}).finally(() => { this.exporting = false })
		}
	}
}
</script>

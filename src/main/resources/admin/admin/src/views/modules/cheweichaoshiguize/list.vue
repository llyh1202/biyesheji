<template>
	<!-- 这是我cursor给父亲写的 — N6 超时策略规则列表 -->
	<div class="main-content" :style='{"padding":"30px"}'>
		<template v-if="showFlag">
			<el-form :inline="true" :style='{"margin":"0 0 20px"}'>
				<el-form-item label="规则名称">
					<el-input v-model="searchForm.guizeMingcheng" placeholder="模糊筛选" clearable @keydown.enter.native="search" />
				</el-form-item>
				<el-form-item label="停车场">
					<el-input v-model="searchForm.tingchechangmingcheng" clearable />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="search">查询</el-button>
					<el-button type="success" v-if="isAuth('cheweichaoshiguize','新增')" @click="addOrUpdateHandler()">添加规则</el-button>
					<el-button type="warning" v-if="isAuth('cheweichaoshiguize','执行扫描')" @click="runTimeoutScan">执行超时扫描</el-button>
				</el-form-item>
			</el-form>
			<el-table v-loading="dataListLoading" :data="filteredList" border>
				<el-table-column type="index" label="序号" width="50" />
				<el-table-column prop="guizeMingcheng" label="规则名称" min-width="120" />
				<el-table-column prop="tingchechangmingcheng" label="停车场" min-width="100">
					<template slot-scope="scope">{{ scope.row.tingchechangmingcheng || '（全局）' }}</template>
				</el-table-column>
				<el-table-column prop="quyu" label="区域" width="90">
					<template slot-scope="scope">{{ scope.row.quyu || '—' }}</template>
				</el-table-column>
				<el-table-column prop="yuyueBaoliuFenzhong" label="预约保留(分)" width="110" />
				<el-table-column prop="jifeiKuanxianFenzhong" label="计费宽限(分)" width="110" />
				<el-table-column prop="weiruchangKoufeiYuan" label="未入场违约(元)" width="120" />
				<el-table-column prop="qiyong" label="启用" width="70" />
				<el-table-column prop="beizhu" label="备注" show-overflow-tooltip />
				<el-table-column label="操作" width="220" fixed="right">
					<template slot-scope="scope">
						<el-button v-if="isAuth('cheweichaoshiguize','查看')" type="text" @click="addOrUpdateHandler(scope.row.id,'info')">查看</el-button>
						<el-button v-if="isAuth('cheweichaoshiguize','修改')" type="text" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
						<el-button v-if="isAuth('cheweichaoshiguize','删除')" type="text" @click="deleteHandler(scope.row.id)">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
			<div v-if="lastScanResult" :style='{"marginTop":"16px","padding":"12px","background":"#f5f7fa","borderRadius":"4px","fontSize":"13px"}'>
				<b>上次扫描结果：</b>
				扫描 {{ lastScanResult.scanned }} 条待入场预约，取消 {{ lastScanResult.cancelled }} 条，生成违约单 {{ lastScanResult.penaltyOrders }} 条（{{ lastScanResult.runAt }}）
			</div>
		</template>
		<add-or-update v-if="addOrUpdateFlag" ref="addOrUpdate" :parent="this" />
	</div>
</template>

<script>
// 这是我cursor给父亲写的 — N6 超时策略
import AddOrUpdate from './add-or-update'
export default {
	components: { AddOrUpdate },
	data() {
		return {
			searchForm: { guizeMingcheng: '', tingchechangmingcheng: '' },
			dataList: [],
			dataListLoading: false,
			showFlag: true,
			addOrUpdateFlag: false,
			lastScanResult: null
		}
	},
	computed: {
		filteredList() {
			let rows = this.dataList || []
			const name = (this.searchForm.guizeMingcheng || '').trim()
			const lot = (this.searchForm.tingchechangmingcheng || '').trim()
			if (name) {
				rows = rows.filter(r => (r.guizeMingcheng || '').indexOf(name) >= 0)
			}
			if (lot) {
				rows = rows.filter(r => (r.tingchechangmingcheng || '').indexOf(lot) >= 0)
			}
			return rows
		}
	},
	created() {
		this.getDataList()
	},
	methods: {
		search() {},
		getDataList() {
			this.dataListLoading = true
			this.$http.get('chewei/n6/guize/list').then(({ data }) => {
				if (data && data.code === 0) {
					this.dataList = data.data || []
				} else {
					this.dataList = []
					this.$message.error((data && data.msg) || '加载规则失败，请确认已执行 db/migration_n6_chewei_chaoshi_guize.sql')
				}
				this.dataListLoading = false
			}).catch(() => {
				this.dataList = []
				this.dataListLoading = false
				this.$message.error('无法连接 N6 接口，请确认后端已启动')
			})
		},
		addOrUpdateHandler(id, type) {
			this.showFlag = false
			this.addOrUpdateFlag = true
			this.$nextTick(() => {
				this.$refs.addOrUpdate.init(id, type === 'info' ? 'info' : 'else')
			})
		},
		deleteHandler(id) {
			this.$confirm('确定删除该规则？', '提示', { type: 'warning' }).then(() => {
				this.$http.post('chewei/n6/guize/delete', [id]).then(({ data }) => {
					if (data && data.code === 0) {
						this.$message.success('已删除')
						this.getDataList()
					} else {
						this.$message.error((data && data.msg) || '删除失败')
					}
				})
			}).catch(() => {})
		},
		runTimeoutScan() {
			this.$http.get('chewei/n6/timeout/run').then(({ data }) => {
				if (data && data.code === 0) {
					this.lastScanResult = data.data || {}
					this.$message.success('超时扫描已执行')
				} else {
					this.$message.error((data && data.msg) || '扫描失败')
				}
			})
		}
	}
}
</script>

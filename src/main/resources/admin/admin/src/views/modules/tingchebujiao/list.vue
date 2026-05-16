<template>
	<!-- 这是我cursor给父亲写的 — N7 补缴单管理 -->
	<div class="main-content" :style='{"padding":"30px"}'>
		<el-card shadow="never" :style='{"marginBottom":"16px"}'>
			<div slot="header">新建补缴/调整单（管理员）</div>
			<el-form label-width="120px" :model="createForm">
				<el-form-item label="入场单 id" required>
					<el-input v-model="createForm.chezijinchangId" placeholder="chezijinchang 主键" />
				</el-form-item>
				<el-form-item label="类型">
					<el-select v-model="createForm.leixing" style="width:240px">
						<el-option label="超时补缴" value="超时补缴" />
						<el-option label="管理员调整" value="管理员调整" />
						<el-option label="续费" value="续费" />
					</el-select>
				</el-form-item>
				<el-form-item label="金额(元)" required>
					<el-input-number v-model="createForm.jine" :min="0.01" :precision="2" />
				</el-form-item>
				<el-form-item label="原因说明">
					<el-input v-model="createForm.yuanyin" type="textarea" />
				</el-form-item>
				<el-form-item label="备注">
					<el-input v-model="createForm.beizhu" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" :loading="creating" @click="checkOvertime">检测是否超时</el-button>
					<el-button type="success" :loading="creating" @click="submitCreate">创建补缴单</el-button>
				</el-form-item>
			</el-form>
			<el-alert v-if="overtimeHint" :title="overtimeHint" type="warning" :closable="false" show-icon />
		</el-card>
		<el-form :inline="true" :style='{"marginBottom":"12px"}'>
			<el-form-item label="入场单 id">
				<el-input v-model="filterRuchangId" clearable />
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click="loadList">刷新列表</el-button>
			</el-form-item>
		</el-form>
		<el-table v-loading="loading" :data="filteredList" border>
			<el-table-column prop="danhao" label="单号" min-width="140" />
			<el-table-column prop="chezijinchangId" label="入场单" width="80" />
			<el-table-column prop="leixing" label="类型" width="100" />
			<el-table-column prop="jine" label="金额" width="80" />
			<el-table-column prop="zhuangtai" label="状态" width="100" />
			<el-table-column prop="yonghuzhanghao" label="用户" width="100" />
			<el-table-column prop="chepaihao" label="车牌" width="100" />
			<el-table-column prop="yuanyin" label="原因" show-overflow-tooltip />
			<el-table-column label="操作" width="120">
				<template slot-scope="s">
					<el-button v-if="s.row.zhuangtai==='待支付'" type="text" @click="cancelRow(s.row.id)">作废</el-button>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<script>
export default {
	data() {
		return {
			loading: false,
			creating: false,
			dataList: [],
			filterRuchangId: '',
			overtimeHint: '',
			createForm: {
				chezijinchangId: '',
				leixing: '管理员调整',
				jine: 10,
				yuanyin: '',
				beizhu: ''
			}
		}
	},
	computed: {
		filteredList() {
			const id = (this.filterRuchangId || '').trim()
			if (!id) return this.dataList
			return this.dataList.filter(r => String(r.chezijinchangId) === id)
		}
	},
	created() {
		this.loadList()
	},
	methods: {
		parseId(val) {
			const s = String(val || '').trim()
			if (!/^\d+$/.test(s)) {
				this.$message.warning('入场单 id 须为数字')
				return null
			}
			return parseInt(s, 10)
		},
		loadList() {
			this.loading = true
			this.$http.get('chewei/n7/bujiao/list').then(({ data }) => {
				if (data && data.code === 0) {
					this.dataList = data.data || []
				} else {
					this.$message.error((data && data.msg) || '加载失败')
				}
				this.loading = false
			}).catch(() => {
				this.loading = false
				this.$message.error('请确认已执行 migration_n7 且后端已启动')
			})
		},
		checkOvertime() {
			const id = this.parseId(this.createForm.chezijinchangId)
			if (id == null) return
			this.$http.get('chewei/n7/overtime/check', { params: { chezijinchangId: id } }).then(({ data }) => {
				if (data && data.code === 0) {
					const d = data.data || {}
					this.overtimeHint = d.hint || ''
					if (d.suggestedJine > 0) {
						this.createForm.jine = d.suggestedJine
						this.createForm.leixing = '超时补缴'
					}
				}
			})
		},
		submitCreate() {
			const id = this.parseId(this.createForm.chezijinchangId)
			if (id == null) return
			this.creating = true
			this.$http.post('chewei/n7/bujiao/admin/create', {
				chezijinchangId: id,
				leixing: this.createForm.leixing,
				jine: this.createForm.jine,
				yuanyin: this.createForm.yuanyin,
				beizhu: this.createForm.beizhu
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.$message.success('已创建补缴单')
					this.loadList()
				} else {
					this.$message.error((data && data.msg) || '创建失败')
				}
				this.creating = false
			}).catch(() => {
				this.creating = false
				this.$message.error('创建请求失败')
			})
		},
		cancelRow(id) {
			this.$confirm('作废该补缴单？', '提示', { type: 'warning' }).then(() => {
				this.$http.post('chewei/n7/bujiao/cancel', { id }).then(({ data }) => {
					if (data && data.code === 0) {
						this.$message.success('已作废')
						this.loadList()
					} else {
						this.$message.error((data && data.msg) || '操作失败')
					}
				})
			}).catch(() => {})
		}
	}
}
</script>

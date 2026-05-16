<template>
	<!-- 这是我cursor给父亲写的 — M5 计费规则 -->
	<div class="main-content" :style='{"padding":"30px"}'>
		<el-form :inline="true" :style='{"marginBottom":"12px"}'>
			<el-button type="primary" @click="loadList">刷新</el-button>
			<el-button type="success" @click="openForm()">添加规则</el-button>
		</el-form>
		<el-table v-loading="loading" :data="dataList" border>
			<el-table-column prop="guizeMingcheng" label="名称" />
			<el-table-column prop="tingchechangmingcheng" label="停车场">
				<template slot-scope="s">{{ s.row.tingchechangmingcheng || '全局' }}</template>
			</el-table-column>
			<el-table-column prop="jifeiMoshi" label="模式" width="90" />
			<el-table-column prop="meixiaoshiDanjia" label="时单价" width="70" />
			<el-table-column prop="shouxiaoshiYuan" label="首小时(元)" width="90" />
			<el-table-column prop="jietiDanjia" label="阶梯单价" width="90" />
			<el-table-column prop="fengdingYuan" label="封顶(元)" width="80" />
			<el-table-column prop="qiyong" label="启用" width="60" />
			<el-table-column label="操作" width="140">
				<template slot-scope="s">
					<el-button type="text" @click="openForm(s.row)">编辑</el-button>
					<el-button type="text" @click="delRow(s.row.id)">删除</el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-dialog :title="form.id ? '编辑计费规则' : '新增计费规则'" :visible.sync="dialogVisible" width="520px">
			<el-form label-width="120px" :model="form">
				<el-form-item label="规则名称"><el-input v-model="form.guizeMingcheng" /></el-form-item>
				<el-form-item label="停车场"><el-input v-model="form.tingchechangmingcheng" placeholder="空=全局" /></el-form-item>
				<el-form-item label="区域"><el-input v-model="form.quyu" /></el-form-item>
				<el-form-item label="计费模式">
					<el-select v-model="form.jifeiMoshi">
						<el-option label="纯小时" value="纯小时" />
						<el-option label="首小时" value="首小时" />
						<el-option label="阶梯" value="阶梯" />
					</el-select>
				</el-form-item>
				<el-form-item label="每小时单价"><el-input-number v-model="form.meixiaoshiDanjia" :min="0" /></el-form-item>
				<el-form-item label="首小时费用"><el-input-number v-model="form.shouxiaoshiYuan" :min="0" :precision="2" /></el-form-item>
				<el-form-item label="后续时单价"><el-input-number v-model="form.jietiDanjia" :min="0" /></el-form-item>
				<el-form-item label="封顶金额"><el-input-number v-model="form.fengdingYuan" :min="0" :precision="2" /></el-form-item>
				<el-form-item label="最小计费小时"><el-input-number v-model="form.zuixiaoJifeiXiaoshi" :min="0.5" :step="0.5" /></el-form-item>
				<el-form-item label="启用">
					<el-select v-model="form.qiyong"><el-option label="是" value="是" /><el-option label="否" value="否" /></el-select>
				</el-form-item>
			</el-form>
			<span slot="footer">
				<el-button @click="dialogVisible=false">取消</el-button>
				<el-button type="primary" @click="saveForm">保存</el-button>
			</span>
		</el-dialog>
	</div>
</template>
<script>
export default {
	data() {
		return {
			loading: false,
			dataList: [],
			dialogVisible: false,
			form: {}
		}
	},
	created() { this.loadList() },
	methods: {
		loadList() {
			this.loading = true
			this.$http.get('chewei/m5/guize/list').then(({ data }) => {
				if (data && data.code === 0) this.dataList = data.data || []
				this.loading = false
			})
		},
		openForm(row) {
			this.form = row ? Object.assign({}, row) : {
				guizeMingcheng: '', jifeiMoshi: '纯小时', qiyong: '是', zuixiaoJifeiXiaoshi: 1
			}
			this.dialogVisible = true
		},
		saveForm() {
			const url = this.form.id ? 'chewei/m5/guize/update' : 'chewei/m5/guize/save'
			this.$http.post(url, this.form).then(({ data }) => {
				if (data && data.code === 0) {
					this.$message.success('已保存')
					this.dialogVisible = false
					this.loadList()
				} else this.$message.error((data && data.msg) || '失败')
			})
		},
		delRow(id) {
			this.$http.post('chewei/m5/guize/delete', [id]).then(({ data }) => {
				if (data && data.code === 0) { this.$message.success('已删除'); this.loadList() }
			})
		}
	}
}
</script>

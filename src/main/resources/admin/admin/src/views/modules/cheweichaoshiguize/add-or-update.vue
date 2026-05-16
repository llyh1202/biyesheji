<template>
	<!-- 这是我cursor给父亲写的 — N6 超时策略表单 -->
	<div class="addEdit-block" :style='{"padding":"30px"}'>
		<el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="160px" :style='{"padding":"30px","background":"#fff","borderRadius":"6px"}'>
			<el-form-item label="规则名称" prop="guizeMingcheng">
				<el-input v-model="ruleForm.guizeMingcheng" :readonly="type==='info'" placeholder="如 东区默认" />
			</el-form-item>
			<el-form-item label="停车场名称">
				<el-input v-model="ruleForm.tingchechangmingcheng" :readonly="type==='info'" placeholder="留空表示全局默认" />
			</el-form-item>
			<el-form-item label="区域">
				<el-input v-model="ruleForm.quyu" :readonly="type==='info'" placeholder="留空表示全场" />
			</el-form-item>
			<el-form-item label="预约保留时长(分钟)" prop="yuyueBaoliuFenzhong">
				<el-input-number v-model="ruleForm.yuyueBaoliuFenzhong" :min="0" :disabled="type==='info'" />
			</el-form-item>
			<el-form-item label="计费宽限期(分钟)" prop="jifeiKuanxianFenzhong">
				<el-input-number v-model="ruleForm.jifeiKuanxianFenzhong" :min="0" :disabled="type==='info'" />
			</el-form-item>
			<el-form-item label="未入场违约金(元)">
				<el-input-number v-model="ruleForm.weiruchangKoufeiYuan" :min="0" :precision="2" :disabled="type==='info'" />
			</el-form-item>
			<el-form-item label="启用">
				<el-select v-model="ruleForm.qiyong" :disabled="type==='info'">
					<el-option label="是" value="是" />
					<el-option label="否" value="否" />
				</el-select>
			</el-form-item>
			<el-form-item label="备注">
				<el-input type="textarea" v-model="ruleForm.beizhu" :readonly="type==='info'" />
			</el-form-item>
			<el-form-item>
				<el-button v-if="type!=='info'" type="primary" @click="onSubmit">提交</el-button>
				<el-button @click="back()">{{ type==='info' ? '返回' : '取消' }}</el-button>
			</el-form-item>
		</el-form>
	</div>
</template>

<script>
// 这是我cursor给父亲写的 — N6 超时策略
export default {
	props: ['parent'],
	data() {
		return {
			id: '',
			type: '',
			ruleForm: {
				id: '',
				guizeMingcheng: '',
				tingchechangmingcheng: '',
				quyu: '',
				yuyueBaoliuFenzhong: 30,
				jifeiKuanxianFenzhong: 15,
				weiruchangKoufeiYuan: 0,
				qiyong: '是',
				beizhu: ''
			},
			rules: {
				guizeMingcheng: [{ required: true, message: '请填写规则名称', trigger: 'blur' }],
				yuyueBaoliuFenzhong: [{ required: true, message: '必填', trigger: 'blur' }],
				jifeiKuanxianFenzhong: [{ required: true, message: '必填', trigger: 'blur' }]
			}
		}
	},
	methods: {
		init(id, type) {
			this.id = id || ''
			this.type = type || 'else'
			if (this.id) {
				this.$http.get('chewei/n6/guize/list').then(({ data }) => {
					const rows = (data && data.data) || []
					const row = rows.find(r => String(r.id) === String(this.id))
					if (row) {
						this.ruleForm = Object.assign({}, this.ruleForm, row)
					}
				})
			} else {
				this.ruleForm = {
					id: '',
					guizeMingcheng: '',
					tingchechangmingcheng: '',
					quyu: '',
					yuyueBaoliuFenzhong: 30,
					jifeiKuanxianFenzhong: 15,
					weiruchangKoufeiYuan: 0,
					qiyong: '是',
					beizhu: ''
				}
			}
		},
		onSubmit() {
			this.$refs.ruleForm.validate(valid => {
				if (!valid) return
				const url = this.ruleForm.id ? 'chewei/n6/guize/update' : 'chewei/n6/guize/save'
				this.$http.post(url, this.ruleForm).then(({ data }) => {
					if (data && data.code === 0) {
						this.$message.success('保存成功')
						this.back()
					} else {
						this.$message.error((data && data.msg) || '保存失败')
					}
				})
			})
		},
		back() {
			this.parent.showFlag = true
			this.parent.addOrUpdateFlag = false
			this.parent.getDataList()
		}
	}
}
</script>

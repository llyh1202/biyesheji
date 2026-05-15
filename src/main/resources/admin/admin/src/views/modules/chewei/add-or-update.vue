<template>
	<!-- 这是我cursor给父亲写的 — N1 车位主数据表单 -->
	<div class="addEdit-block" :style='{"padding":"30px"}'>
		<el-form
			:style='{"padding":"40px 30px","boxShadow":"0 1px 6px rgba(64, 158, 255, .3)","borderRadius":"6px","flexWrap":"wrap","background":"#fff","display":"flex"}'
			class="add-update-preview"
			ref="ruleForm"
			:model="ruleForm"
			:rules="rules"
			label-width="180px"
		>
			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' class="input" v-if="type!='info'" label="停车场名称" prop="tingchechangmingcheng">
				<el-input v-model="ruleForm.tingchechangmingcheng" placeholder="停车场名称" clearable :readonly="ro.tingchechangmingcheng"></el-input>
			</el-form-item>
			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' v-else class="input" label="停车场名称" prop="tingchechangmingcheng">
				<el-input v-model="ruleForm.tingchechangmingcheng" readonly></el-input>
			</el-form-item>

			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' class="input" v-if="type!='info'" label="区域" prop="quyu">
				<el-select :disabled="ro.quyu" v-model="ruleForm.quyu" placeholder="请选择区域" clearable filterable allow-create default-first-option>
					<el-option v-for="(item, index) in quyuOptions" :key="index" :label="item" :value="item"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' v-else class="input" label="区域" prop="quyu">
				<el-input v-model="ruleForm.quyu" readonly></el-input>
			</el-form-item>

			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' class="input" v-if="type!='info'" label="车位编号" prop="cheweibianhao">
				<el-input v-model="ruleForm.cheweibianhao" placeholder="如 A-01" clearable :readonly="ro.cheweibianhao"></el-input>
			</el-form-item>
			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' v-else class="input" label="车位编号" prop="cheweibianhao">
				<el-input v-model="ruleForm.cheweibianhao" readonly></el-input>
			</el-form-item>

			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' class="input" v-if="type!='info'" label="状态" prop="zhuangtai">
				<el-input v-model="ruleForm.zhuangtai" placeholder="默认 空闲" clearable></el-input>
			</el-form-item>
			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' v-else class="input" label="状态" prop="zhuangtai">
				<el-input v-model="ruleForm.zhuangtai" readonly></el-input>
			</el-form-item>

			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' class="input" v-if="type!='info'" label="关联车位信息ID" prop="cheweixinxiId">
				<el-input v-model="ruleForm.cheweixinxiId" placeholder="可选，对应车位信息表主键" clearable></el-input>
			</el-form-item>
			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' v-else class="input" label="关联车位信息ID" prop="cheweixinxiId">
				<el-input v-model="ruleForm.cheweixinxiId" readonly></el-input>
			</el-form-item>

			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' class="textarea" v-if="type!='info'" label="备注" prop="beizhu">
				<el-input type="textarea" :rows="3" v-model="ruleForm.beizhu" placeholder="备注"></el-input>
			</el-form-item>
			<el-form-item :style='{"width":"100%","margin":"0 0 20px 0"}' v-else class="input" label="备注" prop="beizhu">
				<span :style='{"fontSize":"14px","lineHeight":"24px","color":"#333"}'>{{ ruleForm.beizhu }}</span>
			</el-form-item>

			<el-form-item :style='{"width":"100%","padding":"0","margin":"20px 0 0"}' class="btn">
				<el-button class="btn3" v-if="type!='info'" type="success" @click="onSubmit">提交</el-button>
				<el-button class="btn4" v-if="type!='info'" type="success" @click="back()">取消</el-button>
				<el-button class="btn5" v-if="type=='info'" type="success" @click="back()">返回</el-button>
			</el-form-item>
		</el-form>
	</div>
</template>

<script>
// 这是我cursor给父亲写的 — N1 车位主数据
export default {
	props: ['parent'],
	data() {
		return {
			id: '',
			type: '',
			ro: {
				tingchechangmingcheng: false,
				quyu: false,
				cheweibianhao: false
			},
			ruleForm: {
				id: '',
				tingchechangmingcheng: '',
				quyu: '',
				cheweibianhao: '',
				zhuangtai: '空闲',
				cheweixinxiId: '',
				beizhu: ''
			},
			quyuOptions: [],
			rules: {
				tingchechangmingcheng: [{ required: true, message: '停车场名称不能为空', trigger: 'blur' }],
				cheweibianhao: [{ required: true, message: '车位编号不能为空', trigger: 'blur' }]
			}
		}
	},
	methods: {
		init(id, type) {
			this.quyuOptions = 'A区,B区,C区,D区'.split(',')
			this.id = id
			this.type = type
			if (this.type === 'info') {
				this.info(id)
			} else if (this.type === 'else') {
				if (id) {
					this.info(id)
				} else {
					this.ruleForm = {
						id: '',
						tingchechangmingcheng: '',
						quyu: '',
						cheweibianhao: '',
						zhuangtai: '空闲',
						cheweixinxiId: '',
						beizhu: ''
					}
				}
			}
		},
		info(id) {
			this.$http({
				url: `chewei/info/${id}`,
				method: 'get'
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.ruleForm = Object.assign({}, this.ruleForm, data.data)
					if (this.ruleForm.cheweixinxiId == null) {
						this.ruleForm.cheweixinxiId = ''
					}
				} else {
					this.$message.error(data.msg)
				}
			})
		},
		onSubmit() {
			this.$refs.ruleForm.validate(valid => {
				if (!valid) return
				const payload = Object.assign({}, this.ruleForm)
				if (payload.cheweixinxiId === '' || payload.cheweixinxiId === undefined) {
					delete payload.cheweixinxiId
				} else {
					payload.cheweixinxiId = Number(payload.cheweixinxiId)
				}
				const url = `chewei/${!payload.id ? 'save' : 'update'}`
				this.$http({
					url,
					method: 'post',
					data: payload
				}).then(({ data }) => {
					if (data && data.code === 0) {
						this.$message({
							message: '操作成功',
							type: 'success',
							duration: 1500,
							onClose: () => {
								this.parent.showFlag = true
								this.parent.addOrUpdateFlag = false
								this.parent.search()
								this.parent.contentStyleChange()
							}
						})
					} else {
						this.$message.error(data.msg)
					}
				})
			})
		},
		back() {
			this.parent.showFlag = true
			this.parent.addOrUpdateFlag = false
			this.parent.contentStyleChange()
		}
	}
}
</script>

<style lang="scss" scoped>
.add-update-preview .el-form-item ::v-deep  .el-form-item__label {
	padding: 0 10px 0 0;
	color: #666;
	font-weight: 500;
	width: 180px;
	font-size: 14px;
	line-height: 40px;
	text-align: right;
}
.add-update-preview .el-form-item ::v-deep  .el-form-item__content {
	margin-left: 180px;
}
.add-update-preview .el-input ::v-deep  .el-input__inner,
.add-update-preview .el-textarea ::v-deep  .el-textarea__inner {
	border: 1px solid #000;
	border-radius: 4px;
	max-width: 500px;
}
</style>

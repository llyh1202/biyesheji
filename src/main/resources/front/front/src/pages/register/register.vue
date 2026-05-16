<template>
	<!-- 这是我cursor给父亲写的 — 用户注册页简洁布局（逻辑与数据未改） -->
	<div class="tech-auth-page register-page-wrap">
		<div class="register-page">
			<div class="register-card">
				<header class="register-header">
					<div class="register-logo">
						<i class="el-icon-user-solid"></i>
					</div>
					<h1 class="register-title">创建账号</h1>
					<p class="register-subtitle">智慧停车场管理系统 · 用户注册</p>
				</header>

				<el-form
					v-if="pageFlag == 'register'"
					ref="registerForm"
					class="rgs-form register-form"
					:model="registerForm"
					:rules="rules"
					label-position="top"
					hide-required-asterisk
				>
					<div class="register-fields">
						<el-form-item v-if="tableName == 'yonghu'" label="用户账号" prop="yonghuzhanghao" class="register-field">
							<el-input v-model="registerForm.yonghuzhanghao" placeholder="5～12 位字符" clearable />
						</el-form-item>
						<el-form-item v-if="tableName == 'yonghu'" label="姓名" prop="xingming" class="register-field">
							<el-input v-model="registerForm.xingming" placeholder="请输入真实姓名" clearable />
						</el-form-item>
						<div class="register-row-2" v-if="tableName == 'yonghu'">
							<el-form-item label="密码" prop="mima" class="register-field">
								<el-input v-model="registerForm.mima" type="password" placeholder="6～15 位" show-password />
							</el-form-item>
							<el-form-item label="确认密码" prop="mima2" class="register-field">
								<el-input v-model="registerForm.mima2" type="password" placeholder="再次输入密码" show-password />
							</el-form-item>
						</div>
						<div class="register-row-2" v-if="tableName == 'yonghu'">
							<el-form-item label="性别" prop="xingbie" class="register-field">
								<el-select v-model="registerForm.xingbie" placeholder="请选择" style="width:100%">
									<el-option
										v-for="(item, index) in yonghuxingbieOptions"
										:key="index"
										:label="item"
										:value="item"
									/>
								</el-select>
							</el-form-item>
							<el-form-item label="手机" prop="shouji" class="register-field">
								<el-input v-model="registerForm.shouji" placeholder="11 位手机号" clearable maxlength="11" />
							</el-form-item>
						</div>
						<el-form-item v-if="tableName == 'yonghu'" label="车牌号" prop="chepaihao" class="register-field">
							<el-input v-model="registerForm.chepaihao" placeholder="例如：粤B12345" clearable />
						</el-form-item>
						<el-form-item v-if="tableName == 'yonghu'" label="头像" prop="touxiang" class="register-field register-field-avatar">
							<file-upload
								tip="点击上传头像（选填）"
								action="file/upload"
								:limit="1"
								:multiple="true"
								:fileUrls="registerForm.touxiang ? registerForm.touxiang : ''"
								@change="yonghutouxiangUploadChange"
							/>
						</el-form-item>
					</div>

					<div class="register-actions">
						<el-button type="primary" class="btn-register" @click="submitForm('registerForm')">注册</el-button>
						<el-button class="btn-reset" @click="resetForm('registerForm')">重置</el-button>
					</div>

					<router-link class="register-login-link" to="/login">已有账号？去登录</router-link>
				</el-form>
			</div>
		</div>
	</div>
</template>

<script>

export default {
    //数据集合
    data() {
		return {
            pageFlag : '',
			tableName: '',
			registerForm: {},
			forgetForm: {},
			rules: {},
			requiredRules: {},
            yonghuxingbieOptions: [],
		}
    },
	mounted() {
		if(this.$route.query.pageFlag=='register'){
			this.tableName = this.$route.query.role;
			if(this.tableName=='yonghu'){
				this.registerForm = {
					yonghuzhanghao: '',
					xingming: '',
					mima: '',
					mima2: '',
					xingbie: '',
					shouji: '',
					chepaihao: '',
					touxiang: '',
				}
			}
			if ('yonghu' == this.tableName) {
				this.requiredRules.yonghuzhanghao = [{ required: true, message: '请输入用户账号', trigger: 'blur' }]
			}
			if ('yonghu' == this.tableName) {
				this.requiredRules.xingming = [{ required: true, message: '请输入姓名', trigger: 'blur' }]
			}
			if ('yonghu' == this.tableName) {
				this.requiredRules.mima = [{ required: true, message: '请输入密码', trigger: 'blur' }]
			}
		}
	},
    created() {
		this.pageFlag = this.$route.query.pageFlag;
		if(this.$route.query.pageFlag=='register'){
		  if ('yonghu' == this.tableName) {
			this.rules.yonghuzhanghao = [{ required: true, message: '请输入用户账号', trigger: 'blur' }];
		  }
		  if ('yonghu' == this.tableName) {
			this.rules.xingming = [{ required: true, message: '请输入姓名', trigger: 'blur' }];
		  }
		  if ('yonghu' == this.tableName) {
			this.rules.mima = [{ required: true, message: '请输入密码', trigger: 'blur' }];
		  }
			this.yonghuxingbieOptions = "男,女".split(',');
		  if ('yonghu' == this.tableName) {
			this.rules.shouji = [{ required: true, validator: this.$validate.isMobile, trigger: 'blur' }];
		  }
		}
    },
    //方法集合
    methods: {
		changeRules(name){
			if(this.requiredRules[name]){
				return true
			}
			return false
		},
      // 获取uuid
      getUUID () {
        return new Date().getTime();
      },
        // 下二随
      yonghutouxiangUploadChange(fileUrls) {
          this.registerForm.touxiang = fileUrls.replace(new RegExp(this.$config.baseUrl,"g"),"");
      },

        // 多级联动参数


      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            var url=this.tableName+"/register";
				if((!this.registerForm.yonghuzhanghao) && `yonghu` == this.tableName){
					this.$message.error(`用户账号不能为空`);
					return
				}
              if(`yonghu` == this.tableName && (this.registerForm.yonghuzhanghao).toString().length<5){
                this.$message.error('用户账号长度不能小于5');
                return
              }
              if(`yonghu` == this.tableName && (this.registerForm.yonghuzhanghao).toString().length>12){
                this.$message.error('用户账号长度不能大于12');
                return
              }
				if((!this.registerForm.xingming) && `yonghu` == this.tableName){
					this.$message.error(`姓名不能为空`);
					return
				}
              if(`yonghu` == this.tableName && (this.registerForm.xingming).toString().length<1){
                this.$message.error('姓名长度不能小于1');
                return
              }
              if(`yonghu` == this.tableName && (this.registerForm.xingming).toString().length>15){
                this.$message.error('姓名长度不能大于15');
                return
              }
               if(`yonghu` == this.tableName && this.registerForm.mima!=this.registerForm.mima2) {
                this.$message.error(`两次密码输入不一致`);
                return
               }
				if((!this.registerForm.mima) && `yonghu` == this.tableName){
					this.$message.error(`密码不能为空`);
					return
				}
              if(`yonghu` == this.tableName && (this.registerForm.mima).toString().length<6){
                this.$message.error('密码长度不能小于6');
                return
              }
              if(`yonghu` == this.tableName && (this.registerForm.mima).toString().length>15){
                this.$message.error('密码长度不能大于15');
                return
              }
					if(`yonghu` == this.tableName && this.registerForm.shouji &&(!this.$validate.isMobile2(this.registerForm.shouji))){
						this.$message.error(`手机应输入手机格式`);
						return
					}
            this.$http.post(url, this.registerForm).then(res => {
              if (res.data.code === 0) {
                this.$message({
                  message: '注册成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.$router.push('/login');
                  }
                });
              } else {
                this.$message.error(res.data.msg);
              }
            });
          } else {
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      }
    }
  }
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
/* 这是我cursor给父亲写的 — 注册页样式 */
.register-page-wrap {
	min-height: 100vh;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 24px 16px;
	box-sizing: border-box;
}

.register-page {
	width: 100%;
	max-width: 520px;
}

.register-card {
	background: rgba(255, 255, 255, 0.96);
	border: 1px solid rgba(6, 182, 212, 0.25);
	border-radius: 16px;
	box-shadow: 0 12px 40px rgba(6, 182, 212, 0.12), 0 4px 16px rgba(15, 23, 42, 0.06);
	padding: 36px 32px 28px;
	backdrop-filter: blur(12px);
}

.register-header {
	text-align: center;
	margin-bottom: 28px;
}

.register-logo {
	width: 56px;
	height: 56px;
	margin: 0 auto 14px;
	border-radius: 14px;
	background: linear-gradient(135deg, #06b6d4, #6366f1);
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 8px 20px rgba(6, 182, 212, 0.35);

	i {
		font-size: 28px;
		color: #fff;
	}
}

.register-title {
	margin: 0 0 6px;
	font-size: 22px;
	font-weight: 600;
	color: #0e7490;
	letter-spacing: 0.5px;
}

.register-subtitle {
	margin: 0;
	font-size: 13px;
	color: #64748b;
	line-height: 1.5;
}

.register-form {
	width: 100%;
}

.register-fields {
	display: flex;
	flex-direction: column;
	gap: 2px;
}

.register-row-2 {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 0 16px;
}

@media (max-width: 480px) {
	.register-row-2 {
		grid-template-columns: 1fr;
		gap: 0;
	}

	.register-card {
		padding: 28px 20px 22px;
	}
}

.register-form ::v-deep .el-form-item {
	margin-bottom: 16px;
}

.register-form ::v-deep .el-form-item__label {
	padding: 0 0 6px;
	line-height: 1.4;
	font-size: 13px;
	font-weight: 600;
	color: #334155;
}

.register-form ::v-deep .el-form-item__content {
	line-height: normal;
}

.register-form ::v-deep .el-input__inner,
.register-form ::v-deep .el-select .el-input__inner {
	height: 42px;
	line-height: 42px;
	border-radius: 8px;
	border: 1px solid #e2e8f0;
	background: #f8fafc;
	color: #1e293b;
	font-size: 14px;
	transition: border-color 0.2s, box-shadow 0.2s, background 0.2s;

	&:focus {
		border-color: #06b6d4;
		background: #fff;
		box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.12);
	}
}

.register-field-avatar ::v-deep .el-form-item__content {
	display: block;
}

.register-field-avatar ::v-deep .el-upload--picture-card,
.register-field-avatar ::v-deep .upload .upload-img,
.register-field-avatar ::v-deep .el-upload .el-icon-plus {
	width: 88px;
	height: 88px;
	line-height: 88px;
	border-radius: 12px;
	border: 1px dashed #cbd5e1;
	background: #f8fafc;
	color: #94a3b8;
	font-size: 24px;
	transition: border-color 0.2s, color 0.2s;

	&:hover {
		border-color: #06b6d4;
		color: #06b6d4;
	}
}

.register-actions {
	display: flex;
	gap: 12px;
	margin-top: 8px;
}

.register-actions .btn-register {
	flex: 1;
	height: 44px;
	border: none;
	border-radius: 10px;
	font-size: 15px;
	font-weight: 600;
	letter-spacing: 0.5px;
	background: linear-gradient(135deg, #06b6d4, #6366f1) !important;
	box-shadow: 0 6px 16px rgba(6, 182, 212, 0.35);
}

.register-actions .btn-register:hover {
	transform: translateY(-1px);
	box-shadow: 0 8px 20px rgba(6, 182, 212, 0.4);
}

.register-actions .btn-reset {
	flex: 0 0 96px;
	height: 44px;
	border-radius: 10px;
	border: 1px solid #e2e8f0;
	background: #fff;
	color: #64748b;
	font-size: 14px;
}

.register-actions .btn-reset:hover {
	border-color: #06b6d4;
	color: #0e7490;
	background: #f0f9ff;
}

.register-login-link {
	display: block;
	margin-top: 20px;
	text-align: center;
	font-size: 14px;
	color: #0891b2;
	text-decoration: none;
	transition: color 0.2s;
}

.register-login-link:hover {
	color: #06b6d4;
	text-decoration: underline;
}
</style>

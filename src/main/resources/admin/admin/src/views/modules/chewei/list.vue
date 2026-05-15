<template>
	<!-- 这是我cursor给父亲写的 — N1 车位主数据列表/导入 -->
	<div class="main-content" :style='{"padding":"30px"}'>
		<template v-if="showFlag">
			<el-form class="center-form-pv chewei-toolbar-form" :style='{"padding":"0","margin":"0 0 20px","borderRadius":"8px","background":"none","width":"100%"}' :inline="false" :model="searchForm">
				<div class="chewei-toolbar-row">
					<div class="chewei-toolbar-fields">
						<div class="chewei-toolbar-field">
							<label class="item-label">停车场名称</label>
							<el-input v-model="searchForm.tingchechangmingcheng" placeholder="停车场名称" @keydown.enter.native="search()" clearable></el-input>
						</div>
						<div class="chewei-toolbar-field">
							<label class="item-label">区域</label>
							<el-input v-model="searchForm.quyu" placeholder="区域" clearable></el-input>
						</div>
						<div class="chewei-toolbar-field">
							<label class="item-label">车位编号</label>
							<el-input v-model="searchForm.cheweibianhao" placeholder="如 A-01" @keydown.enter.native="search()" clearable></el-input>
						</div>
					</div>
					<div class="chewei-toolbar-btns">
						<el-button class="chewei-toolbar-btn chewei-toolbar-btn--search" @click="search()">查询</el-button>
						<el-button class="chewei-toolbar-btn chewei-toolbar-btn--add" v-if="isAuth('chewei','新增')" @click="addOrUpdateHandler()">添加</el-button>
						<el-button class="chewei-toolbar-btn chewei-toolbar-btn--del" v-if="isAuth('chewei','删除')" :disabled="dataListSelections.length?false:true" type="danger" @click="deleteHandler()">删除</el-button>
						<el-button class="chewei-toolbar-btn chewei-toolbar-btn--import" v-if="isAuth('chewei','导入')" type="success" @click="$refs.importInput.click()">导入 Excel</el-button>
						<el-button class="chewei-toolbar-btn chewei-toolbar-btn--tpl" v-if="isAuth('chewei','下载模板')" type="primary" plain @click="downloadTemplate">下载模板</el-button>
						<input ref="importInput" type="file" accept=".xlsx,.xls" style="display:none" @change="importExcel" />
					</div>
				</div>
			</el-form>
			<div :style='{"width":"100%","padding":"0","overflow":"hidden","borderRadius":"8px"}'>
				<el-table class="tables"
					:stripe='false'
					:style='{"width":"100%","padding":"0","borderColor":"#eee","borderStyle":"solid","borderWidth":"1px 0 0 1px","background":"#fff"}'
					:border='true'
					v-if="isAuth('chewei','查看')"
					:data="dataList"
					v-loading="dataListLoading"
					@selection-change="selectionChangeHandler">
					<el-table-column :resizable='true' type="selection" align="center" width="50"></el-table-column>
					<el-table-column :resizable='true' :sortable='true' label="序号" type="index" width="50" />
					<el-table-column prop="tingchechangmingcheng" label="停车场名称" />
					<el-table-column prop="quyu" label="区域" />
					<el-table-column prop="cheweibianhao" label="车位编号" />
					<el-table-column prop="zhuangtai" label="状态" width="100" />
					<el-table-column prop="cheweixinxiId" label="关联车位信息ID" width="140" />
					<el-table-column prop="beizhu" label="备注" show-overflow-tooltip />
					<el-table-column width="280" label="操作">
						<template slot-scope="scope">
							<el-button class="view" v-if="isAuth('chewei','查看')" type="success" @click="addOrUpdateHandler(scope.row.id,'info')">
								查看
							</el-button>
							<el-button class="edit" v-if="isAuth('chewei','修改')" type="success" @click="addOrUpdateHandler(scope.row.id)">
								修改
							</el-button>
							<el-button class="del" v-if="isAuth('chewei','删除')" type="primary" @click="deleteHandler(scope.row.id)">
								删除
							</el-button>
						</template>
					</el-table-column>
				</el-table>
			</div>
			<el-pagination
				@size-change="sizeChangeHandle"
				@current-change="currentChangeHandle"
				:current-page="pageIndex"
				background
				:page-sizes="[10, 50, 100, 200]"
				:page-size="pageSize"
				:layout="layouts.join()"
				:total="totalPage"
				prev-text="< "
				next-text="> "
				:hide-on-single-page="true"
				:style='{"padding":"0","margin":"20px 0 0","whiteSpace":"nowrap","color":"#333","textAlign":"center","width":"100%","fontWeight":"500"}'
			></el-pagination>
		</template>

		<add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
	</div>
</template>

<script>
// 这是我cursor给父亲写的 — N1 车位主数据
import axios from 'axios'
import AddOrUpdate from './add-or-update'
export default {
	data() {
		return {
			searchForm: {
				tingchechangmingcheng: '',
				quyu: '',
				cheweibianhao: ''
			},
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalPage: 0,
			dataListLoading: false,
			dataListSelections: [],
			showFlag: true,
			addOrUpdateFlag: false,
			layouts: ['total', 'prev', 'pager', 'next', 'sizes', 'jumper']
		}
	},
	created() {
		this.getDataList()
		this.contentStyleChange()
	},
	components: { AddOrUpdate },
	methods: {
		contentStyleChange() {
			this.contentPageStyleChange()
		},
		contentPageStyleChange() {},
		search() {
			this.pageIndex = 1
			this.getDataList()
		},
		getDataList() {
			this.dataListLoading = true
			const params = {
				page: this.pageIndex,
				limit: this.pageSize,
				sort: 'id',
				order: 'desc'
			}
			if (this.searchForm.tingchechangmingcheng) {
				params.tingchechangmingcheng = '%' + this.searchForm.tingchechangmingcheng + '%'
			}
			if (this.searchForm.quyu) {
				params.quyu = '%' + this.searchForm.quyu + '%'
			}
			if (this.searchForm.cheweibianhao) {
				params.cheweibianhao = '%' + this.searchForm.cheweibianhao + '%'
			}
			this.$http({
				url: 'chewei/page',
				method: 'get',
				params
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.dataList = data.data.list
					this.totalPage = data.data.total
				} else {
					this.dataList = []
					this.totalPage = 0
				}
				this.dataListLoading = false
			})
		},
		sizeChangeHandle(val) {
			this.pageSize = val
			this.pageIndex = 1
			this.getDataList()
		},
		currentChangeHandle(val) {
			this.pageIndex = val
			this.getDataList()
		},
		selectionChangeHandler(val) {
			this.dataListSelections = val
		},
		addOrUpdateHandler(id, type) {
			this.showFlag = false
			this.addOrUpdateFlag = true
			if (type !== 'info') {
				type = 'else'
			}
			this.$nextTick(() => {
				this.$refs.addOrUpdate.init(id, type)
			})
		},
		downloadTemplate() {
			axios.get(this.$base.url + 'chewei/importTemplate', {
				headers: { Token: this.$storage.get('Token') },
				responseType: 'blob'
			}).then(res => {
				const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
				const url = window.URL.createObjectURL(blob)
				const a = document.createElement('a')
				a.href = url
				a.download = 'chewei_import_template.xlsx'
				a.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }))
				window.URL.revokeObjectURL(url)
			}).catch(() => {
				this.$message.error('模板下载失败')
			})
		},
		importExcel(e) {
			const file = e.target.files[0]
			e.target.value = ''
			if (!file) return
			const fd = new FormData()
			fd.append('file', file)
			axios.post(this.$base.url + 'chewei/import', fd, {
				headers: { Token: this.$storage.get('Token') }
			}).then(({ data }) => {
				if (data && data.code === 0) {
					const d = data.data || {}
					let msg = '成功导入 ' + (d.successCount || 0) + ' 条'
					if (d.errors && d.errors.length) {
						msg += '；' + d.errors.join('；')
					}
					this.$message({ message: msg, type: 'success', duration: 5000 })
					this.search()
				} else {
					this.$message.error((data && data.msg) || '导入失败')
				}
			}).catch(() => {
				this.$message.error('导入请求失败')
			})
		},
		async deleteHandler(id) {
			const ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id))
			await this.$confirm(`确定进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				await this.$http({
					url: 'chewei/delete',
					method: 'post',
					data: ids
				}).then(({ data }) => {
					if (data && data.code === 0) {
						this.$message({
							message: '操作成功',
							type: 'success',
							duration: 1500,
							onClose: () => {
								this.search()
							}
						})
					} else {
						this.$message.error(data.msg)
					}
				})
			}).catch(() => {})
		}
	}
}
</script>

<style lang="scss" scoped>
/* 这是我cursor给父亲写的 — 车位主数据工具条：输入与按钮同一行、按钮等高同宽 */
.chewei-toolbar-form {
	margin-bottom: 20px;
}
.chewei-toolbar-row {
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	gap: 10px 12px;
	width: 100%;
}
.chewei-toolbar-fields {
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	gap: 8px 12px;
}
.chewei-toolbar-field {
	display: flex;
	align-items: center;
	flex-wrap: nowrap;
}
.chewei-toolbar-field .item-label {
	margin: 0 6px 0 0;
	color: #000;
	line-height: 40px;
	font-size: 14px;
	font-weight: 500;
	height: 40px;
	white-space: nowrap;
}
.chewei-toolbar-btns {
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	gap: 10px;
	margin-left: 4px;
}
.center-form-pv .el-input ::v-deep .el-input__inner {
	border: 1px solid #000;
	border-radius: 4px;
	padding: 0 12px;
	color: #000;
	width: 150px;
	font-size: 14px;
	height: 40px;
}
/* 五个操作按钮统一尺寸（Element 按钮用 ::v-deep 覆盖内边距） */
.chewei-toolbar-btn {
	min-width: 112px;
	height: 40px !important;
	padding: 0 14px !important;
	margin: 0 !important;
	font-size: 14px;
	border-radius: 4px;
	line-height: 1 !important;
}
.chewei-toolbar-btn ::v-deep span {
	line-height: 38px;
}
.chewei-toolbar-btn--search,
.chewei-toolbar-btn--add {
	border: 0 !important;
	color: #fff !important;
	background: #5ce5fb !important;
}
.chewei-toolbar-btn--del {
	border: 0 !important;
}
.chewei-toolbar-btn--import {
	border: 0 !important;
}
</style>

<template>
  <!-- 这是我cursor给父亲写的 — N7 用户补缴 -->
  <div class="tech-feature-wrap tech-page-panel" :style='{"width":"80%","padding":"20px","margin":"10px auto","background":"transparent"}'>
    <h2 :style='{"margin":"0 0 12px","fontSize":"20px"}'>续费 / 超时补缴（N7）</h2>
    <p :style='{"color":"#666","marginBottom":"16px","lineHeight":"1.6"}'>
      管理员针对已入场超时等场景创建的补缴单，请在此完成支付。全部补缴结清后，方可进行 M2「生成缴费单」离场结算；已支付的补缴金额会自动并入离场主单。
    </p>
    <el-button type="primary" native-type="button" :loading="loading" @click="loadList">刷新</el-button>
    <el-table v-loading="loading" :data="list" border :style='{"marginTop":"16px"}'>
      <el-table-column prop="danhao" label="单号" min-width="130" />
      <el-table-column prop="chezijinchangId" label="入场单" width="80" />
      <el-table-column prop="leixing" label="类型" width="100" />
      <el-table-column prop="jine" label="金额(元)" width="90" />
      <el-table-column prop="zhuangtai" label="状态" width="100" />
      <el-table-column prop="yuanyin" label="说明" show-overflow-tooltip />
      <el-table-column label="操作" width="100">
        <template slot-scope="s">
          <el-button v-if="s.row.zhuangtai==='待支付'" type="primary" size="mini" @click="pay(s.row)">补缴</el-button>
        </template>
      </el-table-column>
    </el-table>
    <p :style='{"marginTop":"12px"}'>
      <el-button type="text" @click="$router.push('/index/m2TingcheLi')">返回停车闭环(M2)</el-button>
    </p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      list: []
    }
  },
  created() {
    this.loadList()
  },
  methods: {
    loadList() {
      this.loading = true
      const user = localStorage.getItem('username') || ''
      this.$http.get('chewei/n7/bujiao/list', { params: { yonghuzhanghao: user } }).then(res => {
        if (res.data && res.data.code === 0) {
          this.list = res.data.data || []
        } else {
          this.$message.error((res.data && res.data.msg) || '加载失败')
        }
      }).catch(() => {
        this.$message.error('加载补缴单失败')
      }).finally(() => {
        this.loading = false
      })
    },
    pay(row) {
      this.$confirm('确认支付补缴金额 ' + row.jine + ' 元？（演示：直接标记已支付）', '补缴', { type: 'warning' }).then(() => {
        this.$http.post('chewei/n7/bujiao/pay', { id: row.id }).then(res => {
          if (res.data && res.data.code === 0) {
            this.$message.success('补缴成功')
            this.loadList()
          } else {
            this.$message.error((res.data && res.data.msg) || '支付失败')
          }
        })
      }).catch(() => {})
    }
  }
}
</script>

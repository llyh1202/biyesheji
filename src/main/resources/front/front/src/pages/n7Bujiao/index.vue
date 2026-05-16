<template>
  <!-- 这是我cursor给父亲写的 — N7 用户补缴；P1-24 支持从 M2 离场引导进入并回跳 -->
  <div class="tech-feature-wrap tech-page-panel" :style='{"width":"80%","padding":"20px","margin":"10px auto","background":"transparent"}'>
    <h2 :style='{"margin":"0 0 12px","fontSize":"20px"}'>续费 / 超时补缴（N7）</h2>
    <p :style='{"color":"#666","marginBottom":"16px","lineHeight":"1.6"}'>
      管理员针对已入场超时等场景创建的补缴单，请在此完成支付。全部补缴结清后，方可进行 M2「生成缴费单」离场结算；已支付的补缴金额会自动并入离场主单。
    </p>
    <el-alert
      v-if="fromM2"
      title="您从停车闭环(M2)离场进入：请完成下方待支付补缴，结清后将自动返回 M2 继续离场。"
      type="warning"
      :closable="false"
      show-icon
      :style='{"marginBottom":"16px"}'
    />
    <el-alert
      v-if="fromM2 && focusChezijinchangId"
      :title="'当前入场单 #' + focusChezijinchangId + ' 的待支付补缴'"
      type="info"
      :closable="false"
      show-icon
      :style='{"marginBottom":"12px"}'
    />
    <el-button type="primary" native-type="button" :loading="loading" @click="loadList">刷新</el-button>
    <el-table v-loading="loading" :data="displayList" border :style='{"marginTop":"16px"}'>
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
      <el-button type="text" @click="backToM2">返回停车闭环(M2)</el-button>
    </p>
  </div>
</template>

<script>
import { readM2LichangReturnContext, clearM2LichangReturnContext } from '@/common/auth'

export default {
  data() {
    return {
      loading: false,
      list: [],
      fromM2: false,
      focusChezijinchangId: ''
    }
  },
  computed: {
    displayList() {
      if (!this.fromM2 || !this.focusChezijinchangId) {
        return this.list
      }
      const cid = String(this.focusChezijinchangId)
      const pending = this.list.filter(r => String(r.chezijinchangId) === cid && r.zhuangtai === '待支付')
      const rest = this.list.filter(r => !(String(r.chezijinchangId) === cid && r.zhuangtai === '待支付'))
      return pending.concat(rest)
    }
  },
  created() {
    const q = this.$route.query || {}
    this.fromM2 = q.fromM2 === '1' || q.fromM2 === 1 || q.fromM2 === true
    if (q.chezijinchangId) {
      this.focusChezijinchangId = String(q.chezijinchangId)
    } else {
      const ctx = readM2LichangReturnContext()
      if (ctx && ctx.chezijinchangId) {
        this.focusChezijinchangId = String(ctx.chezijinchangId)
        if (ctx.fromM2) this.fromM2 = true
      }
    }
    this.loadList()
  },
  methods: {
    loadList() {
      this.loading = true
      const user = localStorage.getItem('username') || ''
      const req = this.focusChezijinchangId
        ? this.$http.get('chewei/n7/bujiao/list', { params: { chezijinchangId: this.focusChezijinchangId } })
        : this.$http.get('chewei/n7/bujiao/list', { params: { yonghuzhanghao: user } })
      req.then(res => {
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
            // 这是我cursor给父亲写的 — P1-24 补缴结清后回 M2 离场
            this.checkReturnToM2AfterPay(row.chezijinchangId)
          } else {
            this.$message.error((res.data && res.data.msg) || '支付失败')
          }
        })
      }).catch(() => {})
    },
    checkReturnToM2AfterPay(chezijinchangId) {
      const ctx = readM2LichangReturnContext() || {}
      const cid = String(chezijinchangId || this.focusChezijinchangId || ctx.chezijinchangId || '')
      if (!cid) {
        this.loadList()
        return
      }
      this.$http.get('chewei/n7/bujiao/list', { params: { chezijinchangId: cid } }).then(res => {
        if (!(res.data && res.data.code === 0)) {
          this.loadList()
          return
        }
        const rows = res.data.data || []
        this.list = rows
        const pending = rows.filter(r => r.zhuangtai === '待支付')
        if (!this.fromM2 && !ctx.fromM2) return
        if (pending.length > 0) {
          this.$message.info('仍有 ' + pending.length + ' 笔待支付补缴，请继续完成')
          return
        }
        const query = {
          chezijinchangId: cid,
          step: '3',
          bujiaoDone: '1'
        }
        if (ctx.yuyueId) query.yuyueId = String(ctx.yuyueId)
        this.$message.success('补缴已全部结清，正在返回 M2 继续离场…')
        this.$router.push({ path: '/index/m2TingcheLi', query })
      }).catch(() => {
        this.loadList()
      })
    },
    backToM2() {
      const ctx = readM2LichangReturnContext()
      if (ctx && ctx.fromM2 && ctx.chezijinchangId) {
        const query = { chezijinchangId: String(ctx.chezijinchangId), step: '3' }
        if (ctx.yuyueId) query.yuyueId = String(ctx.yuyueId)
        this.$router.push({ path: '/index/m2TingcheLi', query })
        return
      }
      this.$router.push('/index/m2TingcheLi')
    }
  }
}
</script>

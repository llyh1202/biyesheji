<template>
  <!-- 这是我cursor给父亲写的 — M5 费用试算 -->
  <div :style='{"width":"80%","padding":"20px","margin":"10px auto","background":"#fff"}'>
    <h2 :style='{"margin":"0 0 8px","fontSize":"20px"}'>停车费用试算（M5）</h2>
    <p :style='{"color":"#666","marginBottom":"12px","lineHeight":"1.6"}'>与离场结算同一套规则：N6 宽限期 + 首小时/阶梯/封顶。</p>
    <el-button size="small" @click="fillLot('停车场名称1','A区',1)">示例：停车场1（首小时+封顶）</el-button>
    <el-button size="small" @click="fillLot('停车场名称2','A区',2)">示例：停车场2（纯小时）</el-button>
    <el-alert v-if="ruleHint" :title="ruleHint" type="success" :closable="false" :style='{"margin":"12px 0"}' />
    <el-form label-width="110px">
      <el-form-item label="停车场">
        <el-input v-model="form.tingchechangmingcheng" @blur="loadRule" />
      </el-form-item>
      <el-form-item label="区域">
        <el-input v-model="form.quyu" @blur="loadRule" />
      </el-form-item>
      <el-form-item label="小时单价">
        <el-input-number v-model="form.xiaoshidanjia" :min="1" />
        <span :style='{"marginLeft":"8px","color":"#909399","fontSize":"12px"}'>入场单单价，规则内单价优先</span>
      </el-form-item>
      <el-form-item label="进场时间">
        <el-date-picker v-model="form.jinchangshijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:260px" />
      </el-form-item>
      <el-form-item label="离场时间">
        <el-date-picker v-model="form.lichangshijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:260px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="calc">试算费用</el-button>
        <el-button @click="setLichangNow">离场时间设为当前</el-button>
      </el-form-item>
    </el-form>
    <el-card v-if="result" shadow="never">
      <div><b>应付：</b>{{ result.fee }} 元 <span v-if="result.capped">（已封顶）</span></div>
      <div :style='{"marginTop":"8px","color":"#666"}'>{{ result.breakdown }}</div>
      <div :style='{"marginTop":"4px","fontSize":"13px"}'>计费模式：{{ result.jifeiMoshi }}；宽限 {{ result.graceMinutes }} 分；计费时长 {{ result.billHours }} 小时</div>
    </el-card>
    <p :style='{"marginTop":"12px","color":"#909399","fontSize":"13px"}'>规则维护：管理端 → 车位信息管理 → 计费规则</p>
  </div>
</template>
<script>
export default {
  data() {
    return {
      loading: false,
      form: {
        tingchechangmingcheng: '停车场名称1',
        quyu: 'A区',
        xiaoshidanjia: 1,
        jinchangshijian: '',
        lichangshijian: ''
      },
      result: null,
      jifeiRule: null
    }
  },
  computed: {
    ruleHint() {
      const g = this.jifeiRule
      if (!g) return ''
      let s = '当前规则：' + (g.guizeMingcheng || g.jifeiMoshi)
      s += '（' + (g.jifeiMoshi || '') + '）'
      if (g.shouxiaoshiYuan) s += '，首小时 ' + g.shouxiaoshiYuan + ' 元'
      if (g.jietiDanjia) s += '，后续 ' + g.jietiDanjia + ' 元/时'
      if (g.fengdingYuan) s += '，封顶 ' + g.fengdingYuan + ' 元'
      return s
    }
  },
  created() {
    const q = this.$route.query || {}
    if (q.tingchechangmingcheng) this.form.tingchechangmingcheng = q.tingchechangmingcheng
    if (q.quyu) this.form.quyu = q.quyu
    if (q.xiaoshidanjia) this.form.xiaoshidanjia = parseInt(q.xiaoshidanjia, 10) || 1
    if (q.jinchangshijian) this.form.jinchangshijian = q.jinchangshijian
    if (q.lichangshijian) this.form.lichangshijian = q.lichangshijian
    if (!this.form.lichangshijian) this.setLichangNow()
    if (!this.form.jinchangshijian) {
      const d = new Date(Date.now() - 3 * 3600 * 1000)
      this.form.jinchangshijian = this.formatDate(d)
    }
    this.loadRule()
  },
  methods: {
    formatDate(d) {
      const p = n => (n < 10 ? '0' : '') + n
      return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) + ' ' + p(d.getHours()) + ':' + p(d.getMinutes()) + ':' + p(d.getSeconds())
    },
    setLichangNow() {
      this.form.lichangshijian = this.formatDate(new Date())
    },
    fillLot(lot, quyu, price) {
      this.form.tingchechangmingcheng = lot
      this.form.quyu = quyu
      this.form.xiaoshidanjia = price
      this.loadRule()
    },
    loadRule() {
      if (!this.form.tingchechangmingcheng) return
      this.$http.get('chewei/m5/guize/resolve', {
        params: { tingchechangmingcheng: this.form.tingchechangmingcheng, quyu: this.form.quyu || '' }
      }).then(res => {
        if (res.data && res.data.code === 0) this.jifeiRule = res.data.data
      })
    },
    calc() {
      if (!this.form.jinchangshijian || !this.form.lichangshijian) {
        this.$message.warning('请填写进场与离场时间')
        return
      }
      this.loading = true
      this.$http.post('chewei/m5/jifei/calc', this.form).then(res => {
        if (res.data && res.data.code === 0) {
          this.result = res.data.data
        } else {
          this.$message.error((res.data && res.data.msg) || '试算失败')
        }
      }).finally(() => { this.loading = false })
    }
  }
}
</script>

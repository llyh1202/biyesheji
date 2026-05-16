<template>
  <div class="tech-feature-wrap tech-page-panel" :style='{"width":"80%","padding":"20px","margin":"10px auto","position":"relative","background":"transparent","zIndex":1}'>
    <h2 :style='{"margin":"0 0 8px","fontSize":"20px"}'>停车业务闭环（M2）</h2>
    <p :style='{"color":"#666","margin":"0 0 16px","lineHeight":"1.6"}'>
      预约单 id 填数据库主键<strong>数字</strong>（如 <code>1</code>），不要填表名 <code>chewei_yuyue.1</code>。流程：读预约 → 入场 → 离场生成缴费单 → 关单。
    </p>
    <el-alert
      v-if="!hasToken"
      title="入场、离场、结算须先登录；未登录将跳转登录页。"
      type="warning"
      :closable="false"
      show-icon
      :style='{"marginBottom":"16px"}'
    />

    <el-card shadow="never" :style='{"marginBottom":"16px"}'>
      <div slot="header">1. 查询预约快照</div>
      <el-form inline @submit.native.prevent="loadSnapshot">
        <el-form-item label="预约单 id">
          <el-input v-model="yuyueIdInput" placeholder="仅数字，例如 1" clearable style="width:220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="button" :loading="loadingSnapshot" @click="loadSnapshot">加载</el-button>
        </el-form-item>
      </el-form>
      <div v-if="snapshot && snapshot.yuyue" :style='{"fontSize":"14px","lineHeight":"1.8"}'>
        <div>车位 id：{{ snapshot.yuyue.cheweiId }}，流程：{{ snapshot.yuyue.liuchengJiedian || '—' }}，预约态：{{ snapshot.yuyue.zhuangtai }}</div>
        <div>时段：{{ snapshot.yuyue.kaishiShijian }} ~ {{ snapshot.yuyue.jieshuShijian }}</div>
        <div v-if="snapshot.chewei">车位编号：{{ snapshot.chewei.cheweibianhao }}，当前车位状态：{{ snapshot.chewei.zhuangtai }}</div>
        <div v-if="snapshot.cheweixinxi">停车场：{{ snapshot.cheweixinxi.tingchechangmingcheng }} / {{ snapshot.cheweixinxi.quyu }}，小时单价：{{ snapshot.cheweixinxi.xiaoshidanjia }}</div>
        <el-alert v-if="jifeiRule" :title="jifeiRuleHint" type="success" :closable="false" :style='{"marginTop":"10px"}' />
        <div v-if="snapshot.hint" :style='{"color":"#909399","marginTop":"8px"}'>{{ snapshot.hint }}</div>
      </div>
    </el-card>

    <el-card shadow="never" :style='{"marginBottom":"16px"}'>
      <div slot="header">2. 预约校验后入场</div>
      <el-form label-width="110px" :model="ruchangForm" @submit.native.prevent="doM2Ruchang">
        <el-form-item label="预约单 id">
          <el-input v-model="ruchangForm.yuyueId" :disabled="!!lockedYuyueId" placeholder="数字 id" />
        </el-form-item>
        <el-form-item label="进场时间">
          <el-date-picker v-model="ruchangForm.jinchangshijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="默认当前" style="width:260px" />
        </el-form-item>
        <el-form-item label="用户账号">
          <el-input v-model="ruchangForm.yonghuzhanghao" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="ruchangForm.xingming" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="ruchangForm.shouji" />
        </el-form-item>
        <el-form-item label="车牌号" required>
          <el-input v-model="ruchangForm.chepaihao" placeholder="必填" />
        </el-form-item>
        <el-form-item label="车辆图片">
          <el-input v-model="ruchangForm.cheliangtupian" placeholder="可选，逗号分隔相对路径" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="button" :loading="loadingRuchang" @click="doM2Ruchang">提交入场</el-button>
        </el-form-item>
      </el-form>
      <div v-if="ruchangResult" :style='{"marginTop":"12px"}'>
        入场单 id：<b>{{ ruchangResult.ruchang && ruchangResult.ruchang.id }}</b>
        ，预约 id：<b>{{ ruchangResult.yuyueId }}</b>
      </div>
    </el-card>

    <el-card shadow="never" :style='{"marginBottom":"16px"}'>
      <div slot="header">2.5 补缴单（N7）</div>
      <el-button size="small" native-type="button" @click="loadBujiao">刷新补缴单</el-button>
      <el-button size="small" type="text" @click="$router.push('/index/n7Bujiao')">去补缴</el-button>
      <el-table v-if="bujiaoList.length" :data="bujiaoList" size="small" border :style='{"marginTop":"10px"}'>
        <el-table-column prop="danhao" label="单号" />
        <el-table-column prop="leixing" label="类型" width="90" />
        <el-table-column prop="jine" label="金额" width="70" />
        <el-table-column prop="zhuangtai" label="状态" width="90" />
      </el-table>
    </el-card>

    <el-card shadow="never" :style='{"marginBottom":"16px"}'>
      <div slot="header">3. 离场 → 生成缴费单（M5 统一计费）</div>
      <el-form inline @submit.native.prevent="doLichang">
        <el-form-item label="入场单 id">
          <el-input v-model="lichangForm.chezijinchangId" placeholder="数字 id" />
        </el-form-item>
        <el-form-item label="预约单 id">
          <el-input v-model="lichangForm.yuyueId" placeholder="数字 id，建议填写" />
        </el-form-item>
        <el-form-item label="离场时间">
          <el-date-picker v-model="lichangForm.lichangshijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="info" native-type="button" :loading="loadingPreview" @click="previewFee">试算停车费</el-button>
          <el-button type="warning" native-type="button" :loading="loadingLichang" @click="doLichang">生成缴费单</el-button>
          <el-button type="text" @click="goM5Preview">打开 M5 试算页</el-button>
        </el-form-item>
      </el-form>
      <el-alert v-if="previewJifei" :title="'试算：' + previewJifei.fee + ' 元 — ' + previewJifei.breakdown" type="info" :closable="false" show-icon :style='{"marginTop":"8px"}' />
      <div v-if="orderAfterLichang" :style='{"marginTop":"8px"}'>
        缴费单 id：<b>{{ orderAfterLichang.id }}</b>，金额：<b>{{ orderAfterLichang.bencitingchefeiyong }}</b>，状态：{{ orderAfterLichang.ispay }}
      </div>
      <div v-if="lastJifeiCalc" :style='{"marginTop":"6px","fontSize":"13px","color":"#606266"}'>
        M5 明细：{{ lastJifeiCalc.breakdown }}（模式 {{ lastJifeiCalc.jifeiMoshi }}，宽限 {{ lastJifeiCalc.graceMinutes }} 分）
      </div>
    </el-card>

    <el-card shadow="never">
      <div slot="header">4. 关单（标记已支付）/ 再去支付页</div>
      <el-form inline @submit.native.prevent="doJiesuan">
        <el-form-item label="缴费单 id">
          <el-input v-model="jiesuanId" placeholder="数字 id" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" native-type="button" :loading="loadingJiesuan" @click="doJiesuan">模拟结算（关单）</el-button>
          <el-button native-type="button" @click="goPayDetail">打开支付详情</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
// 这是我cursor给父亲写的 — P1-10 M2 闭环关键接口须登录
import { requireFrontLogin, handleAuthFail } from '@/common/auth'

export default {
  data() {
    return {
      hasToken: !!localStorage.getItem('frontToken'),
      yuyueIdInput: '',
      lockedYuyueId: null,
      snapshot: null,
      loadingSnapshot: false,
      loadingRuchang: false,
      loadingLichang: false,
      loadingPreview: false,
      loadingJiesuan: false,
      previewJifei: null,
      lastJifeiCalc: null,
      jifeiRule: null,
      ruchangForm: {
        yuyueId: '',
        jinchangshijian: '',
        yonghuzhanghao: '',
        xingming: '',
        shouji: '',
        chepaihao: '',
        cheliangtupian: ''
      },
      ruchangResult: null,
      lichangForm: {
        chezijinchangId: '',
        yuyueId: '',
        lichangshijian: ''
      },
      orderAfterLichang: null,
      jiesuanId: '',
      bujiaoList: []
    }
  },
  computed: {
    // 这是我cursor给父亲写的 — 当前匹配到的 M5 计费规则说明
    jifeiRuleHint() {
      const g = this.jifeiRule
      if (!g) return ''
      let s = 'M5 规则：' + (g.guizeMingcheng || '') + '，模式「' + (g.jifeiMoshi || '') + '」'
      if (g.fengdingYuan) s += '，封顶 ' + g.fengdingYuan + ' 元'
      if (g.shouxiaoshiYuan) s += '，首小时 ' + g.shouxiaoshiYuan + ' 元'
      if (g.jietiDanjia) s += '，后续 ' + g.jietiDanjia + ' 元/时'
      return s
    }
  },
  created() {
    const q = this.$route.query || {}
    if (q.yuyueId) {
      this.yuyueIdInput = String(q.yuyueId)
      this.ruchangForm.yuyueId = String(q.yuyueId)
      this.lockedYuyueId = String(q.yuyueId)
      this.lichangForm.yuyueId = String(q.yuyueId)
      this.loadSnapshot()
    }
    if (q.chezijinchangId) {
      this.lichangForm.chezijinchangId = String(q.chezijinchangId)
      this.loadChain(q.chezijinchangId)
    }
    const un = localStorage.getItem('username')
    if (un) {
      this.ruchangForm.yonghuzhanghao = un
    }
  },
  methods: {
    // 这是我cursor给父亲写的 — 预约/入场/缴费单 id 须为纯数字主键
    parseId(val, label) {
      if (val === null || val === undefined || String(val).trim() === '') {
        this.$message.warning('请填写' + label + '（纯数字，如 1）')
        return null
      }
      const s = String(val).trim()
      if (!/^\d+$/.test(s)) {
        this.$message.error(label + '须为数字主键，勿填表名（错误示例：chewei_yuyue.1）')
        return null
      }
      return parseInt(s, 10)
    },
    onHttpFail(err, fallback) {
      if (handleAuthFail(this, err)) {
        return
      }
      const body = err && err.body
      if (body && handleAuthFail(this, body)) {
        return
      }
      const msg = (body && body.msg) || (err && err.message) || fallback || '请求失败，请确认后端已启动且地址为 ' + (this.$config.baseUrl || '')
      this.$message.error(msg)
    },
    loadSnapshot() {
      const id = this.parseId(this.yuyueIdInput || this.ruchangForm.yuyueId, '预约单 id')
      if (id == null) {
        return
      }
      this.loadingSnapshot = true
      this.$http.get('n3/tingcheli/m2/yuyue/snapshot', { params: { yuyueId: id } }).then(res => {
        if (res.data && res.data.code === 0) {
          this.snapshot = res.data.data
          this.ruchangForm.yuyueId = String(id)
          this.lichangForm.yuyueId = String(id)
          this.loadJifeiRule()
          this.$message.success('已加载预约快照')
        } else {
          this.$message.error((res.data && res.data.msg) || '加载失败')
        }
      }).catch(err => {
        this.onHttpFail(err, '加载快照失败')
      }).finally(() => {
        this.loadingSnapshot = false
      })
    },
    loadChain(cid) {
      const id = this.parseId(cid, '入场单 id')
      if (id == null) {
        return
      }
      this.$http.get('n3/tingcheli/chain', { params: { chezijinchangId: id } }).then(res => {
        if (res.data && res.data.code === 0) {
          const d = res.data.data || {}
          if (d.ruchang) {
            this.ruchangResult = { ruchang: d.ruchang, yuyueId: this.lichangForm.yuyueId }
            this.lichangForm.chezijinchangId = String(d.ruchang.id)
          }
          if (d.yuyue && d.yuyue.id) {
            this.lichangForm.yuyueId = String(d.yuyue.id)
            this.ruchangForm.yuyueId = String(d.yuyue.id)
            this.yuyueIdInput = String(d.yuyue.id)
            this.snapshot = { yuyue: d.yuyue, chewei: d.chewei, hint: '已从业务链带出关联预约单。' }
          }
          if (d.bujiaoList) {
            this.bujiaoList = d.bujiaoList
          }
          this.loadBujiao()
          this.loadJifeiRule()
          this.$message.success('已加载入场业务链')
        } else {
          this.$message.error((res.data && res.data.msg) || '加载失败')
        }
      }).catch(err => {
        this.onHttpFail(err, '加载业务链失败')
      })
    },
    doM2Ruchang() {
      if (!requireFrontLogin(this)) {
        return
      }
      const yuyueId = this.parseId(this.ruchangForm.yuyueId, '预约单 id')
      if (yuyueId == null) {
        return
      }
      if (!(this.ruchangForm.chepaihao || '').trim()) {
        this.$message.error('请填写车牌号')
        return
      }
      const body = {
        yuyueId: yuyueId,
        chepaihao: (this.ruchangForm.chepaihao || '').trim(),
        yonghuzhanghao: (this.ruchangForm.yonghuzhanghao || '').trim(),
        xingming: (this.ruchangForm.xingming || '').trim(),
        shouji: (this.ruchangForm.shouji || '').trim(),
        cheliangtupian: (this.ruchangForm.cheliangtupian || '').trim()
      }
      if (this.ruchangForm.jinchangshijian) {
        body.jinchangshijian = this.ruchangForm.jinchangshijian
      }
      this.loadingRuchang = true
      this.$http.post('n3/tingcheli/m2/ruchang', body).then(res => {
        if (res.data && res.data.code === 0) {
          this.ruchangResult = res.data.data
          const r = res.data.data && res.data.data.ruchang
          if (r && r.id) {
            this.lichangForm.chezijinchangId = String(r.id)
            if (res.data.data.yuyueId) {
              this.lichangForm.yuyueId = String(res.data.data.yuyueId)
            }
            this.loadBujiao()
            this.loadJifeiRule()
          }
          this.$message.success('入场成功')
        } else {
          if (handleAuthFail(this, res.data)) {
            return
          }
          this.$message.error((res.data && res.data.msg) || '入场失败')
        }
      }).catch(err => {
        this.onHttpFail(err, '入场请求失败')
      }).finally(() => {
        this.loadingRuchang = false
      })
    },
    // 这是我cursor给父亲写的 — 从入场单/预约快照取停车场上下文
    getM5Context() {
      const r = this.ruchangResult && this.ruchangResult.ruchang
      const y = this.snapshot && this.snapshot.yuyue
      const info = this.snapshot && this.snapshot.cheweixinxi
      const cw = this.snapshot && this.snapshot.chewei
      return {
        tingchechangmingcheng: (r && r.tingchechangmingcheng) || (y && y.tingchechangmingcheng) || (info && info.tingchechangmingcheng) || (cw && cw.tingchechangmingcheng) || '',
        quyu: (r && r.quyu) || (y && y.quyu) || (info && info.quyu) || (cw && cw.quyu) || '',
        xiaoshidanjia: (r && r.xiaoshidanjia) || (info && info.xiaoshidanjia) || 1
      }
    },
    loadJifeiRule() {
      const ctx = this.getM5Context()
      if (!ctx.tingchechangmingcheng) return
      this.$http.get('chewei/m5/guize/resolve', { params: { tingchechangmingcheng: ctx.tingchechangmingcheng, quyu: ctx.quyu } }).then(res => {
        if (res.data && res.data.code === 0) this.jifeiRule = res.data.data
      })
    },
    goM5Preview() {
      const ctx = this.getM5Context()
      const r = this.ruchangResult && this.ruchangResult.ruchang
      this.$router.push({
        path: '/index/m5Jifei',
        query: {
          tingchechangmingcheng: ctx.tingchechangmingcheng,
          quyu: ctx.quyu,
          xiaoshidanjia: ctx.xiaoshidanjia,
          jinchangshijian: r && r.jinchangshijian,
          lichangshijian: this.lichangForm.lichangshijian || ''
        }
      })
    },
    // 这是我cursor给父亲写的 — M5 离场前试算（与 lichang 同一入口）
    buildM5CalcBody() {
      const r = this.ruchangResult && this.ruchangResult.ruchang
      if (!r || !r.jinchangshijian) {
        this.$message.warning('请先完成入场，或去 M5 页手动试算')
        return null
      }
      const ctx = this.getM5Context()
      return {
        jinchangshijian: r.jinchangshijian,
        lichangshijian: this.lichangForm.lichangshijian || this.formatNow(),
        tingchechangmingcheng: ctx.tingchechangmingcheng,
        quyu: ctx.quyu,
        xiaoshidanjia: ctx.xiaoshidanjia
      }
    },
    formatNow() {
      const d = new Date()
      const p = n => (n < 10 ? '0' : '') + n
      return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) + ' ' + p(d.getHours()) + ':' + p(d.getMinutes()) + ':' + p(d.getSeconds())
    },
    previewFee() {
      const body = this.buildM5CalcBody()
      if (!body) return
      this.loadingPreview = true
      this.$http.post('chewei/m5/jifei/calc', body).then(res => {
        if (res.data && res.data.code === 0) {
          this.previewJifei = res.data.data
          this.$message.success('试算完成')
        } else {
          this.$message.error((res.data && res.data.msg) || '试算失败')
        }
      }).catch(err => this.onHttpFail(err, '试算失败')).finally(() => { this.loadingPreview = false })
    },
    doLichang() {
      if (!requireFrontLogin(this)) {
        return
      }
      const chezijinchangId = this.parseId(this.lichangForm.chezijinchangId, '入场单 id')
      if (chezijinchangId == null) {
        return
      }
      const body = { chezijinchangId: chezijinchangId }
      const yuyueRaw = (this.lichangForm.yuyueId || '').trim()
      if (yuyueRaw) {
        const yuyueId = this.parseId(yuyueRaw, '预约单 id')
        if (yuyueId == null) {
          return
        }
        body.yuyueId = yuyueId
      }
      if (this.lichangForm.lichangshijian) {
        body.lichangshijian = this.lichangForm.lichangshijian
      }
      this.loadingLichang = true
      this.$http.post('n3/tingcheli/lichang', body).then(res => {
        if (res.data && res.data.code === 0) {
          const d = res.data.data || {}
          const order = d.order || d
          this.orderAfterLichang = order
          this.lastJifeiCalc = d.jifeiCalc || null
          this.previewJifei = this.lastJifeiCalc
          this.jiesuanId = String(order.id)
          let msg = '已生成缴费单'
          if (d.bujiaoMerged > 0) {
            msg += '（含已支付补缴 ' + d.bujiaoMerged + ' 元）'
          }
          this.$message.success(msg)
        } else {
          if (handleAuthFail(this, res.data)) {
            return
          }
          this.$message.error((res.data && res.data.msg) || '离场失败')
        }
      }).catch(err => {
        this.onHttpFail(err, '离场请求失败')
      }).finally(() => {
        this.loadingLichang = false
      })
    },
    doJiesuan() {
      if (!requireFrontLogin(this)) {
        return
      }
      const tingchejiaofeiId = this.parseId(this.jiesuanId, '缴费单 id')
      if (tingchejiaofeiId == null) {
        return
      }
      this.loadingJiesuan = true
      this.$http.post('n3/tingcheli/jiesuan', { tingchejiaofeiId: tingchejiaofeiId }).then(res => {
        if (res.data && res.data.code === 0) {
          this.$message.success('结算成功')
        } else {
          if (handleAuthFail(this, res.data)) {
            return
          }
          this.$message.error((res.data && res.data.msg) || '结算失败')
        }
      }).catch(err => {
        this.onHttpFail(err, '结算请求失败')
      }).finally(() => {
        this.loadingJiesuan = false
      })
    },
    goPayDetail() {
      const id = this.parseId(this.jiesuanId, '缴费单 id')
      if (id == null) {
        return
      }
      this.$router.push({ path: '/index/tingchejiaofeiDetail', query: { id: id } })
    },
    loadBujiao() {
      const id = this.parseId(this.lichangForm.chezijinchangId, '入场单 id')
      if (id == null) {
        return
      }
      this.$http.get('chewei/n7/bujiao/list', { params: { chezijinchangId: id } }).then(res => {
        if (res.data && res.data.code === 0) {
          this.bujiaoList = res.data.data || []
        }
      })
    }
  }
}
</script>

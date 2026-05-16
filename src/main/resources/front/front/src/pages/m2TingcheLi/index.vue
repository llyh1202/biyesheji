<template>
  <div :style='{"width":"80%","padding":"20px","margin":"10px auto","position":"relative","background":"#fff"}'>
    <h2 :style='{"margin":"0 0 8px","fontSize":"20px"}'>停车业务闭环（M2）</h2>
    <p :style='{"color":"#666","margin":"0 0 16px","lineHeight":"1.6"}'>
      先通过「车位时段预约」拿到预约单 id（<code>chewei_yuyue.id</code>），再在本页完成：读预约 → 校验时段 → 入场 → 离场生成缴费单 → 关单/去支付。
    </p>

    <el-card shadow="never" :style='{"marginBottom":"16px"}'>
      <div slot="header">1. 查询预约快照</div>
      <el-form inline>
        <el-form-item label="预约单 id">
          <el-input v-model.number="yuyueIdInput" placeholder="chewei_yuyue 主键" clearable style="width:220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadSnapshot">加载</el-button>
        </el-form-item>
      </el-form>
      <div v-if="snapshot && snapshot.yuyue" :style='{"fontSize":"14px","lineHeight":"1.8"}'>
        <div>车位 id：{{ snapshot.yuyue.cheweiId }}，流程：{{ snapshot.yuyue.liuchengJiedian || '—' }}，预约态：{{ snapshot.yuyue.zhuangtai }}</div>
        <div>时段：{{ snapshot.yuyue.kaishiShijian }} ~ {{ snapshot.yuyue.jieshuShijian }}</div>
        <div v-if="snapshot.chewei">车位编号：{{ snapshot.chewei.cheweibianhao }}，当前车位状态：{{ snapshot.chewei.zhuangtai }}</div>
        <div v-if="snapshot.cheweixinxi">小时单价（来自车位信息）：{{ snapshot.cheweixinxi.xiaoshidanjia }}</div>
        <div v-if="snapshot.hint" :style='{"color":"#909399","marginTop":"8px"}'>{{ snapshot.hint }}</div>
      </div>
    </el-card>

    <el-card shadow="never" :style='{"marginBottom":"16px"}'>
      <div slot="header">2. 预约校验后入场</div>
      <el-form label-width="110px" :model="ruchangForm">
        <el-form-item label="预约单 id">
          <el-input v-model.number="ruchangForm.yuyueId" :disabled="!!lockedYuyueId" />
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
          <el-button type="primary" @click="doM2Ruchang">提交入场</el-button>
        </el-form-item>
      </el-form>
      <div v-if="ruchangResult" :style='{"marginTop":"12px"}'>
        入场单 id：<b>{{ ruchangResult.ruchang && ruchangResult.ruchang.id }}</b>
        ，预约 id：<b>{{ ruchangResult.yuyueId }}</b>
      </div>
    </el-card>

    <el-card shadow="never" :style='{"marginBottom":"16px"}'>
      <div slot="header">3. 离场 → 生成缴费单（触发结算）</div>
      <el-form inline>
        <el-form-item label="入场单 id">
          <el-input v-model.number="lichangForm.chezijinchangId" />
        </el-form-item>
        <el-form-item label="预约单 id">
          <el-input v-model.number="lichangForm.yuyueId" placeholder="M2 建议填写" />
        </el-form-item>
        <el-form-item label="离场时间">
          <el-date-picker v-model="lichangForm.lichangshijian" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" style="width:260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="doLichang">生成缴费单</el-button>
        </el-form-item>
      </el-form>
      <div v-if="orderAfterLichang" :style='{"marginTop":"8px"}'>
        缴费单 id：<b>{{ orderAfterLichang.id }}</b>，金额：<b>{{ orderAfterLichang.bencitingchefeiyong }}</b>，状态：{{ orderAfterLichang.ispay }}
      </div>
    </el-card>

    <el-card shadow="never">
      <div slot="header">4. 关单（标记已支付）/ 再去支付页</div>
      <el-form inline>
        <el-form-item label="缴费单 id">
          <el-input v-model.number="jiesuanId" />
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="doJiesuan">模拟结算（关单）</el-button>
          <el-button @click="goPayDetail">打开支付详情</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      yuyueIdInput: null,
      lockedYuyueId: null,
      snapshot: null,
      ruchangForm: {
        yuyueId: null,
        jinchangshijian: '',
        yonghuzhanghao: '',
        xingming: '',
        shouji: '',
        chepaihao: '',
        cheliangtupian: ''
      },
      ruchangResult: null,
      lichangForm: {
        chezijinchangId: null,
        yuyueId: null,
        lichangshijian: ''
      },
      orderAfterLichang: null,
      jiesuanId: null
    }
  },
  created() {
    const q = this.$route.query || {}
    if (q.yuyueId) {
      this.yuyueIdInput = Number(q.yuyueId)
      this.ruchangForm.yuyueId = Number(q.yuyueId)
      this.lockedYuyueId = Number(q.yuyueId)
      this.lichangForm.yuyueId = Number(q.yuyueId)
      this.loadSnapshot()
    }
    if (q.chezijinchangId) {
      this.lichangForm.chezijinchangId = Number(q.chezijinchangId)
      this.loadChain(Number(q.chezijinchangId))
    }
    const un = localStorage.getItem('username')
    if (un) {
      this.ruchangForm.yonghuzhanghao = un
    }
  },
  methods: {
    loadSnapshot() {
      const id = this.yuyueIdInput || this.ruchangForm.yuyueId
      if (!id) {
        this.$message.error('请填写预约单 id')
        return
      }
      this.$http.get('n3/tingcheli/m2/yuyue/snapshot', { params: { yuyueId: id } }).then(res => {
        if (res.data.code === 0) {
          this.snapshot = res.data.data
          this.ruchangForm.yuyueId = id
          this.lichangForm.yuyueId = id
          this.$message.success('已加载预约快照')
        } else {
          this.$message.error(res.data.msg || '加载失败')
        }
      })
    },
    loadChain(cid) {
      this.$http.get('n3/tingcheli/chain', { params: { chezijinchangId: cid } }).then(res => {
        if (res.data.code === 0) {
          const d = res.data.data || {}
          if (d.ruchang) {
            this.ruchangResult = { ruchang: d.ruchang, yuyueId: this.lichangForm.yuyueId }
            this.lichangForm.chezijinchangId = d.ruchang.id
          }
          if (d.yuyue && d.yuyue.id) {
            this.lichangForm.yuyueId = d.yuyue.id
            this.ruchangForm.yuyueId = d.yuyue.id
            this.yuyueIdInput = d.yuyue.id
            this.snapshot = { yuyue: d.yuyue, chewei: d.chewei, hint: '已从业务链带出关联预约单。' }
          }
          this.$message.success('已加载入场业务链')
        } else {
          this.$message.error(res.data.msg || '加载失败')
        }
      })
    },
    doM2Ruchang() {
      const body = {
        yuyueId: this.ruchangForm.yuyueId,
        chepaihao: (this.ruchangForm.chepaihao || '').trim(),
        yonghuzhanghao: (this.ruchangForm.yonghuzhanghao || '').trim(),
        xingming: (this.ruchangForm.xingming || '').trim(),
        shouji: (this.ruchangForm.shouji || '').trim(),
        cheliangtupian: (this.ruchangForm.cheliangtupian || '').trim()
      }
      if (this.ruchangForm.jinchangshijian) {
        body.jinchangshijian = this.ruchangForm.jinchangshijian
      }
      this.$http.post('n3/tingcheli/m2/ruchang', body).then(res => {
        if (res.data.code === 0) {
          this.ruchangResult = res.data.data
          const r = res.data.data && res.data.data.ruchang
          if (r && r.id) {
            this.lichangForm.chezijinchangId = r.id
            if (res.data.data.yuyueId) {
              this.lichangForm.yuyueId = res.data.data.yuyueId
            }
          }
          this.$message.success('入场成功')
        } else {
          this.$message.error(res.data.msg || '入场失败')
        }
      })
    },
    doLichang() {
      const body = {
        chezijinchangId: this.lichangForm.chezijinchangId,
        yuyueId: this.lichangForm.yuyueId || undefined
      }
      if (this.lichangForm.lichangshijian) {
        body.lichangshijian = this.lichangForm.lichangshijian
      }
      this.$http.post('n3/tingcheli/lichang', body).then(res => {
        if (res.data.code === 0) {
          this.orderAfterLichang = res.data.data
          this.jiesuanId = res.data.data.id
          this.$message.success('已生成缴费单')
        } else {
          this.$message.error(res.data.msg || '离场失败')
        }
      })
    },
    doJiesuan() {
      if (!this.jiesuanId) {
        this.$message.error('请填写缴费单 id')
        return
      }
      this.$http.post('n3/tingcheli/jiesuan', { tingchejiaofeiId: this.jiesuanId }).then(res => {
        if (res.data.code === 0) {
          this.$message.success('结算成功')
        } else {
          this.$message.error(res.data.msg || '结算失败')
        }
      })
    },
    goPayDetail() {
      if (!this.jiesuanId) {
        this.$message.error('请先生成缴费单或填写缴费单 id')
        return
      }
      this.$router.push({ path: '/index/tingchejiaofeiDetail', query: { id: this.jiesuanId } })
    }
  }
}
</script>

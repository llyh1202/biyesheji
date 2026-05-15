/**
 * 系统首页「当天空余车位」报表（运行时注入，不依赖重新 npm build）
 * 依赖 index.html 中的 window.__API_BASE__；接口 /cheweixinxi/kongyuReport
 */
(function () {
  var CHART_BOX_ID = 'kongyuTodayChartInject';
  var ECHARTS_SRC = 'https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js';
  var injectedWrap = null;
  var pollTimer = null;
  var chartInstance = null;

  function apiBase() {
    var b = window.__API_BASE__;
    if (b) return String(b).replace(/\/$/, '');
    var p = location.port;
    var h = location.hostname;
    if ((h === 'localhost' || h === '127.0.0.1') && (p === '8081' || p === '8082' || p === '5173' || p === '9527' || p === '9528')) {
      return location.protocol + '//' + h + ':8080/springboot673i609q';
    }
    return '/springboot673i609q';
  }

  function readToken() {
    var t = localStorage.getItem('Token');
    if (!t) return '';
    try {
      return JSON.parse(t);
    } catch (e) {
      return String(t).replace(/^"|"$/g, '');
    }
  }

  function loadScript(src, cb) {
    var s = document.createElement('script');
    s.src = src;
    s.async = true;
    s.onload = cb;
    s.onerror = function () {
      console.warn('[kongyu-inject] failed to load echarts');
    };
    document.head.appendChild(s);
  }

  function teardown() {
    if (chartInstance) {
      try {
        chartInstance.dispose();
      } catch (e) {}
      chartInstance = null;
      window.__kongyuChart = null;
    }
    if (injectedWrap && injectedWrap.parentNode) {
      injectedWrap.parentNode.removeChild(injectedWrap);
      injectedWrap = null;
    }
  }

  function renderChart(box) {
    if (typeof window.echarts === 'undefined') {
      loadScript(ECHARTS_SRC, function () {
        renderChart(box);
      });
      return;
    }
    if (chartInstance) {
      try {
        chartInstance.dispose();
      } catch (e) {}
    }
    chartInstance = window.echarts.init(box);
    window.__kongyuChart = chartInstance;

    var d = new Date();
    var p2 = function (n) {
      return n < 10 ? '0' + n : '' + n;
    };
    var dateStr = d.getFullYear() + '-' + p2(d.getMonth() + 1) + '-' + p2(d.getDate());

    var url = apiBase() + '/cheweixinxi/kongyuReport';
    fetch(url, {
      method: 'GET',
      headers: {
        Token: readToken(),
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
      .then(function (r) {
        return r.json();
      })
      .then(function (data) {
        var names = [];
        var kongyu = [];
        var yiyong = [];
        if (data && data.code === 0 && data.data && data.data.length) {
          for (var i = 0; i < data.data.length; i++) {
            var row = data.data[i];
            names.push(row.mingcheng || '区域' + (i + 1));
            kongyu.push(parseInt(row.kongyu, 10) || 0);
            yiyong.push(parseInt(row.yiyong, 10) || 0);
          }
        }
        chartInstance.setOption({
          title: {
            text: '当天空余车位',
            subtext: dateStr + ' ｜ 空余 = 车位配置总数 − 在场未离场车辆',
            left: 'center',
            textStyle: { fontSize: 18, fontWeight: 600 },
            subtextStyle: { fontSize: 12, color: '#888' }
          },
          tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
          legend: { data: ['空余车位', '已占用'], top: 48 },
          grid: { left: '3%', right: '4%', bottom: '3%', top: 100, containLabel: true },
          xAxis: {
            type: 'category',
            data: names.length ? names : ['暂无数据'],
            axisLabel: { rotate: names.length > 4 ? 28 : 0 }
          },
          yAxis: { type: 'value', name: '车位数', minInterval: 1 },
          color: ['#67C23A', '#909399'],
          series: [
            {
              name: '空余车位',
              type: 'bar',
              stack: 'chewei',
              barMaxWidth: 48,
              data: names.length ? kongyu : [0]
            },
            {
              name: '已占用',
              type: 'bar',
              stack: 'chewei',
              barMaxWidth: 48,
              data: names.length ? yiyong : [0]
            }
          ]
        });
      })
      .catch(function () {
        chartInstance.setOption({
          title: { text: '当天空余车位', subtext: '数据加载失败', left: 'center' }
        });
      });
  }

  function tryInject() {
    if (location.hash !== '#/') {
      return false;
    }
    var root = document.querySelector('#app .content');
    if (!root) return false;
    if (document.getElementById(CHART_BOX_ID)) return true;
    var title = root.querySelector('.text');
    if (!title || !title.parentNode) return false;

    var wrap = document.createElement('div');
    wrap.className = 'kongyu-center-wrap';
    wrap.setAttribute(
      'style',
      'width:100%;display:flex;justify-content:center;align-items:center;order:1;margin:8px auto 20px;'
    );
    var box = document.createElement('div');
    box.id = CHART_BOX_ID;
    box.setAttribute(
      'style',
      'width:88%;max-width:960px;height:400px;border-radius:8px;padding:8px 12px;background:rgba(255,255,255,0.96);box-shadow:0 1px 8px rgba(0,0,0,0.18);'
    );
    wrap.appendChild(box);
    title.parentNode.insertBefore(wrap, title.nextSibling);
    injectedWrap = wrap;
    renderChart(box);
    return true;
  }

  function startPoll() {
    if (pollTimer) clearInterval(pollTimer);
    var n = 0;
    pollTimer = setInterval(function () {
      n++;
      if (tryInject() || n > 80) {
        clearInterval(pollTimer);
        pollTimer = null;
      }
    }, 250);
  }

  window.addEventListener('hashchange', function () {
    if (location.hash !== '#/') {
      teardown();
    } else {
      startPoll();
    }
  });

  window.addEventListener('resize', function () {
    if (chartInstance) {
      try {
        chartInstance.resize();
      } catch (e) {}
    }
    var el = document.getElementById('tingchejiaofeiChart1');
    if (typeof window.echarts !== 'undefined' && el) {
      var t = window.echarts.getInstanceByDom(el);
      if (t) t.resize();
    }
  });

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', startPoll);
  } else {
    startPoll();
  }

  setInterval(function () {
    if (location.hash === '#/') {
      tryInject();
    }
  }, 1500);
})();

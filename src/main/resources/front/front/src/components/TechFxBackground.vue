<template>
	<!-- 这是我cursor给父亲写的 — 用户端科技感全屏动效层（纯展示，不影响业务逻辑） -->
	<div class="tech-fx-layer" aria-hidden="true">
		<canvas ref="canvas" class="tech-fx-canvas"></canvas>
		<div class="tech-fx-grid"></div>
		<div class="tech-fx-scanline"></div>
		<div class="tech-fx-glow tech-fx-glow-a"></div>
		<div class="tech-fx-glow tech-fx-glow-b"></div>
	</div>
</template>

<script>
export default {
	name: 'TechFxBackground',
	data() {
		return {
			raf: null,
			points: []
		}
	},
	mounted() {
		this.initCanvas()
		window.addEventListener('resize', this.onResize)
	},
	beforeDestroy() {
		window.removeEventListener('resize', this.onResize)
		if (this.raf) cancelAnimationFrame(this.raf)
	},
	methods: {
		onResize() {
			this.initCanvas(true)
		},
		initCanvas(keep) {
			const canvas = this.$refs.canvas
			if (!canvas) return
			const ctx = canvas.getContext('2d')
			const w = window.innerWidth
			const h = window.innerHeight
			canvas.width = w
			canvas.height = h
			if (!keep || !this.points.length) {
				const n = Math.min(120, Math.floor((w * h) / 12000))
				this.points = []
				for (let i = 0; i < n; i++) {
					this.points.push({
						x: Math.random() * w,
						y: Math.random() * h,
						vx: (Math.random() - 0.5) * 0.6,
						vy: (Math.random() - 0.5) * 0.6,
						r: Math.random() * 2 + 0.5
					})
				}
			}
			this.loop(ctx, w, h)
		},
		loop(ctx, w, h) {
			ctx.clearRect(0, 0, w, h)
			const pts = this.points
			for (let i = 0; i < pts.length; i++) {
				const p = pts[i]
				p.x += p.vx
				p.y += p.vy
				if (p.x < 0 || p.x > w) p.vx *= -1
				if (p.y < 0 || p.y > h) p.vy *= -1
				ctx.beginPath()
				ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2)
				ctx.fillStyle = 'rgba(6, 182, 212, 0.75)' // 这是我cursor给父亲写的 — 浅色主题粒子
				ctx.fill()
				for (let j = i + 1; j < pts.length; j++) {
					const q = pts[j]
					const dx = p.x - q.x
					const dy = p.y - q.y
					const dist = Math.sqrt(dx * dx + dy * dy)
					if (dist < 140) {
						ctx.strokeStyle = 'rgba(6, 182, 212, ' + (1 - dist / 140) * 0.22 + ')'
						ctx.lineWidth = 1
						ctx.beginPath()
						ctx.moveTo(p.x, p.y)
						ctx.lineTo(q.x, q.y)
						ctx.stroke()
					}
				}
			}
			this.raf = requestAnimationFrame(() => this.loop(ctx, w, h))
		}
	}
}
</script>

<style scoped>
.tech-fx-layer {
	position: fixed;
	inset: 0;
	z-index: 0;
	pointer-events: none;
	overflow: hidden;
	background: radial-gradient(ellipse at 20% 15%, rgba(6, 182, 212, 0.12), transparent 55%),
		radial-gradient(ellipse at 85% 75%, rgba(124, 58, 237, 0.08), transparent 50%),
		linear-gradient(165deg, #f0f9ff 0%, #e0f2fe 45%, #f8fafc 100%);
}
.tech-fx-canvas {
	position: absolute;
	inset: 0;
	width: 100%;
	height: 100%;
	opacity: 0.75;
}
.tech-fx-grid {
	position: absolute;
	inset: -50%;
	background-image: linear-gradient(rgba(6, 182, 212, 0.08) 1px, transparent 1px),
		linear-gradient(90deg, rgba(6, 182, 212, 0.08) 1px, transparent 1px);
	background-size: 48px 48px;
	transform: perspective(500px) rotateX(58deg);
	animation: techGridDrift 28s linear infinite;
	opacity: 0.35;
}
.tech-fx-scanline {
	position: absolute;
	inset: 0;
	background: repeating-linear-gradient(
		0deg,
		transparent,
		transparent 2px,
		rgba(6, 182, 212, 0.04) 2px,
		rgba(6, 182, 212, 0.04) 4px
	);
	animation: techScan 8s linear infinite;
	opacity: 0.25;
}
.tech-fx-glow {
	position: absolute;
	width: 40vw;
	height: 40vw;
	border-radius: 50%;
	filter: blur(80px);
	opacity: 0.2;
	animation: techPulse 6s ease-in-out infinite;
}
.tech-fx-glow-a {
	top: -10%;
	left: -5%;
	background: #7dd3fc;
}
.tech-fx-glow-b {
	bottom: -15%;
	right: -10%;
	background: #c4b5fd;
	animation-delay: 3s;
}
@keyframes techGridDrift {
	0% { transform: perspective(500px) rotateX(58deg) translateY(0); }
	100% { transform: perspective(500px) rotateX(58deg) translateY(48px); }
}
@keyframes techScan {
	0% { transform: translateY(0); }
	100% { transform: translateY(20px); }
}
@keyframes techPulse {
	0%, 100% { opacity: 0.25; transform: scale(1); }
	50% { opacity: 0.45; transform: scale(1.08); }
}
</style>

# 在单独终端运行后端（勿与杀端口的脚本混在同一管道里结束子进程）。这是我cursor给父亲写的
$repo = Resolve-Path (Join-Path $PSScriptRoot '..')
Set-Location $repo.Path
$mvn = Get-Command mvn.cmd -ErrorAction SilentlyContinue
if ($mvn) {
	& $mvn.Source spring-boot:run
} else {
	$fallback = Join-Path $env:USERPROFILE 'Downloads\apache-maven-3.9.11-bin\apache-maven-3.9.11\bin\mvn.cmd'
	if (Test-Path $fallback) {
		& $fallback spring-boot:run
	} else {
		Write-Error '未找到 mvn.cmd，请将 Maven 加入 PATH 或修改本脚本中的 fallback 路径。'
		exit 1
	}
}

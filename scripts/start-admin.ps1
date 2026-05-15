# 管理端 Vue：请在独立 PowerShell 窗口执行。这是我cursor给父亲写的
Set-Location (Join-Path $PSScriptRoot '..\src\main\resources\admin\admin')
npx -y pnpm@9.15.0 run serve

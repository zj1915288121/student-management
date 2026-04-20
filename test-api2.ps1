$login = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"123456"}'
Write-Host "=== 登录结果 ==="
Write-Host "code: $($login.code)"
Write-Host "message: $($login.message)"
Write-Host "用户名: $($login.data.userInfo.username)"
Write-Host "姓名: $($login.data.userInfo.realName)"
Write-Host "角色: $($login.data.userInfo.roleName)"
Write-Host "token: $($login.data.token.Substring(0, [Math]::Min(30, $login.data.token.Length)))..."

$token = $login.data.token
$headers = @{ Authorization = "Bearer $token" }

Write-Host "`n=== T001 登录 ==="
$t001login = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"T001","password":"123456"}'
Write-Host "用户名: $($t001login.data.userInfo.username)"
Write-Host "姓名: $($t001login.data.userInfo.realName)"
Write-Host "角色: $($t001login.data.userInfo.roleName)"

Write-Host "`n=== /auth/info 接口 ==="
$info = Invoke-RestMethod -Uri "http://localhost:8080/auth/info" -Headers $headers -ContentType "application/json"
Write-Host "realName: $($info.data.realName)"
Write-Host "roleName: $($info.data.roleName)"

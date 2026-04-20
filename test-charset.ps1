$resp = Invoke-WebRequest -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"123456"}'
$resp.Headers | Format-Table -AutoSize
Write-Host "=== Raw Content Bytes ==="
$bytes = [System.Text.Encoding]::UTF8.GetBytes($resp.Content)
Write-Host "Content length: $($resp.Content.Length)"
Write-Host "First 100 bytes hex: $([System.BitConverter]::ToString($bytes[0..99]))"

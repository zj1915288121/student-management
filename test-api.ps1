$login = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body '{"username":"admin","password":"123456"}'
Write-Host "=== LOGIN RESPONSE ==="
$login | ConvertTo-Json -Depth 5

$token = $login.data.token
$headers = @{ Authorization = "Bearer $token" }

Write-Host "`n=== DASHBOARD STATS ==="
try {
    $stats = Invoke-RestMethod -Uri "http://localhost:8080/api/dashboard/stats" -Headers $headers -ContentType "application/json"
    $stats | ConvertTo-Json -Depth 5
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}

Write-Host "`n=== STUDENT LIST ==="
try {
    $students = Invoke-RestMethod -Uri "http://localhost:8080/api/student/page?page=1&size=5" -Headers $headers -ContentType "application/json"
    $students | ConvertTo-Json -Depth 5
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}

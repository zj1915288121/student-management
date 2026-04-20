$body = @{username="admin"; password="123456"} | ConvertTo-Json
$resp = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method POST -ContentType "application/json" -Body $body
$resp | ConvertTo-Json -Depth 5

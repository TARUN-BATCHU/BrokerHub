# BrokerHub Network Access Test Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "BrokerHub Network Access Test Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Get local IP address
$localIP = (Get-NetIPAddress -AddressFamily IPv4 -InterfaceAlias "Wi-Fi*" | Where-Object {$_.IPAddress -like "192.168.*" -or $_.IPAddress -like "10.*"}).IPAddress

if (-not $localIP) {
    $localIP = (Get-NetIPAddress -AddressFamily IPv4 | Where-Object {$_.IPAddress -like "192.168.*" -or $_.IPAddress -like "10.*"}).IPAddress
}

if (-not $localIP) {
    Write-Host "⚠️  Could not detect local IP address automatically" -ForegroundColor Yellow
    Write-Host "Please find your IP address manually using 'ipconfig'" -ForegroundColor Yellow
    $localIP = "YOUR_IP_ADDRESS"
} else {
    Write-Host "✅ Local IP Address detected: $localIP" -ForegroundColor Green
}

Write-Host ""
Write-Host "📱 Access URLs:" -ForegroundColor Yellow
Write-Host "   Local access:     http://localhost:8080" -ForegroundColor White
Write-Host "   Network access:   http://$localIP:8080" -ForegroundColor White
Write-Host "   Network test:     http://$localIP:8080/network-test.html" -ForegroundColor White
Write-Host ""

Write-Host "🔧 Testing Steps:" -ForegroundColor Yellow
Write-Host "   1. Start the application (see below)" -ForegroundColor White
Write-Host "   2. Open http://localhost:8080 on this PC" -ForegroundColor White
Write-Host "   3. Open http://$localIP:8080 on your phone/tablet" -ForegroundColor White
Write-Host "   4. Run network tests at http://$localIP:8080/network-test.html" -ForegroundColor White
Write-Host ""

Write-Host "🚀 Starting BrokerHub application..." -ForegroundColor Green
Write-Host ""

# Check if JAR file exists
if (Test-Path "target\brokerageapp-0.0.1-SNAPSHOT.jar") {
    java -jar target\brokerageapp-0.0.1-SNAPSHOT.jar
} else {
    Write-Host "❌ JAR file not found. Please build the application first:" -ForegroundColor Red
    Write-Host "   mvn clean package -DskipTests" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
}
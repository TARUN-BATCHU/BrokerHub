@echo off
echo ========================================
echo BrokerHub Network Access Test Script
echo ========================================
echo.

echo Starting BrokerHub application...
echo.

REM Get the local IP address
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4 Address"') do (
    for /f "tokens=1" %%b in ("%%a") do (
        set LOCAL_IP=%%b
        goto :found_ip
    )
)

:found_ip
set LOCAL_IP=%LOCAL_IP: =%

echo Your local IP address: %LOCAL_IP%
echo.
echo Access URLs:
echo - Local access: http://localhost:8080
echo - Network access: http://%LOCAL_IP%:8080
echo - Network test page: http://%LOCAL_IP%:8080/network-test.html
echo.

echo Starting the application...
java -jar target\brokerageapp-0.0.1-SNAPSHOT.jar

pause
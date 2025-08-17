@echo off
echo Testing Broker Login API...
echo.

echo Testing with correct URL path:
curl -X POST "http://localhost:8080/BrokerHub/Broker/login" ^
  -H "Content-Type: application/json" ^
  -H "Accept: application/json" ^
  -d "{\"userName\":\"admin\",\"password\":\"admin123\"}"

echo.
echo.
echo If you see "Login successful" above, the API is working correctly.
echo If you see 401 Unauthorized, there might be a Spring Security configuration issue.
pause

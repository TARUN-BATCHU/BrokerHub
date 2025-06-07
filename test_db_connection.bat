@echo off
echo Testing Database Connection...
echo.

echo Checking if brokerHub_multiTenant database exists:
mysql -u root -ptarun -e "SHOW DATABASES LIKE 'brokerHub_multiTenant';"

echo.
echo Checking broker table:
mysql -u root -ptarun -D brokerHub_multiTenant -e "SELECT COUNT(*) as broker_count FROM broker;"

echo.
echo Testing completed.
pause

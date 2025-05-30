@echo off
echo 🚀 Setting up database optimizations for Brokerage App...
echo.

REM Check if MySQL is accessible
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ MySQL not found. Please ensure MySQL is installed and in PATH.
    pause
    exit /b 1
)

echo ✅ MySQL found
echo.

REM Prompt for database credentials
set /p dbUser="Enter MySQL username (default: root): "
if "%dbUser%"=="" set dbUser=root

set /p dbPassword="Enter MySQL password: "
set /p dbName="Enter database name (default: brokerHub): "
if "%dbName%"=="" set dbName=brokerHub

echo.
echo 📊 Creating essential database indexes...
echo.

REM Create the most important indexes (ignore errors if they already exist)
echo Creating daily_ledger indexes...
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_daily_ledger_financial_year ON daily_ledger(financial_year_year_id);" 2>nul
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_daily_ledger_date ON daily_ledger(date);" 2>nul

echo Creating ledger_details indexes...
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_ledger_details_user ON ledger_details(user_id);" 2>nul
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_ledger_details_daily_ledger ON ledger_details(daily_ledger_daily_ledger_id);" 2>nul

echo Creating ledger_record indexes...
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_ledger_record_buyer ON ledger_record(to_buyer_user_id);" 2>nul
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_ledger_record_product ON ledger_record(product_product_id);" 2>nul
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_ledger_record_ledger_details ON ledger_record(ledger_details_ledger_details_id);" 2>nul

echo Creating user and address indexes...
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_user_address ON user(address_id);" 2>nul
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_user_type ON user(user_type);" 2>nul
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_address_city ON address(city);" 2>nul

echo Creating composite indexes for analytics...
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_analytics_monthly ON daily_ledger(financial_year_year_id, date);" 2>nul
mysql -u %dbUser% -p%dbPassword% %dbName% -e "CREATE INDEX idx_user_type_address ON user(user_type, address_id);" 2>nul

echo.
echo ✅ Database indexes created successfully!
echo.
echo 🎉 Database optimization setup complete!
echo.
echo Next steps:
echo 1. Install Redis for Windows (see REDIS_SETUP_WINDOWS.md)
echo 2. Start your Spring Boot application
echo 3. Monitor performance improvements
echo.
pause

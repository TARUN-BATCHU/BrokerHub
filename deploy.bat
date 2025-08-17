@echo off
echo ========================================
echo BrokerHub Deployment Helper
echo ========================================
echo.

echo Step 1: Building the application...
call mvnw clean package -DskipTests
if %errorlevel% neq 0 (
    echo Build failed! Please check for errors.
    pause
    exit /b 1
)

echo.
echo Step 2: Build completed successfully!
echo.
echo Next steps for deployment:
echo 1. Push your code to GitHub
echo 2. Setup Supabase database (see DEPLOYMENT_GUIDE.md)
echo 3. Deploy backend to Railway
echo 4. Deploy frontend to Vercel
echo.
echo Your JAR file is ready at: target\brokerage-app-0.0.1-SNAPSHOT.jar
echo.
echo For detailed instructions, see DEPLOYMENT_GUIDE.md
echo.
pause
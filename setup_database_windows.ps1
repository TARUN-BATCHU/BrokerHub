# Windows PowerShell script to set up database optimizations
# Run this script from the project root directory

Write-Host "🚀 Setting up database optimizations for Brokerage App..." -ForegroundColor Green

# Check if MySQL is accessible
try {
    $mysqlVersion = mysql --version
    Write-Host "✅ MySQL found: $mysqlVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ MySQL not found. Please ensure MySQL is installed and in PATH." -ForegroundColor Red
    exit 1
}

# Prompt for database credentials
$dbUser = Read-Host "Enter MySQL username (default: root)"
if ([string]::IsNullOrEmpty($dbUser)) {
    $dbUser = "root"
}

$dbPassword = Read-Host "Enter MySQL password" -AsSecureString
$dbPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($dbPassword))

$dbName = Read-Host "Enter database name (default: brokerHub)"
if ([string]::IsNullOrEmpty($dbName)) {
    $dbName = "brokerHub"
}

Write-Host "📊 Executing database optimization indexes..." -ForegroundColor Yellow

# Execute the database optimization script
try {
    Get-Content "database_optimization_indexes.sql" | mysql -u $dbUser -p$dbPasswordPlain $dbName
    Write-Host "✅ Database indexes created successfully!" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Some indexes may already exist (this is normal). Check output above for details." -ForegroundColor Yellow
}

Write-Host "🎉 Database optimization setup complete!" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Install Redis for Windows" -ForegroundColor White
Write-Host "2. Start your Spring Boot application" -ForegroundColor White
Write-Host "3. Monitor performance improvements" -ForegroundColor White

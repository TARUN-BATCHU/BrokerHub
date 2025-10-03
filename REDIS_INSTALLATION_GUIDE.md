# Redis Installation Guide for Windows

## Quick Installation (Recommended)

### Method 1: Using MSI Installer
1. Download Redis for Windows: https://github.com/microsoftarchive/redis/releases/tag/win-3.0.504
2. Download `Redis-x64-3.0.504.msi`
3. Run the installer and follow the setup wizard
4. Redis will start automatically as a Windows service

### Method 2: Using Chocolatey (if you have it)
```cmd
choco install redis-64
```

### Method 3: Manual Installation
1. Download `Redis-x64-3.0.504.zip` from the same link above
2. Extract to `C:\Redis`
3. Open Command Prompt as Administrator
4. Navigate to Redis folder: `cd C:\Redis`
5. Install as service: `redis-server --service-install`
6. Start service: `redis-server --service-start`

## Verify Installation

Open Command Prompt and run:
```cmd
redis-cli ping
```
You should see: `PONG`

## Start/Stop Redis Service

**Start Redis:**
```cmd
net start redis
```

**Stop Redis:**
```cmd
net stop redis
```

## Test with Your Application

1. Start Redis service
2. Change `application.properties`:
```properties
spring.cache.type=redis
```
3. Restart your BrokerHub application
4. Check logs for: "Redis template configured successfully"

## Default Configuration
- **Host:** localhost
- **Port:** 6379
- **No password required**

Your application is already configured to use these defaults.

## Troubleshooting

**If Redis won't start:**
- Run Command Prompt as Administrator
- Try: `redis-server --service-start`

**If still having issues:**
- Use the no-Redis profile: `--spring.profiles.active=no-redis`
- The application works perfectly without Redis
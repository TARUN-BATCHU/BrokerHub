# 🔧 Redis Setup for Windows

## 📋 Overview
This guide will help you install and configure Redis on Windows for the optimized brokerage application.

## 🚀 Installation Options

### **Option 1: Using Windows Subsystem for Linux (WSL) - Recommended**

1. **Install WSL2** (if not already installed):
   ```powershell
   wsl --install
   ```

2. **Install Ubuntu** from Microsoft Store

3. **Install Redis in WSL**:
   ```bash
   sudo apt update
   sudo apt install redis-server
   ```

4. **Start Redis**:
   ```bash
   sudo service redis-server start
   ```

5. **Test Redis**:
   ```bash
   redis-cli ping
   # Should return: PONG
   ```

6. **Configure Redis to start automatically**:
   ```bash
   sudo systemctl enable redis-server
   ```

### **Option 2: Using Docker Desktop - Easy Setup**

1. **Install Docker Desktop** from https://www.docker.com/products/docker-desktop

2. **Run Redis Container**:
   ```powershell
   docker run -d --name redis-brokerage -p 6379:6379 redis:latest
   ```

3. **Test Redis**:
   ```powershell
   docker exec -it redis-brokerage redis-cli ping
   # Should return: PONG
   ```

4. **Start Redis automatically** (add to startup):
   ```powershell
   docker update --restart unless-stopped redis-brokerage
   ```

### **Option 3: Native Windows Installation**

1. **Download Redis for Windows**:
   - Go to: https://github.com/microsoftarchive/redis/releases
   - Download the latest `.msi` file

2. **Install Redis**:
   - Run the downloaded `.msi` file
   - Follow installation wizard
   - Choose "Add Redis to PATH" option

3. **Start Redis Service**:
   ```powershell
   # Start as Windows Service
   redis-server --service-install
   redis-server --service-start
   ```

4. **Test Redis**:
   ```powershell
   redis-cli ping
   # Should return: PONG
   ```

## ⚙️ Configuration for Brokerage App

### **Redis Configuration File** (redis.conf)

Create or edit `redis.conf` with these optimizations:

```conf
# Memory optimization
maxmemory 2gb
maxmemory-policy allkeys-lru

# Persistence (for production)
save 900 1
save 300 10
save 60 10000

# Network
bind 127.0.0.1
port 6379
timeout 300

# Performance
tcp-keepalive 300
tcp-backlog 511

# Logging
loglevel notice
logfile "redis-server.log"

# Security (optional)
# requirepass your_secure_password
```

### **Start Redis with Custom Config**:

**WSL/Linux**:
```bash
redis-server /path/to/redis.conf
```

**Docker**:
```powershell
docker run -d --name redis-brokerage -p 6379:6379 -v ${PWD}/redis.conf:/usr/local/etc/redis/redis.conf redis:latest redis-server /usr/local/etc/redis/redis.conf
```

**Windows Native**:
```powershell
redis-server redis.conf
```

## 🧪 Testing Redis Connection

### **PowerShell Test Script**:

```powershell
# Test Redis connectivity
try {
    $result = redis-cli ping
    if ($result -eq "PONG") {
        Write-Host "✅ Redis is running and accessible!" -ForegroundColor Green
    } else {
        Write-Host "❌ Redis connection failed" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Redis CLI not found or Redis not running" -ForegroundColor Red
}

# Test Redis with sample data
redis-cli set test-key "Hello Redis"
$value = redis-cli get test-key
Write-Host "Test value: $value" -ForegroundColor Cyan
redis-cli del test-key
```

## 🔍 Troubleshooting

### **Common Issues**:

1. **"redis-cli not recognized"**:
   - Ensure Redis is in your PATH
   - Restart PowerShell after installation
   - Use full path: `C:\Program Files\Redis\redis-cli.exe`

2. **Connection refused**:
   - Check if Redis service is running
   - Verify port 6379 is not blocked by firewall
   - Check Redis logs for errors

3. **Memory issues**:
   - Adjust `maxmemory` setting in redis.conf
   - Monitor memory usage with `redis-cli info memory`

### **Useful Commands**:

```powershell
# Check Redis status
redis-cli info server

# Monitor Redis activity
redis-cli monitor

# Check memory usage
redis-cli info memory

# List all keys (be careful in production)
redis-cli keys "*"

# Flush all data (DANGER: removes all data)
redis-cli flushall
```

## 🚀 Starting Your Application

Once Redis is running, start your Spring Boot application:

```powershell
# Navigate to project directory
cd "D:\Intellij Projects\brokerage-app"

# Start the application
mvn spring-boot:run
```

The application will automatically connect to Redis on `localhost:6379`.

## 📊 Monitoring Performance

### **Redis Monitoring**:
```powershell
# Real-time monitoring
redis-cli --latency

# Memory usage
redis-cli info memory

# Hit/miss statistics
redis-cli info stats
```

### **Application Logs**:
Check your application logs for cache-related messages:
- Cache hit/miss rates
- Performance improvements
- Any connection issues

## 🎯 Expected Results

After Redis setup, you should see:
- ✅ Faster response times for repeated requests
- ✅ Reduced database load
- ✅ Better application performance under load
- ✅ Cache hit rates in application logs

## 🆘 Need Help?

If you encounter issues:
1. Check Redis logs
2. Verify network connectivity
3. Ensure proper configuration
4. Test with simple redis-cli commands first

---

**Note**: For production environments, consider additional security measures like authentication, SSL/TLS, and network restrictions.

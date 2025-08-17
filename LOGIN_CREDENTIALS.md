# Multi-Tenant Brokerage Application - Login Credentials

## 🔐 **Default Login Credentials**

### **Primary Admin Account**
- **Username:** `admin`
- **Password:** `admin123`
- **Broker Name:** Admin Broker
- **Firm:** Default Brokerage Firm
- **Email:** admin@brokerhub.com

## 🏢 **Additional Test Broker Accounts**

### **Mumbai Broker**
- **Username:** `mumbai_broker`
- **Password:** `hello123`
- **Broker Name:** Mumbai Broker
- **Firm:** Mumbai Trading Co.
- **Email:** mumbai@brokerhub.com

### **Delhi Broker**
- **Username:** `delhi_broker`
- **Password:** `password123`
- **Broker Name:** Delhi Broker
- **Firm:** Delhi Commodities Ltd.
- **Email:** delhi@brokerhub.com

### **Pune Broker**
- **Username:** `pune_broker`
- **Password:** `broker123`
- **Broker Name:** Pune Broker
- **Firm:** Pune Agricultural Exchange
- **Email:** pune@brokerhub.com

### **Chennai Broker**
- **Username:** `chennai_broker`
- **Password:** `south123`
- **Broker Name:** Chennai Broker
- **Firm:** South India Trading Hub
- **Email:** chennai@brokerhub.com

## 🌐 **How to Login**

### **Web Application**
1. Go to your application URL (e.g., `http://localhost:8080`)
2. Use any of the above username/password combinations
3. Each broker will see only their own data

### **API Testing (Postman/Curl)**
Use Basic Authentication with these credentials:

**Example for admin:**
```
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

**Example for mumbai_broker:**
```
Authorization: Basic bXVtYmFpX2Jyb2tlcjpoZWxsbzEyMw==
```

### **Curl Example**
```bash
# Login as admin
curl -u admin:admin123 http://localhost:8080/api/users

# Login as mumbai_broker
curl -u mumbai_broker:hello123 http://localhost:8080/api/products
```

## 🔧 **Setting Up Additional Brokers**

To create more broker accounts, run the `create_broker_accounts.sql` script:

```sql
mysql -u root -p brokerHub_multiTenant < create_broker_accounts.sql
```

## 🧪 **Testing Multi-Tenant Isolation**

1. **Login as admin** - Create some users and products
2. **Login as mumbai_broker** - You should see empty lists (no data from admin)
3. **Create data as mumbai_broker** - This data won't be visible to admin
4. **Switch between accounts** - Verify complete data isolation

## 📊 **Sample Data Included**

Each broker account comes with:
- **2 Sample Addresses** in their respective cities
- **1 Financial Year** (2024-25)
- **3 Sample Products** relevant to their region

### **Admin Broker Sample Data:**
- Products: Rice, Wheat, Sugar
- Addresses: Mumbai (Andheri, Bandra)

### **Mumbai Broker Sample Data:**
- Products: Rice, Pulses, Spices
- Addresses: Mumbai (Kurla, Powai)

### **Delhi Broker Sample Data:**
- Products: Wheat, Barley, Mustard
- Addresses: Delhi (Connaught Place, Karol Bagh)

### **Pune Broker Sample Data:**
- Products: Onions, Potatoes, Tomatoes
- Addresses: Pune (Shivaji Nagar, Kothrud)

### **Chennai Broker Sample Data:**
- Products: Rice, Coconut, Tamarind
- Addresses: Chennai (T. Nagar, Anna Nagar)

## ⚠️ **Important Notes**

1. **Data Isolation:** Each broker can only see and modify their own data
2. **Authentication Required:** All API endpoints require authentication
3. **Case Sensitive:** Usernames and passwords are case-sensitive
4. **Session Management:** Web sessions are broker-specific
5. **Cache Isolation:** Cache data is separated per broker

## 🔒 **Security Features**

- **BCrypt Password Encryption:** All passwords are securely hashed
- **Automatic Tenant Context:** System automatically identifies current broker
- **Cross-Tenant Protection:** Prevents access to other brokers' data
- **Session Isolation:** Each broker session is completely isolated

## 🚀 **Quick Start Guide**

1. **Setup Database:**
   ```bash
   mysql -u root -p < database_multitenant_setup.sql
   mysql -u root -p < create_broker_accounts.sql
   ```

2. **Start Application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Test Login:**
   - Open browser: `http://localhost:8080`
   - Login with: `admin` / `admin123`

4. **Test API:**
   ```bash
   curl -u admin:admin123 http://localhost:8080/api/users
   ```

## 📞 **Support**

If you need additional broker accounts or have login issues:
1. Check the `create_broker_accounts.sql` script
2. Verify database connection
3. Ensure BCrypt password encoding is working
4. Check application logs for authentication errors

---

**Happy Testing! 🎉**

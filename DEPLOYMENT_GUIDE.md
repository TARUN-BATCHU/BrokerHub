# BrokerHub Deployment Guide

## Complete Free Deployment Solution

### Step 1: Database Setup (Supabase - Free)

1. **Create Supabase Account**
   - Go to [supabase.com](https://supabase.com)
   - Sign up with GitHub/Google
   - Create new project

2. **Setup Database**
   - Project Name: `brokerhub-db`
   - Database Password: (choose strong password)
   - Region: Choose closest to your users

3. **Get Connection Details**
   - Go to Settings > Database
   - Copy connection string (looks like: `postgresql://postgres:[password]@[host]:5432/postgres`)

4. **Import Your Data**
   - Use the SQL Editor in Supabase
   - Run your `database_tables_creation.sql` file
   - Import your existing data

### Step 2: Backend Deployment (Railway - Free)

1. **Create Railway Account**
   - Go to [railway.app](https://railway.app)
   - Sign up with GitHub
   - Connect your repository

2. **Deploy Backend**
   - Click "New Project" > "Deploy from GitHub repo"
   - Select your BrokerHub repository
   - Railway will auto-detect Spring Boot

3. **Set Environment Variables**
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=your_supabase_connection_string
   DATABASE_USERNAME=postgres
   DATABASE_PASSWORD=your_supabase_password
   EMAIL_USERNAME=your_gmail@gmail.com
   EMAIL_PASSWORD=your_gmail_app_password
   BASIC_AUTH_USERNAME=admin
   BASIC_AUTH_PASSWORD=your_secure_password
   ```

4. **Add Redis (Optional)**
   - In Railway dashboard, click "New" > "Database" > "Redis"
   - Copy Redis connection details to environment variables

### Step 3: Frontend Deployment (Vercel - Free)

1. **Prepare React App**
   - Update API base URL to your Railway backend URL
   - Build production version: `npm run build`

2. **Deploy to Vercel**
   - Go to [vercel.com](https://vercel.com)
   - Sign up with GitHub
   - Import your React project
   - Set build command: `npm run build`
   - Set output directory: `build` or `dist`

3. **Environment Variables**
   - Add your backend API URL
   - Any other frontend environment variables

### Step 4: Custom Domain (Optional - Free)

1. **Get Free Domain**
   - Use Freenom, or
   - Use Vercel's free subdomain
   - Use Railway's free subdomain

2. **Configure DNS**
   - Point domain to Vercel for frontend
   - Use subdomain (api.yourdomain.com) for backend

## Final URLs Structure

- **Frontend**: `https://your-app.vercel.app`
- **Backend API**: `https://your-backend.railway.app`
- **Database**: Managed by Supabase

## Client Access

Your client will simply:
1. Visit the Vercel URL
2. Use the application immediately
3. No installation or setup required

## Backup & Security

- **Database**: Supabase provides automatic backups
- **Code**: Stored in GitHub
- **Monitoring**: Railway provides basic monitoring
- **SSL**: Automatic HTTPS on all platforms

## Cost: $0/month

All services used are completely free with generous limits suitable for small to medium applications.

## Support & Maintenance

- Monitor through Railway dashboard
- Check logs for any issues
- Supabase provides database monitoring
- All platforms have 99.9% uptime SLA

## Scaling (When Needed)

If your application grows:
- Railway: $5/month for more resources
- Supabase: $25/month for more database storage
- Vercel: Free tier is usually sufficient for frontend

## Troubleshooting

1. **Backend not starting**: Check Railway logs
2. **Database connection issues**: Verify Supabase connection string
3. **Frontend API errors**: Check CORS configuration
4. **Email not working**: Verify Gmail app password

## Security Checklist

- ✅ HTTPS enabled (automatic)
- ✅ Database password protected
- ✅ Environment variables secured
- ✅ CORS properly configured
- ✅ Authentication implemented
- ✅ Input validation in place
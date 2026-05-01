# Meal Plan API - Spring Boot Backend

A community meal planning platform backend built with Spring Boot 3.4, PostgreSQL, and Spring Security.

## Project Structure

```
meal-plan-api/
├── src/main/java/com/mealplan/
│   ├── MealPlanApiApplication.java          (Main entry point)
│   ├── config/
│   │   └── SecurityConfig.java              (Spring Security configuration)
│   ├── controller/
│   │   ├── AuthController.java              (Auth endpoints)
│   │   ├── MealPlanController.java          (CRUD for meal plans)
│   │   ├── CreatorController.java           (Creator analytics)
│   │   └── AdminController.java             (Admin endpoints)
│   ├── service/
│   │   ├── UserService.java                 (User management)
│   │   ├── MealPlanService.java             (Plan CRUD)
│   │   ├── MealPlanJoinService.java         (Join logic)
│   │   └── AnalyticsService.java            (Analytics)
│   ├── entity/
│   │   ├── User.java
│   │   ├── UserRole.java
│   │   ├── MealPlan.java
│   │   ├── MealPlanJoin.java
│   │   ├── PlanView.java
│   │   ├── PlanRating.java
│   │   └── Video.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── MealPlanRepository.java
│   │   ├── MealPlanJoinRepository.java
│   │   ├── PlanViewRepository.java
│   │   ├── PlanRatingRepository.java
│   │   └── VideoRepository.java
│   └── dto/
│       ├── RegisterRequest.java
│       ├── UserDto.java
│       ├── CreateMealPlanRequest.java
│       ├── MealPlanDto.java
│       └── CreatorAnalyticsDto.java
├── src/main/resources/
│   └── application.properties                (Configuration)
├── build.gradle                              (Gradle build config)
└── README.md
```

## Prerequisites

- Java 21+
- PostgreSQL 14+
- Gradle 8+

## Setup

### 1. Install PostgreSQL

```bash
# macOS
brew install postgresql@15

# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# Start PostgreSQL service
brew services start postgresql@15  # macOS
sudo systemctl start postgresql     # Linux
```

### 2. Create Database

```bash
createdb mealplan
createuser -P postgres  # Create user 'postgres' with password 'postgres'
```

Or via `psql`:
```sql
CREATE DATABASE mealplan;
CREATE USER mealplan_user WITH PASSWORD 'mealplan_password';
ALTER ROLE mealplan_user SET client_encoding TO 'utf8';
ALTER ROLE mealplan_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE mealplan_user SET default_transaction_deferrable TO on;
ALTER ROLE mealplan_user SET timezone TO 'UTC';
GRANT ALL PRIVILEGES ON DATABASE mealplan TO mealplan_user;
```

### 3. Update Database Config

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mealplan
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 4. Build & Run

```bash
cd meal-plan-api

# Build
./gradlew build

# Run
./gradlew bootRun
```

The API will be available at `http://localhost:8080/api`

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `GET /auth/me` - Get current user

### Meal Plans (Public)
- `GET /meal-plans` - List all plans (with pagination & filters)
- `GET /meal-plans?cuisine=Italian&diet=vegan` - Filter by cuisine/diet
- `GET /meal-plans/{id}` - Get plan details (logs a view)
- `POST /meal-plans/{id}/join?userId=xxx` - Join a plan
- `DELETE /meal-plans/{id}/leave?userId=xxx` - Leave a plan

### Creator Endpoints
- `POST /meal-plans?creatorId=xxx` - Create new plan
- `PUT /meal-plans/{id}?creatorId=xxx` - Edit own plan
- `DELETE /meal-plans/{id}?creatorId=xxx` - Delete own plan
- `GET /creators/{creatorId}/plans` - My created plans
- `GET /creators/{creatorId}/plans/{planId}/analytics` - Plan analytics

### Admin Endpoints
- `GET /admin/users` - All users
- `GET /admin/users/creators` - All creators
- `GET /admin/users/browsers` - All consumers
- `GET /admin/analytics` - System-wide analytics

## Testing with Curl

### Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "username": "alice",
    "password": "password123"
  }'
```

### Create a Meal Plan
```bash
curl -X POST "http://localhost:8080/api/meal-plans?creatorId=<UUID>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "High Protein Week",
    "description": "Protein-focused meal plan",
    "cuisine": "Italian",
    "diet": "high-protein",
    "mondayRecipe": "Grilled chicken with pasta",
    "tuesdayRecipe": "Salmon with vegetables",
    "wednesdayRecipe": "Lean beef steak",
    "thursdayRecipe": "Turkey meatballs",
    "fridayRecipe": "Fish tacos"
  }'
```

### List All Plans
```bash
curl http://localhost:8080/api/meal-plans
```

### Get Plan Details
```bash
curl http://localhost:8080/api/meal-plans/<PLAN_UUID>
```

### Join a Plan
```bash
curl -X POST "http://localhost:8080/api/meal-plans/<PLAN_UUID>/join?userId=<USER_UUID>"
```

### Get Creator Analytics
```bash
curl "http://localhost:8080/api/creators/<CREATOR_UUID>/plans/<PLAN_UUID>/analytics"
```

## Database Schema

Hibernate will auto-create tables on first run (with `ddl-auto=update`).

**Tables:**
- `users` - User accounts
- `meal_plans` - Meal plans created by users
- `meal_plan_joins` - Users joining plans
- `plan_views` - Views/impressions on plans
- `plan_ratings` - User ratings on plans
- `videos` - Video walkthroughs

## Key Features

✅ User registration with bcrypt password encoding  
✅ Role-based access (CONSUMER, CREATOR, ADMIN)  
✅ CRUD operations for meal plans  
✅ Join/follow functionality  
✅ Filter by cuisine and diet type  
✅ Plan view tracking for analytics  
✅ Rating system for plans  
✅ Creator dashboard with analytics  
✅ Admin overview endpoint  
✅ CORS enabled for frontend integration  
✅ Pagination for list endpoints  

## Next Steps

1. **Frontend Development** - Build React apps (Consumer, Creator, Admin)
2. **Authentication** - Integrate with Spring Security Principal for user context
3. **Advanced Analytics** - Add more detailed metrics (demographic data, trending)
4. **Video Integration** - Partner with creators for video uploads
5. **Notifications** - Email/push when users join a creator's plan
6. **Search** - Full-text search on plan titles and recipes

## Troubleshooting

### PostgreSQL Connection Issues
```bash
# Check if PostgreSQL is running
brew services list  # macOS
sudo systemctl status postgresql  # Linux

# Reset password
psql -U postgres
ALTER USER postgres WITH PASSWORD 'postgres';
```

### Gradle Build Issues
```bash
# Clean build
./gradlew clean build

# Check Java version
java -version  # Ensure Java 21+
```

## License

MIT

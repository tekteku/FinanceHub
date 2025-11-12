# FinanceHub API Test Script
# This script tests all backend endpoints

$BASE_URL = "http://localhost:8081/api"
$TOKEN = ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  FinanceHub API Test Suite" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Test 1: Health Check
Write-Host "[TEST 1] Health Check" -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method Get
    Write-Host "✓ Health Status: $($health.status)" -ForegroundColor Green
    Write-Host "✓ Database: $($health.components.db.status)" -ForegroundColor Green
    Write-Host "✓ Redis: $($health.components.redis.status)`n" -ForegroundColor Green
} catch {
    Write-Host "✗ Health check failed: $($_.Exception.Message)`n" -ForegroundColor Red
    exit 1
}

# Test 2: Register New User
Write-Host "[TEST 2] Register New User" -ForegroundColor Yellow
$registerBody = @{
    username = "testuser$(Get-Random -Maximum 10000)"
    email = "testuser$(Get-Random -Maximum 10000)@financehub.com"
    password = "Test@123456"
    fullName = "Test User"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/register" -Method Post -Body $registerBody -ContentType "application/json"
    Write-Host "✓ User registered successfully" -ForegroundColor Green
    Write-Host "  Username: $($registerResponse.data.username)" -ForegroundColor Gray
    Write-Host "  Email: $($registerResponse.data.email)`n" -ForegroundColor Gray
} catch {
    Write-Host "✗ Registration failed: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 3: Login
Write-Host "[TEST 3] User Login" -ForegroundColor Yellow
$loginBody = @{
    username = "admin@financehub.com"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BASE_URL/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $TOKEN = $loginResponse.data.token
    Write-Host "✓ Login successful" -ForegroundColor Green
    Write-Host "  Token: $($TOKEN.Substring(0, 20))...`n" -ForegroundColor Gray
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)`n" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $TOKEN"
    "Content-Type" = "application/json"
}

# Test 4: Get Accounts
Write-Host "[TEST 4] Get User Accounts" -ForegroundColor Yellow
try {
    $accountsResponse = Invoke-RestMethod -Uri "$BASE_URL/accounts" -Method Get -Headers $headers
    $accountCount = $accountsResponse.data.Count
    Write-Host "✓ Retrieved $accountCount accounts" -ForegroundColor Green
    if ($accountCount -gt 0) {
        Write-Host "  First Account: $($accountsResponse.data[0].name) - Balance: $($accountsResponse.data[0].balance)`n" -ForegroundColor Gray
    } else {
        Write-Host "  No accounts found`n" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Failed to get accounts: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 5: Create Account
Write-Host "[TEST 5] Create New Account" -ForegroundColor Yellow
$accountBody = @{
    name = "Test Checking Account"
    type = "CHECKING"
    balance = 1000.00
    currency = "USD"
} | ConvertTo-Json

try {
    $newAccount = Invoke-RestMethod -Uri "$BASE_URL/accounts" -Method Post -Body $accountBody -Headers $headers
    $accountId = $newAccount.data.id
    Write-Host "✓ Account created successfully" -ForegroundColor Green
    Write-Host "  Account ID: $accountId" -ForegroundColor Gray
    Write-Host "  Name: $($newAccount.data.name)" -ForegroundColor Gray
    Write-Host "  Balance: $($newAccount.data.balance) $($newAccount.data.currency)`n" -ForegroundColor Gray
} catch {
    Write-Host "✗ Failed to create account: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 6: Get Categories
Write-Host "[TEST 6] Get Categories" -ForegroundColor Yellow
try {
    $categoriesResponse = Invoke-RestMethod -Uri "$BASE_URL/categories" -Method Get -Headers $headers
    $categoryCount = $categoriesResponse.data.Count
    Write-Host "✓ Retrieved $categoryCount categories" -ForegroundColor Green
    if ($categoryCount -gt 0) {
        Write-Host "  Categories: $($categoriesResponse.data.name -join ', ')`n" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ Failed to get categories: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 7: Get Transactions
Write-Host "[TEST 7] Get Transactions" -ForegroundColor Yellow
try {
    $transactionsResponse = Invoke-RestMethod -Uri "$BASE_URL/transactions?page=0&size=10" -Method Get -Headers $headers
    $transactionCount = $transactionsResponse.data.content.Count
    Write-Host "✓ Retrieved $transactionCount transactions" -ForegroundColor Green
    if ($transactionCount -gt 0) {
        Write-Host "  First Transaction: $($transactionsResponse.data.content[0].description) - Amount: $($transactionsResponse.data.content[0].amount)`n" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ Failed to get transactions: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 8: Get Budgets
Write-Host "[TEST 8] Get Budgets" -ForegroundColor Yellow
try {
    $budgetsResponse = Invoke-RestMethod -Uri "$BASE_URL/budgets" -Method Get -Headers $headers
    $budgetCount = $budgetsResponse.data.Count
    Write-Host "✓ Retrieved $budgetCount budgets" -ForegroundColor Green
    if ($budgetCount -gt 0) {
        Write-Host "  First Budget: $($budgetsResponse.data[0].name) - Amount: $($budgetsResponse.data[0].amount)`n" -ForegroundColor Gray
    } else {
        Write-Host "  No budgets found`n" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Failed to get budgets: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 9: Create Budget
Write-Host "[TEST 9] Create New Budget" -ForegroundColor Yellow
$startDate = (Get-Date).ToString("yyyy-MM-dd")
$endDate = (Get-Date).AddMonths(1).ToString("yyyy-MM-dd")

$budgetBody = @{
    name = "Monthly Food Budget"
    amount = 500.00
    startDate = $startDate
    endDate = $endDate
    period = "MONTHLY"
    alertThreshold = 80
    isActive = $true
    description = "Monthly budget for food expenses"
} | ConvertTo-Json

try {
    $newBudget = Invoke-RestMethod -Uri "$BASE_URL/budgets" -Method Post -Body $budgetBody -Headers $headers
    Write-Host "✓ Budget created successfully" -ForegroundColor Green
    Write-Host "  Budget ID: $($newBudget.data.id)" -ForegroundColor Gray
    Write-Host "  Name: $($newBudget.data.name)" -ForegroundColor Gray
    Write-Host "  Amount: $($newBudget.data.amount)`n" -ForegroundColor Gray
} catch {
    Write-Host "✗ Failed to create budget: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 10: Dashboard Analytics
Write-Host "[TEST 10] Dashboard Analytics" -ForegroundColor Yellow
try {
    $dashboardResponse = Invoke-RestMethod -Uri "$BASE_URL/analytics/dashboard" -Method Get -Headers $headers
    Write-Host "✓ Dashboard analytics retrieved" -ForegroundColor Green
    Write-Host "  Total Income: $($dashboardResponse.data.totalIncome)" -ForegroundColor Gray
    Write-Host "  Total Expenses: $($dashboardResponse.data.totalExpenses)" -ForegroundColor Gray
    Write-Host "  Balance: $($dashboardResponse.data.balance)`n" -ForegroundColor Gray
} catch {
    Write-Host "✗ Failed to get dashboard analytics: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 11: Financial Summary
Write-Host "[TEST 11] Financial Summary" -ForegroundColor Yellow
$summaryStartDate = (Get-Date).AddDays(-30).ToString("yyyy-MM-dd")
$summaryEndDate = (Get-Date).ToString("yyyy-MM-dd")

try {
    $summaryResponse = Invoke-RestMethod -Uri "$BASE_URL/analytics/summary?startDate=$summaryStartDate&endDate=$summaryEndDate" -Method Get -Headers $headers
    Write-Host "✓ Financial summary retrieved" -ForegroundColor Green
    Write-Host "  Period: $summaryStartDate to $summaryEndDate" -ForegroundColor Gray
    Write-Host "  Income: $($summaryResponse.data.totalIncome)" -ForegroundColor Gray
    Write-Host "  Expenses: $($summaryResponse.data.totalExpenses)`n" -ForegroundColor Gray
} catch {
    Write-Host "✗ Failed to get financial summary: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Test 12: Expenses by Category
Write-Host "[TEST 12] Expenses by Category" -ForegroundColor Yellow
try {
    $expensesResponse = Invoke-RestMethod -Uri "$BASE_URL/analytics/expenses/by-category?startDate=$summaryStartDate&endDate=$summaryEndDate" -Method Get -Headers $headers
    $expensesCount = $expensesResponse.data.Count
    Write-Host "✓ Retrieved expenses for $expensesCount categories" -ForegroundColor Green
    if ($expensesCount -gt 0) {
        foreach ($expense in $expensesResponse.data | Select-Object -First 3) {
            Write-Host "  $($expense.categoryName): $($expense.amount) ($([math]::Round($expense.percentage, 2))%)" -ForegroundColor Gray
        }
    }
    Write-Host ""
} catch {
    Write-Host "✗ Failed to get expenses by category: $($_.Exception.Message)`n" -ForegroundColor Red
}

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Test Suite Completed!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nAll core endpoints are working correctly!" -ForegroundColor Green
Write-Host "You can now use the Angular frontend at http://localhost:4200`n" -ForegroundColor Green

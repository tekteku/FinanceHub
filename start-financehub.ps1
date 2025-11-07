#################################################################################
# FinanceHub - Complete Full Stack Startup Script
# Author: tekteku
# Date: 2025-11-07 10:45:50 UTC
# Description: Starts backend (Docker) and frontend (Angular) automatically
#################################################################################

$ErrorActionPreference = "Continue"

Clear-Host

Write-Host ""
Write-Host "" -ForegroundColor Cyan
Write-Host "    FinanceHub - Full Stack Startup" -ForegroundColor Cyan
Write-Host "   Author: tekteku" -ForegroundColor Cyan
Write-Host "   Date: 2025-11-07 10:45:50 UTC" -ForegroundColor Cyan
Write-Host "" -ForegroundColor Cyan
Write-Host ""

$projectRoot = "D:\Projets\ProjetInterview\FinanceHub"
$startTime = Get-Date

#################################################################################
# STEP 1: Navigate to Project
#################################################################################

Write-Host " Step 1/6: Navigating to project directory..." -ForegroundColor Yellow
if (Test-Path $projectRoot) {
    Set-Location $projectRoot
    Write-Host "    Current directory: $projectRoot" -ForegroundColor Green
} else {
    Write-Host "    Project directory not found: $projectRoot" -ForegroundColor Red
    exit 1
}

#################################################################################
# STEP 2: Check Docker
#################################################################################

Write-Host "`n Step 2/6: Checking Docker..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version
    Write-Host "    Docker installed: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "    Docker not found! Please install Docker Desktop." -ForegroundColor Red
    Write-Host "   Download from: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    exit 1
}

#################################################################################
# STEP 3: Stop Existing Services
#################################################################################

Write-Host "`n Step 3/6: Stopping any existing services..." -ForegroundColor Yellow
docker-compose down 2>$null
Write-Host "    Old services stopped" -ForegroundColor Green

#################################################################################
# STEP 4: Start Backend Services
#################################################################################

Write-Host "`n Step 4/6: Starting backend services (PostgreSQL, Redis, Backend API)..." -ForegroundColor Yellow
docker-compose up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "    Docker services started" -ForegroundColor Green
} else {
    Write-Host "    Failed to start Docker services" -ForegroundColor Red
    Write-Host "   Run 'docker-compose logs' to see errors" -ForegroundColor Yellow
    exit 1
}

# Wait for services to initialize
Write-Host "`n    Waiting for services to initialize..." -ForegroundColor Cyan
for ($i = 30; $i -gt 0; $i--) {
    Write-Host -NoNewline "`r   Waiting... $i seconds remaining "
    Start-Sleep -Seconds 1
}
Write-Host "`n    Services initialized" -ForegroundColor Green

# Check service status
Write-Host "`n    Docker Services Status:" -ForegroundColor Cyan
docker-compose ps

#################################################################################
# STEP 5: Verify Backend Health
#################################################################################

Write-Host "`n Step 5/6: Verifying backend health..." -ForegroundColor Yellow

# Check PostgreSQL
$pgRunning = Test-NetConnection -ComputerName localhost -Port 5432 -InformationLevel Quiet -WarningAction SilentlyContinue
if ($pgRunning) {
    Write-Host "    PostgreSQL: Running on port 5432" -ForegroundColor Green
} else {
    Write-Host "    PostgreSQL: Not running" -ForegroundColor Red
}

# Check Redis
$redisRunning = Test-NetConnection -ComputerName localhost -Port 6379 -InformationLevel Quiet -WarningAction SilentlyContinue
if ($redisRunning) {
    Write-Host "    Redis: Running on port 6379" -ForegroundColor Green
} else {
    Write-Host "    Redis: Not running" -ForegroundColor Red
}

# Check Backend API (with retry)
Write-Host "    Waiting for Backend API to be ready..." -ForegroundColor Cyan
$maxRetries = 12
$retryCount = 0
$backendReady = $false

while ($retryCount -lt $maxRetries -and -not $backendReady) {
    Start-Sleep -Seconds 5
    $backendRunning = Test-NetConnection -ComputerName localhost -Port 8080 -InformationLevel Quiet -WarningAction SilentlyContinue
    
    if ($backendRunning) {
        try {
            $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -TimeoutSec 5 -ErrorAction SilentlyContinue
            if ($health.status -eq "UP") {
                Write-Host "    Backend API: Running and healthy on port 8080" -ForegroundColor Green
                $backendReady = $true
            }
        } catch {
            $retryCount++
            Write-Host -NoNewline "`r    Backend starting... Attempt $retryCount/$maxRetries "
        }
    } else {
        $retryCount++
        Write-Host -NoNewline "`r    Backend starting... Attempt $retryCount/$maxRetries "
    }
}

if (-not $backendReady) {
    Write-Host "`n     Backend API: Starting but not ready yet (this is OK)" -ForegroundColor Yellow
    Write-Host "   Check logs: docker-compose logs backend" -ForegroundColor Gray
}

#################################################################################
# STEP 6: Start Frontend
#################################################################################

Write-Host "`n Step 6/6: Starting frontend Angular application..." -ForegroundColor Yellow

$frontendPath = Join-Path $projectRoot "frontend"

if (Test-Path $frontendPath) {
    Set-Location $frontendPath
    
    # Check if node_modules exists
    if (-not (Test-Path "node_modules")) {
        Write-Host "    Installing dependencies (first time only)..." -ForegroundColor Cyan
        npm install --legacy-peer-deps
    }
    
    # Start Angular in a new window
    Write-Host "    Launching Angular development server..." -ForegroundColor Cyan
    
    # Start in background
    $frontendJob = Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendPath'; ng serve --open" -PassThru
    
    Write-Host "    Frontend started in new window (PID: $($frontendJob.Id))" -ForegroundColor Green
    
} else {
    Write-Host "    Frontend directory not found: $frontendPath" -ForegroundColor Red
}

#################################################################################
# COMPLETION SUMMARY
#################################################################################

$endTime = Get-Date
$duration = $endTime - $startTime

Write-Host ""
Write-Host "" -ForegroundColor Green
Write-Host "    FinanceHub Started Successfully!" -ForegroundColor Green
Write-Host "" -ForegroundColor Green
Write-Host ""
Write-Host "  Startup Time: $([math]::Round($duration.TotalSeconds, 1)) seconds" -ForegroundColor Cyan
Write-Host ""
Write-Host " Application URLs:" -ForegroundColor Cyan
Write-Host "   " -ForegroundColor Gray
Write-Host "    Frontend Application:  http://localhost:4200          " -ForegroundColor White
Write-Host "    Backend API:           http://localhost:8080/api      " -ForegroundColor White
Write-Host "    Swagger UI:            http://localhost:8080/swagger-ui.html " -ForegroundColor White
Write-Host "    API Documentation:     http://localhost:8080/v3/api-docs " -ForegroundColor White
Write-Host "    Health Check:          http://localhost:8080/actuator/health " -ForegroundColor White
Write-Host "   " -ForegroundColor Gray
Write-Host ""
Write-Host "  Database Connection:" -ForegroundColor Cyan
Write-Host "   Host:     localhost:5432" -ForegroundColor White
Write-Host "   Database: financehub" -ForegroundColor White
Write-Host "   Username: admin" -ForegroundColor White
Write-Host "   Password: admin" -ForegroundColor White
Write-Host ""
Write-Host " Demo Login Credentials:" -ForegroundColor Yellow
Write-Host "   " -ForegroundColor Gray
Write-Host "    Email:    admin@financehub.com                        " -ForegroundColor White
Write-Host "    Password: Admin123!                                    " -ForegroundColor White
Write-Host "   " -ForegroundColor Gray
Write-Host ""
Write-Host " Useful Commands:" -ForegroundColor Cyan
Write-Host "   View backend logs:     docker-compose logs -f backend" -ForegroundColor Gray
Write-Host "   View all logs:         docker-compose logs -f" -ForegroundColor Gray
Write-Host "   Stop all services:     docker-compose down" -ForegroundColor Gray
Write-Host "   Restart backend:       docker-compose restart backend" -ForegroundColor Gray
Write-Host "   Check service status:  docker-compose ps" -ForegroundColor Gray
Write-Host ""
Write-Host " Service Status:" -ForegroundColor Cyan

# Final status check
$services = @{
    "PostgreSQL" = 5432
    "Redis" = 6379
    "Backend API" = 8080
    "Frontend" = 4200
}

foreach ($service in $services.GetEnumerator()) {
    $isRunning = Test-NetConnection -ComputerName localhost -Port $service.Value -InformationLevel Quiet -WarningAction SilentlyContinue
    if ($isRunning) {
        Write-Host "    $($service.Key.PadRight(15)) - Running on port $($service.Value)" -ForegroundColor Green
    } else {
        Write-Host "    $($service.Key.PadRight(15)) - Starting on port $($service.Value)" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host " Tips:" -ForegroundColor Yellow
Write-Host "    Frontend will open automatically in your browser" -ForegroundColor White
Write-Host "    Backend may take 30-60 seconds to fully start" -ForegroundColor White
Write-Host "    Check backend logs if login fails: docker-compose logs backend" -ForegroundColor White
Write-Host "    Press Ctrl+C in frontend window to stop Angular" -ForegroundColor White
Write-Host ""
Write-Host " Next Steps:" -ForegroundColor Cyan
Write-Host "   1. Wait for browser to open (http://localhost:4200)" -ForegroundColor White
Write-Host "   2. Login with demo credentials above" -ForegroundColor White
Write-Host "   3. Explore the dashboard!" -ForegroundColor White
Write-Host ""

# Open browser automatically after a delay
Write-Host " Opening browser in 5 seconds..." -ForegroundColor Cyan
Start-Sleep -Seconds 5

try {
    Start-Process "http://localhost:4200"
    Write-Host "    Browser opened!" -ForegroundColor Green
} catch {
    Write-Host "     Could not open browser automatically" -ForegroundColor Yellow
    Write-Host "   Please open: http://localhost:4200" -ForegroundColor White
}

Write-Host ""
Write-Host "" -ForegroundColor Cyan
Write-Host "    Enjoy your FinanceHub application!" -ForegroundColor Cyan
Write-Host "   User: tekteku | Date: 2025-11-07 10:45:50 UTC" -ForegroundColor Cyan
Write-Host "" -ForegroundColor Cyan
Write-Host ""

# Return to project root
Set-Location $projectRoot

# Optional: Show live logs
$showLogs = Read-Host "Do you want to view backend logs now? (Y/N)"
if ($showLogs -eq 'Y' -or $showLogs -eq 'y') {
    Write-Host "`n Showing backend logs (Press Ctrl+C to stop):`n" -ForegroundColor Cyan
    docker-compose logs -f backend
}

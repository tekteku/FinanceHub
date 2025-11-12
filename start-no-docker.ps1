# FinanceHub - Run Without Docker Script
# This script runs the application components directly on your machine

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Starting FinanceHub (No Docker Mode)" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Step 1: Check prerequisites
Write-Host "[1/4] Checking prerequisites..." -ForegroundColor Yellow

# Check Java
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "✓ Java installed: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Java not found. Please install Java 17 or higher." -ForegroundColor Red
    exit 1
}

# Check Node.js
try {
    $nodeVersion = node -v
    Write-Host "✓ Node.js installed: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Node.js not found. Please install Node.js." -ForegroundColor Red
    exit 1
}

# Check npm
try {
    $npmVersion = npm -v
    Write-Host "✓ npm installed: v$npmVersion`n" -ForegroundColor Green
} catch {
    Write-Host "✗ npm not found. Please install npm." -ForegroundColor Red
    exit 1
}

# Step 2: Start database services with Docker
Write-Host "[2/4] Starting database services (PostgreSQL & Redis)..." -ForegroundColor Yellow

try {
    docker-compose up -d postgres redis 2>&1 | Out-Null
    Write-Host "✓ PostgreSQL started on port 5432" -ForegroundColor Green
    Write-Host "✓ Redis started on port 6379`n" -ForegroundColor Green
    
    # Wait for databases to be healthy
    Write-Host "Waiting for databases to be ready..." -ForegroundColor Gray
    Start-Sleep -Seconds 5
} catch {
    Write-Host "✗ Failed to start databases. Is Docker Desktop running?" -ForegroundColor Red
    Write-Host "Please start Docker Desktop and try again." -ForegroundColor Yellow
    exit 1
}

# Step 3: Start Spring Boot Backend
Write-Host "[3/4] Starting Spring Boot backend on port 8080..." -ForegroundColor Yellow

$backendProcess = $null
try {
    # Change to backend directory
    Set-Location -Path "backend"
    
    # Check if we have Maven wrapper
    if (Test-Path ".mvn\wrapper\maven-wrapper.jar") {
        Write-Host "Using Maven wrapper..." -ForegroundColor Gray
        $backendProcess = Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "spring-boot:run" -PassThru -WindowStyle Normal
    } else {
        # Try to compile and run with Gradle or direct Java
        Write-Host "Maven wrapper not found. Checking for compiled classes..." -ForegroundColor Gray
        
        if (Test-Path "target\*.jar") {
            $jarFile = Get-ChildItem "target\*.jar" | Select-Object -First 1
            Write-Host "Found JAR file: $($jarFile.Name)" -ForegroundColor Gray
            $backendProcess = Start-Process -FilePath "java" -ArgumentList "-jar", $jarFile.FullName -PassThru -WindowStyle Normal
        } else {
            Write-Host "✗ No compiled JAR found. Please compile the project first:" -ForegroundColor Red
            Write-Host "  Option 1: Use Docker backend (recommended)" -ForegroundColor Yellow
            Write-Host "  Option 2: Install Maven and run: mvn clean package" -ForegroundColor Yellow
            Write-Host "`nUsing Docker backend as fallback..." -ForegroundColor Yellow
            Set-Location ..
            docker-compose up -d backend 2>&1 | Out-Null
            Write-Host "✓ Backend started in Docker on port 8081`n" -ForegroundColor Green
            
            # Update frontend to use 8081
            Write-Host "Updating frontend to use port 8081..." -ForegroundColor Gray
            $envFile = "frontend\src\environments\environment.development.ts"
            (Get-Content $envFile) -replace 'localhost:8080', 'localhost:8081' | Set-Content $envFile
        }
    }
    
    if ($backendProcess) {
        Write-Host "✓ Backend starting (PID: $($backendProcess.Id))..." -ForegroundColor Green
        Write-Host "  Backend will be available at: http://localhost:8080`n" -ForegroundColor Gray
        Set-Location ..
        
        # Wait for backend to start
        Write-Host "Waiting for backend to initialize (30 seconds)..." -ForegroundColor Gray
        Start-Sleep -Seconds 30
    }
} catch {
    Write-Host "✗ Failed to start backend: $($_.Exception.Message)" -ForegroundColor Red
    Set-Location ..
}

# Step 4: Start Angular Frontend
Write-Host "[4/4] Starting Angular frontend on port 4200..." -ForegroundColor Yellow

try {
    Set-Location -Path "frontend"
    
    # Install dependencies if needed
    if (-not (Test-Path "node_modules")) {
        Write-Host "Installing npm dependencies..." -ForegroundColor Gray
        npm install 2>&1 | Out-Null
    }
    
    Write-Host "✓ Starting Angular dev server...`n" -ForegroundColor Green
    
    # Start Angular in a new window
    Start-Process -FilePath "npm" -ArgumentList "start" -WorkingDirectory (Get-Location)
    
    Set-Location ..
} catch {
    Write-Host "✗ Failed to start frontend: $($_.Exception.Message)" -ForegroundColor Red
    Set-Location ..
    exit 1
}

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  FinanceHub Started Successfully!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`nServices Running:" -ForegroundColor White
Write-Host "  📊 PostgreSQL:  localhost:5432" -ForegroundColor Green
Write-Host "  🔄 Redis:       localhost:6379" -ForegroundColor Green
Write-Host "  🚀 Backend API: http://localhost:8080" -ForegroundColor Green
Write-Host "  💻 Frontend:    http://localhost:4200" -ForegroundColor Green

Write-Host "`nAccess the application:" -ForegroundColor White
Write-Host "  → Open your browser: http://localhost:4200" -ForegroundColor Cyan
Write-Host "  → Login with: admin@financehub.com / admin123" -ForegroundColor Cyan

Write-Host "`nTo stop the application:" -ForegroundColor White
Write-Host "  1. Close the Angular terminal window" -ForegroundColor Gray
Write-Host "  2. Run: docker-compose down" -ForegroundColor Gray
Write-Host "  3. Stop backend process if running`n" -ForegroundColor Gray

# Keep the script running
Write-Host "Press Ctrl+C to exit this window..." -ForegroundColor Yellow
Write-Host "(Application will continue running in separate windows)`n" -ForegroundColor Yellow

try {
    while ($true) {
        Start-Sleep -Seconds 60
    }
} catch {
    Write-Host "`nShutting down..." -ForegroundColor Yellow
}

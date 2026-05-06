#!/usr/bin/env pwsh
# Run script for Room Booking Management System (PowerShell)

$javac = "C:\Program Files\Java\jdk-26.0.1\bin\javac"
$java = "C:\Program Files\Java\jdk-26.0.1\bin\java"

Write-Host "Compiling Main.java..." -ForegroundColor Cyan
& $javac Main.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Compilation successful!" -ForegroundColor Green
Write-Host "Starting application..." -ForegroundColor Cyan
& $java -cp . Main

Read-Host "Press Enter to exit"

@echo off
REM Run script for Room Booking Management System
REM This script compiles and runs the Java application

echo Compiling Main.java...
"C:\Program Files\Java\jdk-26.0.1\bin\javac" Main.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo Starting application...
"C:\Program Files\Java\jdk-26.0.1\bin\java" -cp . Main

pause

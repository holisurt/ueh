@echo off
setlocal

REM Run script for Room Booking Management System
REM This script compiles and runs the Java application using JAVA_HOME or PATH.

if defined JAVA_HOME (
    set "JAVAC=%JAVA_HOME%\bin\javac"
    set "JAVA=%JAVA_HOME%\bin\java"
) else (
    set "JAVAC=javac"
    set "JAVA=java"
)

REM Run from the script directory so relative paths work.
cd /d "%~dp0"

echo Checking Java compiler...
"%JAVAC%" --version >nul 2>&1
if errorlevel 1 (
    echo Java compiler not found. Please install a JDK and ensure javac is on PATH or JAVA_HOME is set.
    pause
    exit /b 1
)

echo Compiling Main.java...
"%JAVAC%" -encoding UTF-8 "Main.java"

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo Starting application...
"%JAVA%" -Dfile.encoding=UTF-8 -cp . Main

pause
endlocal

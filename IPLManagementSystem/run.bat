@echo off
echo ============================================================
echo   Launching IPL Cricket League Management System
echo ============================================================

REM Check if compiled classes exist
if not exist "out\Main.class" (
    echo Out folder not compiled yet. Running build.bat first...
    call build.bat
)

REM Run application with classpath including out and lib (for MySQL connector)
java -cp "out;lib/*" Main

pause

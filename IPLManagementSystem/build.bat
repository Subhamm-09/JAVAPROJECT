@echo off
echo ============================================================
echo   Building IPL Cricket League Management System
echo ============================================================

REM Create bin/out directory if it doesn't exist
if not exist "out" mkdir out

REM Compile all java files
echo Compiling source files...
javac -encoding UTF-8 -d out -cp "lib/*;src" src/model/*.java src/dao/*.java src/connection/*.java src/threads/*.java src/filehandling/*.java src/gui/*.java src/Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================================
    echo [SUCCESS] Compilation finished without errors!
    echo To run the application, execute: run.bat
    echo ============================================================
) else (
    echo.
    echo ============================================================
    echo [ERROR] Compilation failed. Please check errors above.
    echo ============================================================
)
pause

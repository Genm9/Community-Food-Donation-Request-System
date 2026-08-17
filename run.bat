@echo off
setlocal

cd /d "%~dp0"

set "JAVAFX_LIB=C:\Program Files\Java\javafx-sdk-21.0.11\lib"

if not exist "%JAVAFX_LIB%\javafx.controls.jar" (
    echo JavaFX library was not found:
    echo %JAVAFX_LIB%
    echo.
    echo Please check the JavaFX SDK path in run.bat.
    pause
    exit /b 1
)

if not exist "src\*.java" (
    echo No Java source files were found in the src folder.
    pause
    exit /b 1
)

if exist "out" rmdir /s /q "out"
mkdir "out"

echo Compiling JavaFX source files...
javac --module-path "%JAVAFX_LIB%" --add-modules javafx.controls -d out src\*.java
if errorlevel 1 (
    echo.
    echo Compilation failed.
    pause
    exit /b 1
)

echo Starting Community Food Donation and Request System...
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls -cp out FoodDonationApp

if errorlevel 1 (
    echo.
    echo The application stopped with an error.
    pause
)

endlocal

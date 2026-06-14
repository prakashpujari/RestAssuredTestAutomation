@echo off
setlocal

echo Building the project...
mvn clean -q verify
if errorlevel 1 exit /b 1
echo Build succeeded.

echo Starting Spring Boot app on :8080...
start "" mvn spring-boot:run
timeout /t 3

echo Checking server...
curl -s -o NUL -w "%%{http_code}" http://localhost:8080/api/tests | findstr /C:"200"
if errorlevel 1 (
    echo Server failed to start.
    taskkill /F /IM java.exe
    exit /b 1
)
echo Server is up!

echo Running all tests via /api/run endpoint...
for /f "delims=" %%A in ('curl -s -X POST http://localhost:8080/api/run') do set RESULT=%%A
echo %RESULT%

echo Done. Press any key to exit.
pause >nul
endlocal

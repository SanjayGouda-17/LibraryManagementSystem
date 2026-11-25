@echo off
echo ============================================
echo   Building and Deploying LMS to Tomcat 10
echo ============================================

cd /d "c:\Users\iamsa\Desktop\LMS"
call "C:\Users\iamsa\Downloads\apache-maven-3.9.9-bin\apache-maven-3.9.9\bin\mvn.cmd" clean package

if %ERRORLEVEL% equ 0 (
    echo.
    echo Copying LMS.war to Tomcat webapps...
    copy /Y "target\LMS.war" "C:\apache-tomcat-10.1.57\apache-tomcat-10.1.57\webapps\LMS.war"
    echo.
    echo ============================================
    echo   SUCCESS! Application Updated & Deployed!
    echo   Open: http://localhost:8050/LMS/
    echo ============================================
) else (
    echo.
    echo [ERROR] Build failed. Please check compilation errors above.
)
pause

@ECHO OFF

SET JAVA_HOME=%CD%\Programs\jdk1.6.0_45
SET PATH=%JAVA_HOME%\bin;%PATH%

:LAUNCH_INSTALLER
ECHO Launching uninstaller...  Please wait.
start javaw.exe -splash:"Setup\splash.gif" -jar "Setup\installer.jar" "uninstall" 
if %errorlevel% neq 0 pause

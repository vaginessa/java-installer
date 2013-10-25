@ECHO OFF

SET CWD=%~dp0%
SET JAVA_HOME=%CWD%\Programs\jdk1.6.0_45
SET PATH=%PATH%;%JAVA_HOME%\bin

:LAUNCH_INSTALLER
ECHO Launching uninstaller...  Please wait.
start javaw.exe -splash:"%CWD%\Setup\splash.gif" -Djava.library.path="%CWD%\Setup\lib" -jar "%CWD%\Setup\installer-0.0.1-SNAPSHOT.jar" "uninstall" 
if %errorlevel% neq 0 pause

@ECHO OFF
@setlocal enableextensions
@cd /d "%~dp0"

SET CWD=%~dp0%
SET USER_HOME=%HOMEDRIVE%%HOMEPATH%
SET ARQ_HOME=%USER_HOME%\ARQ
SET JAVA_HOME=%ARQ_HOME%\jdk1.6.0_45
SET PATH=%PATH%;%JAVA_HOME%\bin

ECHO Checking JAVA Installation...
IF NOT EXIST "%JAVA_HOME%" GOTO INSTALL_JDK
GOTO LAUNCH_INSTALLER

:INSTALL_JDK
ECHO Installing JDK...
"%CWD%Setup\7z.exe" x "%CWD%Programs\jdk1.6.0_45.zip" -y -o"%ARQ_HOME%"
if %errorlevel% neq 0 pause

:LAUNCH_INSTALLER
ECHO Launching installer...
start javaw.exe -splash:"%CWD%\Setup\splash.gif" -Djava.library.path="%CWD%\Setup\lib" -jar "%CWD%\Setup\installer.jar"
if %errorlevel% neq 0 pause

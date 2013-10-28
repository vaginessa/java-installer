@ECHO OFF

SET CWD=%~dp0%
SET ARQ_HOME=%HOMEDRIVE%\ARQ-SDK\%USERNAME%
SET JAVA_HOME=%ARQ_HOME%\jdk1.6.0_45
SET PATH=%PATH%;%JAVA_HOME%\bin

ECHO Checking JAVA Installation...
IF EXIST "%JAVA_HOME%" GOTO LAUNCH_INSTALLER

:INSTALL_JDK
ECHO.
ECHO Installing JDK... Please wait.
IF NOT EXIST "%ARQ_HOME%" MKDIR "%ARQ_HOME%"
ECHO.
ECHO Downloading files...
COPY /B /Y "%CWD%Programs\jdk1.6.0_45.zip" "%ARQ_HOME%\jdk1.6.0_45.zip"
ECHO.
ECHO Uncompressing files...
"%CWD%Setup\7z.exe" x "%ARQ_HOME%\jdk1.6.0_45.zip" -y -o"%ARQ_HOME%"
DEL /F "%ARQ_HOME%\jdk1.6.0_45.zip"
if %errorlevel% neq 0 pause

:LAUNCH_INSTALLER
ECHO Launching installer...
start javaw.exe -splash:"%CWD%\Setup\splash.gif" -Djava.library.path="%CWD%\Setup\lib" -jar "%CWD%\Setup\installer-0.0.1-SNAPSHOT.jar" "install"
if %errorlevel% neq 0 pause

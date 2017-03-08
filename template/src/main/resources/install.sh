@ECHO OFF

ECHO Launching installer...
javaw.exe -splash:"Setup/splash.gif" -Djava.library.path="Setup/lib" -jar "Setup/installer.jar" "install"


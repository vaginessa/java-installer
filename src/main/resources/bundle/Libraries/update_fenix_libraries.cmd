@ECHO OFF

:VARIABLES
SET MVN_HOME=..\Programs\apache-maven-3.1.0
SET JAVA_HOME=..\Programs\jdk1.6.0_45
SET PATH=%PATH%;%JAVA_HOME%\bin;%MVN_HOME%\bin

:TITLE
ECHO ********************************************************************************
ECHO *           Programa de actualizacion de librerias Fenix                       *
ECHO ********************************************************************************

:ASK
ECHO.
ECHO WARNING: Esta a punto de actualizar las librerias de Fenix de la maqueta.
ECHO.
SET /p RESP="Desea continuar (si/no)? " %=%
IF %RESP% == no GOTO FIN
IF %RESP% == NO GOTO FIN
IF %RESP% == si GOTO CONFIRM
IF %RESP% == SI GOTO CONFIRM
GOTO ASK

:CONFIRM
ECHO.
ECHO WARNING: Este cambio afectara a todos los usuarios que se actualicen la maqueta.
ECHO.
SET /p RESP="Esta seguro de continuar (si/no)? " %=%
IF %RESP% == no GOTO FIN
IF %RESP% == NO GOTO FIN
IF %RESP% == si GOTO UPDATE
IF %RESP% == SI GOTO UPDATE
GOTO UPDATE

:UPDATE
SET REPO_URL=http://10.232.225.42:8081/nexus/content/repositories/snapshots-tst/
SET ARTIFACT_ID=es.generali.arq.library:user-generali:0.1-SNAPSHOT:pom
ECHO.
call mvn -s "..\Config\m2\settings.xml" -P nexus-tst dependency:get -DrepoUrl=%REPO_URL% -Dartifact=%ARTIFACT_ID% -Ddest=.\generali-libraries.pom
call mvn -s "..\Config\m2\settings.xml" -P nexus-tst -f generali-libraries.pom dependency:copy-dependencies -DexcludeTransitive=true -Dmdep.stripVersion=true -DoutputDirectory=.\Generali

:FIN
ECHO.
if %errorlevel% neq 0 pause
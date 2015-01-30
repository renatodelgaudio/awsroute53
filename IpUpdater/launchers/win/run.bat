@echo off

setlocal

set CURRDIR=%~dp0

set JAVA_TOOL_OPTIONS=
set JAVA_OPTIONS=
set _JAVA_OPTIONS=
set IBM_JAVA_OPTIONS=

set JAVA_OPTS=-DINSTALL_DIR=%CURRDIR%

set JAVACMD=java

set RUNCMD=%JAVACMD% %JAVA_OPTS% -jar IpUpdater.jar

echo running %RUNCMD%
%RUNCMD%

pause
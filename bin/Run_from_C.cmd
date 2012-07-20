@echo off
setlocal

set JAVA_JRE6=C:\Program Files\Java\jre6
set JAVA_JRE7=C:\Program Files\Java\jre7

:: get system architecture
IF EXIST "%programfiles(x86)%" set SYS=x64
IF NOT EXIST "%programfiles(x86)%" set SYS=x32
echo Running using %SYS%

:: determine if java exists for v1.6 or v1.7 (but prefer v1.7)
IF EXIST "%JAVA_JRE6%" set JAVA=%JAVA_JRE6%\bin\java.exe
IF EXIST "%JAVA_JRE7%" set JAVA=%JAVA_JRE7%\bin\java.exe

IF "%JAVA%"=="" (
	ECHO ERROR: missing required Java installation! Please install Java v1.6 or higher.
	PAUSE
	EXIT /B 1
)
set JAVA_CLASS=ApplicationWindow
set SCRIPT_DIR=C:\LaundryMate\
set SRC_DIR=%SCRIPT_DIR%src
set INCLUDE_DIR=%SCRIPT_DIR%include
set JAR=%INCLUDE_DIR%\mail.jar;%INCLUDE_DIR%\%SYS%\RXTXcomm.jar
set CLASS_DIR=%INCLUDE_DIR%\%SYS%\class

set CP=%SRC_DIR%;%JAR%;%CLASS_DIR%
set JAVA_ARGS=-Djava.library.path="%INCLUDE_DIR%\%SYS%" -cp "%CP%"
 
call "%JAVA%" %JAVA_ARGS% %JAVA_CLASS%
exit /B 0

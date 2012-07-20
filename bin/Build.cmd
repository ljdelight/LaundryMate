@echo off

:: change this path for your computer! If you have
::    JDK1.5.0u31, this will work find
set JDK_HOME=C:\Program Files\Java\jdk1.6.0_31


IF EXIST "%programfiles(x86)%" set SYS=x64
IF NOT EXIST "%programfiles(x86)%" set SYS=x32
echo Building for %SYS%

set JAVA_CLASS=ApplicationWindow
set SCRIPT_DIR=%~DP0
set SRC_DIR=%SCRIPT_DIR%src
set INCLUDE_DIR=%SCRIPT_DIR%include
set JAR=%INCLUDE_DIR%\mail.jar;%INCLUDE_DIR%\%SYS%\RXTXcomm.jar
set CLASS_OUT_DIR=%INCLUDE_DIR%\%SYS%\class
set CP=%SRC_DIR%;%JAR%
 
:: if the class dir exists, delete it
IF EXIST "%CLASS_OUT_DIR%" rmdir /s /q "%CLASS_OUT_DIR%"

:: create the dir
mkdir "%CLASS_OUT_DIR%"

:: compile the source
call "%JDK_HOME%\bin\javac" -cp "%CP%" -d "%CLASS_OUT_DIR%"  "%SRC_DIR%\%JAVA_CLASS%.java"
exit /B 0
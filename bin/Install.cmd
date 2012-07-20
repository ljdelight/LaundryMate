@echo off

set SCRIPT_DIR=%~DP0
set INCLUDE_DIR=%SCRIPT_DIR%include
set DEST_DIR=C:\LaundryMate

set JAVA_JRE6=C:\Program Files\Java\jre6
set JAVA_JRE7=C:\Program Files\Java\jre7

:: get system architecture
IF EXIST "%programfiles(x86)%" set SYS=x64
IF NOT EXIST "%programfiles(x86)%" set SYS=x32
echo Installing using %SYS%

:: start the install for the driver
call "%INCLUDE_DIR%\MSP430UARTDriver.exe"

:DETERMINE_JAVA
:: determine if java exists for v1.6 or v1.7 (but prefer v1.7)
IF EXIST "%JAVA_JRE6%" set JAVA=%JAVA_JRE6%\bin\java.exe
IF EXIST "%JAVA_JRE7%" set JAVA=%JAVA_JRE7%\bin\java.exe

:: if no java, run install
IF "%JAVA%"=="" (
	IF "%SYS%"=="x64" (
		ECHO MISSING JAVA: Starting JRE 64-bit installation
		call "%INCLUDE_DIR%\jre-7u3-windows-x64.exe"
	) ELSE (
		ECHO MISSING JAVA: Starting JRE 32-bit installation
		call "%INCLUDE_DIR%\jre-7u3-windows-i586.exe"
	)
	goto DETERMINE_JAVA
)

IF EXIST "%DEST_DIR%" rmdir /s /q "%DEST_DIR%"
mkdir "%DEST_DIR%"
IF %ERRORLEVEL% NEQ 0 (
	ECHO ERROR: Could not write the C:\Program Files.
	ECHO ******* This must be run as an administrator
	PAUSE
	exit /b 1
)
:: /R=overwrite read-only ; /Y=don't prompt for overwrite
:: /S=skip empty dirs
xcopy /R /Y /S "%SCRIPT_DIR%*" "%DEST_DIR%"

xcopy /R /Y "%DEST_DIR%\Run_from_C.cmd" "%USERPROFILE%\Desktop\LaundryMate.cmd"
::echo "%DEST_DIR%\Run.cmd" > "%USERPROFILE%\Desktop\LaundryMate.cmd"


@echo off
title EOS

set dir=%CD%
set targetfolder=%dir%\Uncompressed\Client_online
set targetfile=%targetfolder%\EOSClient.exe

goto main  

:main
  cd %dir%
  java -jar EOSClientDownloader.jar
  goto startEOS
  
:startEOS	
  @echo on
  @echo Starting EOSClient as Administrator...
  @echo off
  cd %targetfolder%
  echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\startEOS.vbs"
  set params = %*:"="
  echo UAC.ShellExecute "%targetfile%", "/c %~s0 %params%", "", "runas", 1 >> "%temp%\startEOS.vbs"
  "%temp%\startEOS.vbs"
  del "%temp%\startEOS.vbs"
  exit /B
@echo off
cls

SET APP_JAVA_HOME=%JAVA_HOME%

if [%APP_JAVA_HOME%] == [] (
  echo [WARN] JAVA_HOME not set
  SET APP_JAVA_HOME=%USERPROFILE%\sistema-loja\jre
)

%APP_JAVA_HOME%\bin\java -cp lib\* br.com.jardelnovaes.sefaz.eventos.helper.Launcher

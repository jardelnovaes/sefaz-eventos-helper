#!/bin/bash

APP_JAVA_HOME="$JAVA_HOME"

if [ -z "%APP_JAVA_HOME" ]; then
  echo "[WARN] JAVA_HOME not set";
  APP_JAVA_HOME="${HOME}/sistema-loja/jre"
fi

$APP_JAVA_HOME/bin/java -cp "lib/*" br.com.jardelnovaes.sefaz.eventos.helper.Launcher

#!/bin/bash

export CLASSPATH=.
export CLASSPATH=${CLASSPATH}:./bin
export CLASSPATH=${CLASSPATH}:./build
export CLASSPATH=${CLASSPATH}:ext_jars/com.jar
export CLASSPATH=${CLASSPATH}:ext_jars/jcommon-0.9.6.jar
export CLASSPATH=${CLASSPATH}:ext_jars/jrendezvous.jar
export CLASSPATH=${CLASSPATH}:ext_jars/mem-moni.jar
export CLASSPATH=${CLASSPATH}:ext_jars/servlet.jar
export CLASSPATH=${CLASSPATH}:ext_jars/xal.jar
export CLASSPATH=${CLASSPATH}:ext_jars/xmlrpc-1.1.jar
export CLASSPATH=${CLASSPATH}:ext_jars/pbrawclient-0.0.2.jar
export CLASSPATH=${CLASSPATH}:ext_jars/protobuf-java-2.4.1.jar

echo "Classpath is ${CLASSPATH}"

java \
-Xmx512M -Xms512M \
-Dsun.java2d.pmoffscreen=false \
-Dsun.java2d.print.polling=false \
-Dlog4j.configuration=log4j.properties \
-classpath ${CLASSPATH} \
epics.archiveviewer.base.Launcher \
-u pbraw://cdlx27.slac.stanford.edu:17665/retrieval

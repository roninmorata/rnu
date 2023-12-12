@echo off

cd src\as\intel
if exist As.class del As.class
javac As.java
cd ..\..
if exist Main.class del Main.class
javac Main.java

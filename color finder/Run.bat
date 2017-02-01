@echo off
:start
clear
echo Color Finder
set /p file=File Path:
if exist %file% (
echo Starting Image Mode
java -jar Color.jar  %file% 
goto start
)
echo Starting Webcam Mode
set /p src=Source Number:
java -jar Color.jar %src%
goto start


#. /vha/app/shared/sbl/scripts/Environment.sh
. /vha/home/sbladm/.bash_profile
AI_Home=/vha/app/siebel/ai
cd $AI_Home/applicationcontainer/bin
BUILD_ID=dontKillMe nohup ./startup.sh &> /dev/null
sleep 10
Pid="$(pgrep -f "$AI_Home/applicationcontainer/bin")"
set +e
if [ "$Pid" ]
then
echo "AI application container service is running in PID:$Pid"
exit 0
else
echo "AI application container service is not running"
exit 1
fi
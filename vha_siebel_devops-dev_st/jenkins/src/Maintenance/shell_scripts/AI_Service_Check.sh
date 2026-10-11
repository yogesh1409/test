#. /vha/app/shared/sbl/scripts/Environment.sh
. /vha/home/sbladm/.bash_profile
AI_Home=/vha/app/siebel/ai
Pid="$(pgrep -f "$AI_Home/applicationcontainer/bin")"
set +e
CheckFor=$1
if [ $1 -eq 1 ]
then
P_exit_code=0
N_exit_code=1
else
P_exit_code=1
N_exit_code=0
fi

echo $P_exit_code
echo $N_exit_code

if [ "$Pid" ]
then
echo "AI application container service is running in PID:$Pid"
exit $P_exit_code
else
echo "AI application container service is not running"
exit $N_exit_code
fi

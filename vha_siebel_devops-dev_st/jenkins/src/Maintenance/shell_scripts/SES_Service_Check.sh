#. /vha/app/shared/sbl/scripts/Environment.sh
. /vha/home/sbladm/.bash_profile
set +e
SES_Home=/vha/app/siebel/ses
Pid="$(pgrep -f "$SES_Home/applicationcontainer/bin")"
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
set +e
if [ "$Pid" ]
then
echo "SES application container service is running in PID:$Pid"
exit $P_exit_code
else
echo "SES application container service is not running."
exit $N_exit_code
fi
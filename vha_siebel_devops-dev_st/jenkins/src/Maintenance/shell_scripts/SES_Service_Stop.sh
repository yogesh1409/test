set +e
. /vha/home/sbladm/.bash_profile
#. /vha/app/shared/sbl/scripts/Environment.sh
SES_Home=/vha/app/siebel/ses
cd $SES_Home/applicationcontainer/bin
./shutdown.sh
sleep 20
Pid="$(pgrep -f "$SES_Home/applicationcontainer/bin")"
set +e
if [ "$Pid" ]
then
echo "SES application container service is running in PID:$Pid"
exit 1
else
echo "SES application container service is not running."
exit 0
fi

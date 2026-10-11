#. /vha/app/shared/sbl/scripts/Environment.sh
. /vha/home/sbladm/.bash_profile
SES_Home=/vha/app/siebel/ses
Pid="$( ps -ef | grep "$SES_Home/siebsrvr/admin/" |grep -v grep |cut -d ' ' -f 5| uniq)"
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
echo "Siebel service is running with PID : $Pid"
exit $P_exit_code
else
echo "Siebel service is not running."
exit $N_exit_code
fi
#. /vha/app/shared/sbl/scripts/Environment.sh
. /vha/home/sbladm/.bash_profile
SES_Home=/vha/app/siebel/ses
cd $SES_Home/applicationcontainer/bin
BUILD_ID=dontKillMe nohup ./startup.sh &> /dev/null
echo "sleeping for 10 seconds"
sleep 10s
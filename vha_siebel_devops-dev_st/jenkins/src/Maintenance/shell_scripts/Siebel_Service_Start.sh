#. /vha/app/shared/sbl/scripts/Environment.sh
. /vha/home/sbladm/.bash_profile
SES_Home=/vha/app/siebel/ses
cd  $SES_Home/siebsrvr
. ./siebenv.sh
mwadm stop
BUILD_ID=dontKillMe nohup start_server all &> /dev/null
echo "sleeping for 5 mins"
sleep 5m
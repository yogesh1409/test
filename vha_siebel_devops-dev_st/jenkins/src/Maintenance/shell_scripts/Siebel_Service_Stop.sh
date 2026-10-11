#. /vha/app/shared/sbl/scripts/Environment.sh
. /vha/home/sbladm/.bash_profile
SES_Home=/vha/app/siebel/ses
cd $SES_Home/siebsrvr
. ./siebenv.sh
stop_server all
sleep 120
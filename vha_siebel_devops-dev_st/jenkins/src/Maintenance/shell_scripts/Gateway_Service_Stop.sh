#. /vha/app/shared/sbl/scripts/Environment.sh
. /vha/home/sbladm/.bash_profile
SES_Home=/vha/app/siebel/ses
cd $SES_Home/gtwysrvr
. ./siebenv.sh
cd bin
stop_ns
sleep 30
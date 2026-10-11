#. /vha/app/shared/sbl/scripts/Environment.sh
SES_Home=/vha/app/siebel/ses
cd  $SES_Home/gtwysrvr
. ./siebenv.sh
cd bin
BUILD_ID=dontKillMe nohup start_ns &> /dev/null
echo "sleeping for 5 mins"
sleep 1m
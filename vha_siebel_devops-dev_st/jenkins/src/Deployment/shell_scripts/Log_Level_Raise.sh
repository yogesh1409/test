Environment=$1
SES_Home=$2
AOM=$3
sadmin_passwd=$4

echo "$SES_Home"
echo "$AOM"
cd $SES_Home/siebsrvr/enterprises
enterprise=`ls -d *`
cd $SES_Home/siebsrvr
. ./siebenv.sh
cd bin
srvrmgr -g `hostname`:2320 -e $enterprise -u sadmin -p $sadmin_passwd -c "change evtloglvl %=1 for comp $AOM"

srvrmgr -g `hostname`:2320 -e $enterprise -u sadmin -p $sadmin_passwd -c "list active sessions for comp $AOM  show CC_ALIAS,OM_LOGIN,OM_VIEW,TK_TASKID" > /vha/app/shared/sbl/scripts/active_sessions.txt

echo "Hi Team, 

Log Level is to raised to 5 for Object Manager $AOM in $Environment by $BUILD_USER.

Please find below the Task id for the active sessions:

`sed -n '24,$p' /vha/app/shared/sbl/scripts/active_sessions.txt`

Regards,
Siebel Admin-Team
" | mailx -s "Task_id for $Environment"  -r 'DevSt-Jenkins-Admin@vodafone.com.au' 'nakul.pv@vodafone.com.au'


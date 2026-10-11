SES_Home=$1
Sadmin_Password=$2
Gateway_Host=$3
. /vha/home/sbladm/.bash_profile
cp /vha/app/shared/sbl/scripts/comp_param.cmd $SES_Home/siebsrvr/bin
cd $SES_Home/siebsrvr/enterprises
ENT=`ls|awk '{print $1}'`
cd $SES_Home/siebsrvr
. ./siebenv.sh
cd bin
srvrmgr -g $Gateway_Host:2320 -e $ENT -u sadmin -p $Sadmin_Password -i comp_param.cmd  
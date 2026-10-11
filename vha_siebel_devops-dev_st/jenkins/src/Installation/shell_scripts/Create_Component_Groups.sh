GatewayHost=$1
EnterPriseName=$2
sadmin_passwd=$3
comp_param_file=$4
SES_Home=$5

. /vha/home/sbladm/.bash_profile
cp /vha/app/automation/Comp_Defination/$comp_param_file $SES_Home/siebsrvr/bin
cd $SES_Home/siebsrvr/enterprises
ENT=`ls|awk '{print $1}'`
cd $SES_Home/siebsrvr
. ./siebenv.sh
cd bin
srvrmgr -g $GatewayHost:2320 -e $EnterPriseName -u sadmin -p $sadmin_passwd -i $comp_param_file 
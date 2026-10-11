Log_Base_Dir=$1
Ora_client_Home=$2
Response_File_Home=$3
FROM_MAIL_ID=$4
TO_MAIL_ID=$5

. /vha/home/sbladm/.bash_profile

if [ -d /vha/app/oracle/product/12.2.0/client_1 ]
then 
	echo " Oracle Client is installed"
    exit 0
else
	echo " Oracle Client is not installed"
fi


month=`date +"%m"`
date1=`date +"%d"`
year=`date +"%Y"`
time1=`date +"%H%M%S"`
DATE=$(date "+%Y-%m-%d_%H%M")
log_file=Oracle_client__$(hostname)_$DATE.log
log_dir=$Log_Base_Dir/Ora_Client_Install/$log_file
cd $Ora_client_Home/linuxx86_12201_client/client32
BUILD_ID=dontKillMe ./runInstaller -silent -responseFile $Response_File_Home/oracleclient_install.rsp -invPtrLoc /vha/app/oracle/oraInst.loc >> $log_dir
echo "Oracle Clinet installation successful. Going to sleep for 240 sec"
sleep 240
Oracle_Client="$(grep -o -E 'The installation of Oracle Client 12c was successful' $log_dir)"
cd $Log_Base_Dir/Ora_Client_Install
cp $Ora_client_Home/*.ora /vha/app/oracle/product/12.2.0/client_1/network/admin
if [[ "$(grep -o -E 'The installation of Oracle Client 12c was successful' $log_dir)" ]]
then
echo "Hi Team ,

$Oracle_Client on server $(hostname) is successful. Please review the attached log for reference / analysis.

Regards,
Siebel Admin Team" 
#| mailx -s "SBL_UPG | Oracle_Client_Install_Status | $(hostname)" -a $log_file -r $FROM_MAIL_ID $TO_MAIL_ID
                        exit 0
else
echo "Hi Team ,

Oracle client installation is failed , Please refer the attached Log for further details.

Regards,
Siebel Admin Team" | mailx -s "SBL_UPG | Oracle_Client_Install_Status | $(hostname)" -a $log_file -r $FROM_MAIL_ID $TO_MAIL_ID
                        exit 1
fi
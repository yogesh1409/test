Log_Base_Dir=$1
Siebel_Image_Home=$2
Response_File_Home=$3
FROM_MAIL_ID=$4
TO_MAIL_ID=$5

. /vha/home/sbladm/.bash_profile

if [ -d /vha/app/siebel/ses/gtwysrvr/zookeeper ]
then 
	echo " Gateway is installed"
    exit 0
else
	echo " Gateway is not installed"
fi

month=`date +"%m"`
date1=`date +"%d"`
year=`date +"%Y"`
time1=`date +"%H%M%S"`
DATE=$(date "+%Y-%m-%d_%H%M%S")
log_file=AI_Base_Install_$DATE.log
log_dir=$Log_Base_Dir/AI_Install/$log_file
cd $Siebel_Image_Home/Siebel_Install_Image_PS17_0_Linux/17.0.0.0/Linux/Server/Siebel_Enterprise_Server/Disk1/install
BUILD_ID=dontKillMe ./runInstaller -silent -responseFile $Response_File_Home/gtw_install.rsp -invPtrLoc /vha/app/oracle/oraInst.loc >> $log_dir
echo "Gateway Installation Completed. Going to sleep for 10 mins."
sleep 600
cd $Log_Base_Dir/AI_Install
GTW_Install_Status="$(grep -o -E 'Webserver Started successfully' $log_dir)"
export Summary=$(awk '/The installation of Gateway Server - enu was successful/{flag=1} flag; / Installation Completed Successfully/{flag=0}' $log_dir)
if [[ "$(grep -o -E 'Webserver Started successfully' $log_dir)" ]]
                        then
echo "Hi Team ,

Gateway Base Installation is completed. $GTW_Install_Status in server $(hostname). Please find the attached log for reference / analysis.

$Summary

Regards,
Siebel Admin Team" 
#| mailx -s "SBL_UPG | Gateway_Install_Status | $(hostname)" -a $log_file -r $FROM_MAIL_ID $TO_MAIL_ID
                        exit 0
else
echo "Hi Team ,

Gateway Base installation failed in in server. Please find the attached log for reference / analysis.

Regards,
Siebel Admin Team" | mailx -s "SBL_UPG | Gateway_Install_Status | $(hostname)" -a $log_file -r $FROM_MAIL_ID $TO_MAIL_ID
                        exit 1
fi
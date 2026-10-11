Log_Base_Dir=$1
Siebel_Image_Home=$2
Response_File_Home=$3
FROM_MAIL_ID=$4
TO_MAIL_ID=$5

. /vha/home/sbladm/.bash_profile

if [ -d /vha/app/siebel/ai/applicationcontainer/webapps ]
then 
	echo " AI is installed"
    exit 0
else
	echo " AI is not installed"
fi

month=`date +"%m"`
date1=`date +"%d"`
year=`date +"%Y"`
time1=`date +"%H%M%S"`
DATE=$(date "+%Y-%m-%d_%H%M%S")
log_file=AI_Base_Install_$DATE.log
log_dir=$Log_Base_Dir/AI_Install/$log_file
cd $Siebel_Image_Home/Siebel_Install_Image_PS17_0_Linux/17.0.0.0/Linux/Server/Siebel_Enterprise_Server/Disk1/install
BUILD_ID=dontKillMe ./runInstaller -silent -responseFile $Response_File_Home/ai_install.rsp -invPtrLoc /vha/app/oracle/oraInst.loc
#echo "Installation Completed. Going to sleep for 10 mins."
sleep 60
cd /vha/app/oracle/oraInventory/logs
var=`ls -Art *.out| tail -n 1`
if [[ "$(grep -o -E 'Installation Completed Successfully' $var)" ]]
			then
echo "Hi Team ,

AI Base Installation is completed. $AI_Install_Status in server $(hostname). Please find the attached log for reference / analysis.

$Summary

Regards,
Siebel Admin Team" 
#| mailx -s "SBL_UPG | ApplicationInterface_Install_Status | $(hostname)" -a $log_file -r $FROM_MAIL_ID $TO_MAIL_ID
                        exit 0
else
echo "Hi Team ,

AI Base installation failed in in server $(hostname). Please find the attached log for reference / analysis.

Regards,
Siebel Admin Team" 
#| mailx -s "SBL_UPG | ApplicationInterface_Install_Status | $(hostname)" -a $log_file -r $FROM_MAIL_ID $TO_MAIL_ID
                        exit 1
fi
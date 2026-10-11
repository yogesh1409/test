Log_Base_Dir=$1
Siebel_Image_Home=$2
Response_File_Home=$3
FROM_MAIL_ID=$4
TO_MAIL_ID=$5
Res_File_Name=$6

. /vha/home/sbladm/.bash_profile

if [ -d /vha/app/siebel/ses/applicationcontainer/webapps ]
then 
	echo " App Server is installed"
    exit 0
else
	echo " App Server is not installed"
fi

month=`date +"%m"`
date1=`date +"%d"`
year=`date +"%Y"`
time1=`date +"%H%M%S"`
DATE=$(date "+%Y-%m-%d_%H%M%S")
log_file=SES_Base_Install_$DATE.log
log_dir=$Log_Base_Dir/SES_Install/$log_file
cd $Siebel_Image_Home/Siebel_Install_Image_PS17_0_Linux/17.0.0.0/Linux/Server/Siebel_Enterprise_Server/Disk1/install
BUILD_ID=dontKillMe ./runInstaller -silent -responseFile $Response_File_Home/$Res_File_Name -invPtrLoc /vha/app/oracle/oraInst.loc
echo "Before Sleep"
sleep 300
echo "after sleep"
cd /vha/app/oracle/oraInventory/logs
var=`ls -Art *.out| tail -n 1`

if [[ "$(grep -o -E 'Webserver Started successfully' $var)" ]]
                        then
echo "Hi Team ,

Siebel Enterprise Server Base Installation is completed in server $(hostname). Please find the attached log for reference / analysis.



Regards,
Siebel Admin Team" 
#| mailx -s "SBL_UPG | SES_Install_Status | $(hostname)" -a $var -r $FROM_MAIL_ID $TO_MAIL_ID
exit 0
else
echo "Hi Team ,

Siebel Enterprise Server Base installation failed in in server $(hostname). Please find the attached log for reference / analysis.


Regards,
Siebel Admin Team" 
#| mailx -s "SBL_UPG | SES_Install_Status | $(hostname)" -a $var -r $FROM_MAIL_ID $TO_MAIL_ID
exit 1
fi
echo "End of code"
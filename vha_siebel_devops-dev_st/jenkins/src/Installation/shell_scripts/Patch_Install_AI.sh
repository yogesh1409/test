Log_Base_Dir=$1
Siebel_Patch_Home=$2
Response_File_Home=$3

. /vha/home/sbladm/.bash_profile
month=`date +"%m"`
date1=`date +"%d"`
year=`date +"%Y"`
time1=`date +"%H%M%S"`

DATE=$(date "+%Y-%m-%d_%H%M%S")
log_file=AI_patch_Install_$DATE.log
log_dir=$Log_Base_Dir/AI_Patch/$log_file
cd $Siebel_Patch_Home/Siebel_Latest_Patch/Siebel_Install_Image/Linux/Server/Siebel_Enterprise_Server/Disk1/install
BUILD_ID=dontKillMe ./runInstaller.sh -silent -responseFile $Response_File_Home/ai_patch_install -invPtrLoc $INVENTORY_PATH/oraInst.loc >> $log_dir

sleep 600

PS_Status="$(grep -o -E 'Installation Completed Successfully' "$log_dir")"
Siebel_Version="$(grep 'Siebel Version:' $log_dir | sed 's/^.*: //')"
cd $Log_Base_Dir/AI_Patch
if [[ "$PS_Status" == 'Installation Completed Successfully' ]]
			then
echo "Hi Team ,

Application Interface Patch Set $Siebel_Version applied Successfully in $(hostname), Please refer the attached Log for further details.
		
Regards,
Siebel Admin Team" 
#| mailx -s "Patch_Install_AI_Status | $(hostname)" -a $log_file -r $FROM_MAIL_ID $TO_MAIL_ID
			exit 0
		

else 
		
echo "Hi Team ,
			
Application Interface Patch Set $Siebel_Version Failed in $(hostname) , Please refer the attached Log for further details.
			
Regards,
Siebel Admin Team" | mailx -s "Patch_Install_AI_Status | $(hostname)" -a $log_file -r $FROM_MAIL_ID $TO_MAIL_ID
			exit 1
fi



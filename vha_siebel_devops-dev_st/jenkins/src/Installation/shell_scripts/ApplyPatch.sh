Log_Base_Dir=$1
Siebel_Patch_Home=$2
Response_File_Home=$3
Server_Type=$4
FROM_MAIL_ID=$5
TO_MAIL_ID=$6
Response_File_Name=$7

. /vha/home/sbladm/.bash_profile
month=`date +"%m"`
date1=`date +"%d"`
year=`date +"%Y"`
time1=`date +"%H%M%S"`

DATE=$(date "+%Y-%m-%d_%H%M%S")
file_path=/vha/app/shared/sbl/siebel_dumps/patch_set/Siebel_Latest_Patch/Siebel_Install_Image/Linux/Server/Siebel_Enterprise_Server/Disk1/install
file_name=oraparam.ini
full_path=$file_path/$file_name
search_String="SIEBEL_VERSION"
PatchsetVersion=$(cat $full_path|grep $search_String|cut -d'=' -f2)
echo "Patchset Version"
echo $PatchsetVersion

if [ "$Server_Type" = 'AI_SERVER' ];
then
	AIServerPath=/vha/app/siebel/ai
	AIServerFile=Siebel_version.properties
	AIServerFullPath=$AIServerPath/$AIServerFile

	ServerVersion=$(cat $AIServerFullPath|grep $search_String|cut -d'=' -f2)
	echo "Current AI Patch Version"
	echo $ServerVersion

	if [ "$PatchsetVersion" = "$ServerVersion" ];
	then
	echo "Patch Version is upto date $PatchsetVersion"
	exit 0
	else
	echo "Patch Version out of date. Current Version $ServerVersion to be version $PatchsetVersion"
	fi

	echo "Taking backup of CSS, JS, Images and Jar Files"
	mkdir -p /vha/app/shared/sbl/siebel_dumps/File_Backup/CSS_Backup
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/CSS_Backup
	rm -rf *
	mkdir -p /vha/app/shared/sbl/siebel_dumps/File_Backup/JS_Backup
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/JS_Backup
	rm -rf *
	mkdir -p /vha/app/shared/sbl/siebel_dumps/File_Backup/Image_Backup
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/Image_Backup
	rm -rf *
	mkdir -p /vha/app/shared/sbl/siebel_dumps/File_Backup/Jar_Backup
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/Jar_Backup
	rm -rf *
	mkdir -p /vha/app/shared/sbl/siebel_dumps/File_Backup/Health_Monitor
	rm -rf *
	
	cd /vha/app/siebel/ai/applicationcontainer/webapps/siebel/files/custom
	cp * /vha/app/shared/sbl/siebel_dumps/File_Backup/CSS_Backup
	#cd /vha/app/siebel/ai/applicationcontainer/webapps/siebel/files
	#cp theme-aurora.css login.css /vha/app/shared/sbl/siebel_dumps/File_Backup/CSS_Backup || :
	cd /vha/app/siebel/ai/applicationcontainer/webapps/siebel/scripts/siebel/custom
	cp * /vha/app/shared/sbl/siebel_dumps/File_Backup/JS_Backup
	cd /vha/app/siebel/ai/applicationcontainer/webapps/siebel/scripts/siebel
	cp core-siebel.js cfgrenderer.js /vha/app/shared/sbl/siebel_dumps/File_Backup/JS_Backup || :
	cd /vha/app/siebel/ai/applicationcontainer/webapps/siebel/images/custom
	cp * /vha/app/shared/sbl/siebel_dumps/File_Backup/Image_Backup
	cd /vha/app/siebel/ai/applicationcontainer/webapps/siebel/images
	cp mbbplans.png mobileplans.png vfnbnplans.png /vha/app/shared/sbl/siebel_dumps/File_Backup/Image_Backup || :
	cd /vha/app/siebel/ai/applicationcontainer/webapps/siebel
	cp *.html /vha/app/shared/sbl/siebel_dumps/File_Backup/Health_Monitor || :
	
	
	log_file=AI_patch_Install_$DATE.log
	log_dir=$Log_Base_Dir/AI_Patch/$log_file
	cd $Siebel_Patch_Home/Siebel_Latest_Patch/Siebel_Install_Image/Linux/Server/Siebel_Enterprise_Server/Disk1/install
	BUILD_ID=dontKillMe ./runInstaller.sh -silent -responseFile $Response_File_Home/$Response_File_Name -invPtrLoc /vha/app/oracle/oraInst.loc
	sleep 600
	echo "Restoring CSS, JS and Images."
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/CSS_Backup
	cp * /vha/app/siebel/ai/applicationcontainer/webapps/siebel/files/custom
	#cp theme-aurora.css login.css /vha/app/siebel/ai/applicationcontainer/webapps/siebel/files || :
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/JS_Backup
	cp * /vha/app/siebel/ai/applicationcontainer/webapps/siebel/scripts/siebel/custom
	cp core-siebel.js cfgrenderer.js /vha/app/siebel/ai/applicationcontainer/webapps/siebel/scripts/siebel || :
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/Image_Backup
	cp * /vha/app/siebel/ai/applicationcontainer/webapps/siebel/images/custom
	cp mbbplans.png mobileplans.png vfnbnplans.png /vha/app/siebel/ai/applicationcontainer/webapps/siebel/images || :
	echo "Restoring F5 Health Monitoring Files"
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/Health_Monitor
	cp * /vha/app/siebel/ai/applicationcontainer/webapps/siebel || :
	
elif [ "$Server_Type" = 'GTW_APP_SERVER' ];
then
	Gtw_AppServerPath=/vha/app/siebel/ses
	Gtw_AppServerFile=Siebel_version.properties
	Gtw_AppServerFullPath=$Gtw_AppServerPath/$Gtw_AppServerFile

	ServerVersion=$(cat $Gtw_AppServerFullPath|grep $search_String|cut -d'=' -f2)
	echo "Current AI Patch Version"
	echo $ServerVersion

	if [ "$PatchsetVersion" = "$ServerVersion" ];
	then
	echo "Patch Version is upto date $PatchsetVersion"
	exit 0
	else
	echo "Patch Version out of date. Current Version $ServerVersion to be version $PatchsetVersion"
	fi

	mkdir -p /vha/app/shared/sbl/siebel_dumps/File_Backup/Health_Monitor
	echo "Taking backup of Jar files"
	cd /vha/app/siebel/ses/applicationcontainer/webapps/siebel/WEB-INF/lib
	cp jta.jar itext-licensekey-1.0.3.jar Invoke.jar CreatePDF.jar aqapi.jar jndi.jar jmscommon.jar itextpdf-5.3.4.jar ojdbc8.jar QRCodeProcessor.jar orai18n.jar zxing-core-1.6.jar xdb.jar VHAPDF.jar /vha/app/shared/sbl/siebel_dumps/File_Backup/Jar_Backup || :
	echo "Taking backup of health monitor files"
	cd /vha/app/siebel/ses/gtwysrvr/
	cp *.html /vha/app/shared/sbl/siebel_dumps/File_Backup/Health_Monitor || :

	log_file=Gtw_App_patch_Install_$DATE.log
	log_dir=$Log_Base_Dir/AI_Patch/$log_file
	cd $Siebel_Patch_Home/Siebel_Latest_Patch/Siebel_Install_Image/Linux/Server/Siebel_Enterprise_Server/Disk1/install
	BUILD_ID=dontKillMe ./runInstaller.sh -silent -responseFile $Response_File_Home/$Response_File_Name -invPtrLoc /vha/app/oracle/oraInst.loc
	sleep 600
	echo "Restoring Jar Files"
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/Jar_Backup
	cp * /vha/app/siebel/ses/applicationcontainer/webapps/siebel/WEB-INF/lib || :
	echo "Restoring Health Monitor Files"
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/Health_Monitor
	cp * /vha/app/siebel/ses/gtwysrvr || :

else
	AppServerPath=/vha/app/siebel/ses
	AppServerFile=Siebel_version.properties
	AppServerFullPath=$AppServerPath/$AppServerFile

	ServerVersion=$(cat $AppServerFullPath|grep $search_String|cut -d'=' -f2)
	echo "Current App Patch Version"
	echo $ServerVersion

	if [ "$PatchsetVersion" = "$ServerVersion" ];
	then
	echo "Patch Version is upto date $PatchsetVersion"
	exit 0
	else
	echo "Patch Version out of date. Current Version $ServerVersion to be version $PatchsetVersion"
	fi

	echo "Taking backup of Jar files"
	cd /vha/app/siebel/ses/applicationcontainer/webapps/siebel/WEB-INF/lib
	cp jmscommon.jar aqapi.jar jndi.jar ojdbc8.jar xdb.jar QRCODE_ENV.properties orai18n.jar CreatePDF.jar Invoke.jar QRCodeProcessor.jar InvokeCryption.jar jta.jar itextpdf-5.3.4.jar itext-licensekey-1.0.3.jar /vha/app/shared/sbl/siebel_dumps/File_Backup/Jar_Backup || :
	echo "Taking backup of health monitor files"
	cd /vha/app/siebel/ses/gtwysrvr/
	cp *.html /vha/app/shared/sbl/siebel_dumps/File_Backup/Health_Monitor || :

	log_file=SES_patch_Install_$DATE.log
	log_dir=$Log_Base_Dir/SES_Patch/$log_file
	cd $Siebel_Patch_Home/Siebel_Latest_Patch/Siebel_Install_Image/Linux/Server/Siebel_Enterprise_Server/Disk1/install
	BUILD_ID=dontKillMe ./runInstaller.sh -silent -responseFile $Response_File_Home/$Response_File_Name -invPtrLoc /vha/app/oracle/oraInst.loc
	sleep 600
	echo "Restoring Jar Files"
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/Jar_Backup
	cp * /vha/app/siebel/ses/applicationcontainer/webapps/siebel/WEB-INF/lib || :
	echo "Restoring Health Monitor Files"
	cd /vha/app/shared/sbl/siebel_dumps/File_Backup/Health_Monitor
	cp * /vha/app/siebel/ses/gtwysrvr
fi


#cd /vha/app/oracle/oraInventory/logs
#var=`ls -Art *.out| tail -n 1`
#if [ "$PatchsetVersion" = "$ServerVersion" ];
#			then
#echo "Hi Team ,
#
#Patch Set applied Successfully in `hostname` $Server_Type, Please refer the attached Log for further details.
#		
#Regards,
#Siebel Admin Team" | mailx -s "Patch_Install_Status | `hostname`" -a $var -r $FROM_MAIL_ID $TO_MAIL_ID
#			exit 0
#		
#
#else 
#		
#echo "Hi Team ,
#			
#Patch Set failed in `hostname` $Server_Type, Please refer the attached Log for further details.
#			
#Regards,
#Siebel Admin Team" | mailx -s "Patch_Install_Status | `hostname`" -a $var -r $FROM_MAIL_ID $TO_MAIL_ID
#			exit 1
#fi
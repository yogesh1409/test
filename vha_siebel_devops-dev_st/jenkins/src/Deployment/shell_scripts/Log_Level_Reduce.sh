Environment=$1
SES_Home=$2
Task_Id=$3
sadmin_passwd=$4
Mail_Id=$5

cd $SES_Home/siebsrvr/enterprises
enterprise=`ls -d *`
cd $enterprise
siebel_server=`ls -d *`
cd $siebel_server/log

LogFile=$(ls  *$Task_Id*)
echo $LogFile
cp $LogFile $LogFile._temp
temp_file_name=$(echo $LogFile|cut -f 1 -d '.')
echo $Mail_Id
tar -czvf $temp_file_name".tar.gz" $LogFile._temp
echo "Hi Team,

Please find attached the log file from $Environment env.

Regards,
Siebel-Admin-Team

" | mailx -s "LogFile" -r 'DevSt-Jenkins-Admin@vodafone.com.au' -a $temp_file_name".tar.gz" $Mail_Id

cd $SES_Home/siebsrvr
. ./siebenv.sh
cd bin
srvrmgr -g `hostname`:2320 -e $enterprise -u sadmin -p $sadmin_passwd -i logdec.cmd

echo "Hi Team,

Reverted log level to 1 for all object managers in $Environment
This Jenkins Job was triggered by $BUILD_USER.


Regards,
Siebel-Admin-Team
" | mailx -s "Log Level Reset for $Environment"  -r 'DevSt-Jenkins-Admin@vodafone.com.au' 'nakul.pv@vodafone.com.au'
echo "Log Level Reduced for all components."



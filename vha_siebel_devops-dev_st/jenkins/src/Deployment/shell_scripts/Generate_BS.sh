SES_Home=$1
AI_Home=$2
TNS_Name=$3
sadmin_passwd=$4
AI_Host=$5
Genb_Home=$6
cd $SES_Home/siebsrvr
. ./siebenv.sh
cd bin  
echo "Inside siebsrvr/bin"
echo "${TNS_Name}_DSN"
echo $SES_Home
echo $sadmin_passwd
#echo "#./genbscript /c $SES_Home/siebsrvr/bin/enu/siebel.cfg /vha/app/script/genb /l enu /u sadmin /p $sadmin_passwd /d ${TNS_Name}_DSN"
genbscript /c $SES_Home/siebsrvr/bin/enu/siebel.cfg /vha/app/script/genb /l enu /u sadmin /p $sadmin_passwd /d ${TNS_Name}_DSN
cd /vha/app/script/genb
scp -r `ls -ltr | tail -1 | awk '{ print $9 }'` $AI_Host:$AI_Home/applicationcontainer/webapps/siebel
: 'if [[ "$(grep -o -E 'Successfully created' genb.log)" ]]
then
echo "Hi Team ,

Browser scripts generated successfully $(hostname)
echo "Inside Commented If Part"
Regards,
Siebel Admin Team" 
                        exit 0
else
echo "Hi Team ,

Browser scripts generated failed $(hostname)
echo "Inside Commented Else Part "
Regards,
Siebel Admin Team"
                        exit 1
fi'
echo "End of shell script"
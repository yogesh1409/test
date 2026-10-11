Script_Base_Dir=$1
FROM_MAIL_ID=$2
TO_MAIL_ID=$3


. /vha/home/sbladm/.bash_profile
if [ -d /vha/app/ ]
then 
	echo " VHA home directory present"
else
	echo " VHA home directory not present"
echo "Hi Team ,

VHA home directory is Not Present in Server: $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Server_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi

if [ -d /vha/app/shared ]
then 
	echo " /vha/app/shared directory present"
else
	echo " /vha/app/shared is not  directory not present"
echo "Hi Team ,

VHA Shared Directory is Not Present in Server: $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Server_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi

if [ -d /vha/app/shared/sbl/siebel_dumps ]
then 
	echo " /siebel_dumps directory present"
else
	echo " /siebel_dumps directory is not present"
echo "Hi Team ,

VHA Siebel_Dumps directory is Not Present in Server: $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Server_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi

rpm -qa --queryformat "%{name} - %{Arch}\n" > $Script_Base_Dir/rpm_existing.txt

IFS=$'\n' array=($(cat $Script_Base_Dir/rpm_required.txt))
flag=0
echo -e "\n" > $Script_Base_Dir/rpm_final.txt
for i in "${array[@]}"
do
	if grep -q "^$i" $Script_Base_Dir/rpm_existing.txt; then
    		echo $i "found"
	else
    		echo $i "not found" >> $Script_Base_Dir/rpm_final.txt
			flag=1
	fi
done
echo "Flag Value is $flag"
if [ $flag == 1 ]
then
echo "Hi Team ,

Below mentioned rpm packages are not installed in Server $(hostname):

`more $Script_Base_Dir/rpm_final.txt`


Regards,
Siebel Admin Team" | mailx -s "CheckServerConfig | RPM packages | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
exit 1
else
echo "All rpm packages present" 
fi

echo "Hi Team ,

Server Verification Done Successfully in Server: $(hostname).


Regards,
Siebel Admin Team" 
#| mailx -s "Server_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
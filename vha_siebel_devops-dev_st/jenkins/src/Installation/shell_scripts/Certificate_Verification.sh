Siebel_Dumps_Home=$1
FROM_MAIL_ID=$2
TO_MAIL_ID=$3
SERVER_HOST=$4

. /vha/home/sbladm/.bash_profile


if [ -d $Siebel_Dumps_Home/certificate ]
then 
	echo " Certificate Directory present"
else
	echo " Certificate Directory not present"
echo "Hi Team ,

Certificate Directory is not present in server $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Certificate_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi


if [ -f $Siebel_Dumps_Home/certificate/siebelkeystore.jks ]
then 
	echo " Siebel Keystore File is present "
else
	echo " Siebel Keystore File is not present "
echo "Hi Team ,

Siebel Keystore jks file is not present in server $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Certificate_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi


if [ -f $Siebel_Dumps_Home/certificate/siebeltruststore.jks ]
then 
	echo " Siebel Trustore File is present "
else
	echo " Siebel Truststore File is not present "
echo "Hi Team ,

Siebel Truststore jks file is not present in server $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Certificate_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi

FilePath=$Siebel_Dumps_Home/certificate/siebelkeystore.jks

check=$(keytool -list -v -storepass siebel -keystore $FilePath|grep -ci $SERVER_HOST.vodafone.com.au)

echo $check

if [ $check -gt 0 ]; then
echo "The host $SERVER_HOST is part of the certificate keystore $FilePath"
else
echo "The host $SERVER_HOST is NOT part of the certificate keystore $FilePath"
echo "Hi Team ,

The host $SERVER_HOST is NOT part of the certificate keystore $FilePath.


Regards,
Siebel Admin Team" | mailx -s "Certificate_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
exit 1
fi


echo "Hi Team ,

Certificate Check Completed Successfully in Server: $(hostname).


Regards,
Siebel Admin Team" 
#| mailx -s "Certificate_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID

exit 0
ServerHost=$1
FROM_MAIL_ID=$2
TO_MAIL_ID=$3
. /vha/home/sbladm/.bash_profile
if curl -k --fail "https://$ServerHost.vodafone.com.au:9001/siebel/smc/"  ;
then 

	echo "Hi Team ,
		  SMC Application has been verified for $ServerHost. Its up and running.			
		  URL:  https://$ServerHost.vodafone.com.au:9001/siebel/smc/
			
Regards,
Siebel Admin Team" 
#| mailx -s "Verify SMC" -r $FROM_MAIL_ID $TO_MAIL_ID
			exit 0
else
	echo "Failed"
    echo "Hi Team ,
		  SMC Application has been failed for $ServerHost.			
		  URL:  https://$ServerHost.vodafone.com.au:9001/siebel/smc/
			
Regards,
Siebel Admin Team" | mailx -s "Verify SMC" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi 
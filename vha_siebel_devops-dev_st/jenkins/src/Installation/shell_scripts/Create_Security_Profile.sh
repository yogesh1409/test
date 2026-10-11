AI_Host=$1
Sadmin_Password=$2
EnvName=$3

cd /vha/app/automation/JSON_Input


#out=$(curl -u sadmin:$Sadmin_Password -i -X GET -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/profiles/security" -k)

#echo $out

#result=$(echo $out|grep -c "$EnvName_DataSource")


#if [ $result -gt 0 ]; then
#echo "Security profile deployed"
#exit 0
#else
#echo "Security profile not deployed"
#fi


exit_status=$(curl -u sadmin:$Sadmin_Password -i -X POST -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/GatewaySecurityProfile/" -d@SecurityProfile_output.json -k)
echo $exit_status
if [[ $exit_status == *"HTTP/1.1 200"* ]];
then
echo "Success, Security Profile Has been created."
sleep 10
else
echo "Failure, Security Profile creation has been failed."
exit 1
fi

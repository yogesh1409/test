Sadmin_Password=$2
AI_Host=$1
EnvName=$3


#out=$(curl -u sadmin:$Sadmin_Password -i -X GET -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/profiles/swsm/$EnvName_AI_Profile" -k)


#echo $out

#result=$(echo $out|grep -c "$EnvName_AI_Profile")


#if [ $result -gt 0 ]; then
#echo "AI profile deployed"
#exit 0
#else
#echo "AI profile not deployed"
#fi


cd /vha/app/automation/JSON_Input
exit_status=$(curl -u sadmin:$Sadmin_Password -i -X POST -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/profiles/swsm/" -d@AIProfile_output.json -k)
echo $exit_status
if [[ $exit_status == *"HTTP/1.1 200"* ]];
then
echo "Success, AI Profile has been created."
sleep 10
else
echo " AI Profile has been failed to create."
exit 1
fi

AI_Host=$1
sadmin_passwd=$2
EnvName=$3

cd /vha/app/automation/JSON_Input

#out=$(curl -u sadmin:$sadmin_passwd -i -X GET -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/profiles/enterprises" -k)

#echo $out

#result=$(echo $out|grep -c "$EnvName_Enterprise_Profile")


#if [ $result -gt 0 ]; then
#echo "Enterprise profile deployed"
#exit 0
#else
#echo "Enterprise profile not deployed"
#fi


exit_status=$(curl -u sadmin:$sadmin_passwd -i -X POST -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/profiles/enterprises/" -d@EnterpriseProfile_output.json -k)
echo $exit_status
if [[ $exit_status == *"HTTP/1.1 200"* ]];
then
echo "Success, Enterprise Profile has been created."
sleep 10
exit 0
else
echo "Failure, Enterprise Profile failed to create."
exit 1
fi
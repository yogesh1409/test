AI_Host=$1
Sadmin_Password=$2

cd /vha/app/automation/JSON_Input

out=$(curl -u sadmin:$Sadmin_Password -i -X GET -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/bootstrapCG" -k)

echo $out

result=$(echo $out|grep -c "9111")

echo $result

if [ $result -gt 0 ]; then
echo "Bootstrap profile deployed"
exit 0
else
echo "Bootstrap profile not deployed"
fi



exit_status=$(curl -u sadmin:$Sadmin_Password -i -X POST -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/bootstrapCG" -d@BootstrapProfile_output.json -k)
echo $exit_status
if [[ $exit_status == *"HTTP/1.1 200"* ]];
then
echo "Success, Bootstrap Profile has been created."
sleep 10
else
echo "Failure, Bootstrap Profile has been failed to create."
exit 1
fi

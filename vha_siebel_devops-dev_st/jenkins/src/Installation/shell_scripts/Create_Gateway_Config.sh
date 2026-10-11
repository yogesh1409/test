AI_Host=$1
Sadmin_Password=$2
Path="/vha/app/automation/JSON_Input/"

cd /vha/app/automation/JSON_Input

out=$(curl -u sadmin:$Sadmin_Password -i -X GET -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cginfo" -k)

echo $out

result=$(echo $out|grep -c "cloudgateway")

echo $result

if [ $result -eq 0 ]; then
echo "Gateway config Not deployed"
else
echo "Gateway config deployed"
exit 0
fi


exit_status=$(curl -u admin:admin -i -X POST -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cginfo" -d@GatewayConfig_output.json -k)
echo $exit_status
if [[ $exit_status == *"HTTP/1.1 200"* ]];
then
echo "Success, Gateway Config has been created."
sleep 10
else
echo "Failure, Gateway Config failed to create."
exit 1
fi

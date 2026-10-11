Sadmin_Password=$2
AI_Host=$1
Enterprise_Name=$3
set +xe
cd /vha/app/automation/JSON_Input

exit_status2=$(curl -u sadmin:$Sadmin_Password -i -X GET -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/deployments/enterprises/$Enterprise_Name" -k)
echo $exit_status2
result2=$(echo $exit_status2|grep -c "Deployed")
echo $result2
if [ $result2 -gt 0 ];
then
echo "Enterprise Server Already Deployed"
exit 0
fi
curl -u sadmin:$Sadmin_Password -i -X POST -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/deployments/enterprises/" -d@EnterpriseDeploy_output.json -k
a=0
while [ $a -lt 3 ]
do
    echo "Checking if the Enterprise deploy is successful"
   echo $a
   echo "Going to Sleep for 180 sec"
   sleep 180
   exit_status=$(curl -u sadmin:$Sadmin_Password -i -X GET -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/deployments/enterprises/$Enterprise_Name" -k)
   echo $exit_status
   result=$(echo $exit_status|grep -c "Deployed")
   echo $result
   if [ $result -gt 0 ];
   then
   echo "Enterprise deploy is successful"
      exit 0
   fi
   a=`expr $a + 1`
done
exit 1
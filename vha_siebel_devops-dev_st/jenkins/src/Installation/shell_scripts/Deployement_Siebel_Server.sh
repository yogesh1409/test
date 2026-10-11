AI_Host=$1
Sadmin_Password=$2
set +xe
cd /vha/app/automation/JSON_Input
curl -u sadmin:$Sadmin_Password -i -X POST -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/deployments/servers/" -d@SiebelServerDeploy_output.json -k
a=0
while [ $a -lt 3 ]
do
   echo $a
   sleep 180
   exit_status=$(curl -u sadmin:$Sadmin_Password -i -X GET -H "Content-Type:application/json" "https://$AI_Host:9001/siebel/v1.0/cloudgateway/deployments/servers/$APP_Host" -k)
   if echo $exit_status | grep -q "Deployed"
   then
   	   exit 0
   fi
   a=`expr $a + 1`
done
exit 0
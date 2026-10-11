Server_Type = $1


. /vha/home/sbladm/.bash_profile

month=`date +"%m"`
date1=`date +"%d"`
year=`date +"%Y"`
time1=`date +"%H%M%S"`

DATE=$(date "+%Y-%m-%d_%H%M%S")

search_String="SIEBEL_VERSION"

if [ "$Server_Type" = 'AI_SERVER' ];
then
    AIServerPath=/vha/app/siebel/ai
	AIServerFile=Siebel_version.properties
	AIServerFullPath=$AIServerPath/$AIServerFile

	AIServerVersion=$(cat $AIServerFullPath|grep $search_String|cut -d'=' -f2)
    
	echo "Current AI Patch Version"
	echo $AIServerVersion


    echo "Change directory to /vha/app/siebel/ai/oui/bin"
    cd /vha/app/siebel/ai/oui/bin

elif [ "$Server_Type" = 'APP_SERVER' ];
then

    AppServerPath=/vha/app/siebel/ses
    AppServerFile=Siebel_version.properties
    AppServerFullPath=$AppServerPath/$AppServerFile

    AppServerVersion=$(cat $AppServerFullPath|grep $search_String|cut -d'=' -f2)
    echo "Current App/Gateway Patch Version"
    echo $AppServerVersion

    echo "Change directory to /vha/app/Siebel/ses/oui/bin"
    cd /vha/app/Siebel/ses/oui/bin

else
    echo "Server Type not available"
    exit 1
fi


./patchrollback.sh -invPtrLoc /vha/app/oracle/oraInst.loc


if [ "$Server_Type" = 'AI_SERVER' ];
then
    AIServerPath=/vha/app/siebel/ai
	AIServerFile=Siebel_version.properties
	AIServerFullPath=$AIServerPath/$AIServerFile

	Old_AIServerVersion=$(cat $AIServerFullPath|grep $search_String|cut -d'=' -f2)
    
	echo "Old AI Patch Version"
	echo $Old_AIServerVersion

    if [ "$AIServerVersion" != "$Old_AIServerVersion" ];
	then
        echo "Patch Rolled back from $AIServerVersion to $Old_AIServerVersion"
        exit 0
	else
        echo "Patch Rollback failed"
        exit 1
	fi

elif [ "$Server_Type" = 'APP_SERVER' ];
then

    AppServerPath=/vha/app/siebel/ses
    AppServerFile=Siebel_version.properties
    AppServerFullPath=$AppServerPath/$AppServerFile

    Old_AppServerVersion=$(cat $AppServerFullPath|grep $search_String|cut -d'=' -f2)
    echo "Old App/Gateway Patch Version"
    echo $Old_AppServerVersion

     if [ "$AppServerVersion" != "$Old_AppServerVersion" ];
	then
        echo "Patch Rolled back from $AppServerVersion to $Old_AppServerVersion"
        exit 0
	else
        echo "Patch Rollback failed"
        exit 1
	fi

else
    echo "Server Type not available"
    exit 1
fi


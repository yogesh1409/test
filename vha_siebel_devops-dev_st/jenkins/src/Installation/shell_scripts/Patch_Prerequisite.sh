ServerHost=$1
Siebel_Dumps_Home=$2

. /vha/home/sbladm/.bash_profile
if [ -d $Siebel_Dumps_Home/patch_set/Siebel_Latest_Patch/Siebel_Install_Image/Linux/Server/Siebel_Enterprise_Server/Disk1/install ]
then 
	exit 0
else
	exit 1
fi

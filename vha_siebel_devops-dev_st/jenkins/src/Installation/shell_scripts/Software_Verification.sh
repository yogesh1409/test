Siebel_Dumps_Home=$1
FROM_MAIL_ID=$2
TO_MAIL_ID=$3

. /vha/home/sbladm/.bash_profile

if [ -d /vha/app/oracle/product/12.2.0/client_1 ]
then 
	echo " Oracle Client is installed"
else
	echo " Oracle Client is not installed"
echo "Hi Team ,

Oracle Client is not Installed in Server: $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Software_Verification | Oracle Client | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi

# if [ -f $Siebel_Dumps_Home/Response_File/ai_base_install ]
# then 
# 	echo " AI Base Install Response File is present"
# else
# 	echo " AI Base Install Response File is not present."
# echo "Hi Team ,

# AI Base Install Response File is Not Present in Server: $(hostname).


# Regards,
# Siebel Admin Team" | mailx -s "Software_Verification |AI_Base_Response File | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
#     exit 1
# fi


# if [ -f $Siebel_Dumps_Home/Response_File/ses_base_install ]
# then 
# 	echo " SES Base Install Response File is present"
# else
# 	echo " SES Base Install Response File is not present."
# echo "Hi Team ,

# SES Base Install Response File is Not Present in Server: $(hostname).


# Regards,
# Siebel Admin Team" | mailx -s "Software_Verification |SES_Base_Response File | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
#     exit 1
# fi

# if [ -f $Siebel_Dumps_Home/Response_File/ai_patch_install ]
# then 
# 	echo " AI Patch Install Response File is present"
# else
# 	echo " AI Patch Install Response File is not present."
# echo "Hi Team ,

# AI Patch Install Response File is Not Present in Server: $(hostname).


# Regards,
# Siebel Admin Team" | mailx -s "Software_Verification |AI_Patch_Response File | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
#     exit 1
# fi

# if [ -f $Siebel_Dumps_Home/Response_File/ses_patch_install.rsp ]
# then 
# 	echo " SES Patch Install Response File is present"
# else
# 	echo " SES Patch Install Response File is not present."
# echo "Hi Team ,

# SES Patch Install Response File is Not Present in Server: $(hostname).


# Regards,
# Siebel Admin Team" | mailx -s "Software_Verification |SES_Patch_Response File | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
#     exit 1
# fi

if [ -d $Siebel_Dumps_Home/Siebel_Base_Version/Siebel_Install_Image_PS17_0_Linux/17.0.0.0/Linux/Server/Siebel_Enterprise_Server/Disk1/install ]
then 
	echo " Siebel_base_Version is present"
else
	echo " Siebel_base_Version is not present."
echo "Hi Team ,

Siebel Base Version is not Present in Server: $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Software_Verification | Siebel_Base_Version | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi

if [ -d $Siebel_Dumps_Home/patch_set/Siebel_Latest_Patch/Siebel_Install_Image/Linux/Server/Siebel_Enterprise_Server/Disk1/install ]
then 
	echo " Patch_Set is present"
else
	echo " Patch_Set is not present."
echo "Hi Team ,

Patch Set Version is not Present in Server: $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Software_Verification | Patch_Set | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi


echo "Hi Team ,

Software Verification Done Successfully in Server: $(hostname).


Regards,
Siebel Admin Team" 
#| mailx -s "Software_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
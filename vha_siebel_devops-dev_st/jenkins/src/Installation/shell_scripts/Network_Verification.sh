Script_Base_Dir=$1
FROM_MAIL_ID=$2
TO_MAIL_ID=$3

. /vha/home/sbladm/.bash_profile
cd $Script_Base_Dir

./comm_check.sh comm_input.txt comm_output_$(hostname).txt

cd $Script_Base_Dir
if [[ "$(grep -o -E 'Not Working' $Script_Base_Dir/comm_output_$(hostname).txt)" ]]
then
echo "All comms are not working fine"
echo "Hi Team ,

Below Comms are not working:

`grep "Not Working" $Script_Base_Dir/comm_output.txt`


Regards,
Siebel Admin Team" | mailx -s "Comm_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
else
echo "All Comms are working fine"
fi

echo "Hi Team ,

Network Verification Done Successfully in Server: $(hostname).


Regards,
Siebel Admin Team" 
#| mailx -s "Network_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
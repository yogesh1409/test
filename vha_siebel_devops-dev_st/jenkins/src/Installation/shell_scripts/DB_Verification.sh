DATABASE_USER_NAME_sadmin=$1
DATABASE_PASSWORD_sadmin=$2
DATABASE_USER_NAME_siebel=$3
DATABASE_PASSWORD_siebel=$4
TNS_Name=$5
FROM_MAIL_ID=$6
TO_MAIL_ID=$7

. /vha/home/sbladm/.bash_profile
pwd
#. jenkins/src/Installation/shell_scripts/EnvironmentVariables.sh

if [ -f /vha/app/oracle/product/12.2.0/client_1/network/admin/tnsnames.ora ]
then 
	echo " Database TNS details are present "
else
	echo " Database TNS details are not present "
echo "Hi Team ,

Database TNS Details are Not Present in Server: $(hostname).


Regards,
Siebel Admin Team" | mailx -s "Database_Verification | TNS | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
    exit 1
fi

EXTRACT_SQL2="select * from dual"
sqlplus -s /nolog <<-EOF>> "/vha/app/shared/sbl/scripts/sql_check_sadmin_$(hostname).txt"
WHENEVER SQLERROR EXIT SQL.SQLCODE;
WHENEVER OSERROR EXIT 9;
connect ${DATABASE_USER_NAME_sadmin}/${DATABASE_PASSWORD_sadmin}@$TNS_Name
$EXTRACT_SQL2;
EOF

if [ "$?" -ne 0 ]
	then
		 echo "unable to connect to sql"
		  exit 1
	else
		  echo "Connected successfully with sadmin"
fi

# sqlplus $DATABASE_USER_NAME_sadmin/$DATABASE_PASSWORD_sadmin@$TNS_Name exit > /vha/app/shared/sbl/scripts/sql_check_sadmin_$(hostname).txt
# echo /vha/app/shared/sbl/scripts/sql_check_sadmin_$(hostname).txt
# if [[ "$(grep -o -E 'ERROR' /vha/app/shared/sbl/scripts/sql_check_sadmin_$(hostname).txt)" ]]
# then
# echo "unable to connect to sql"
# echo "Hi Team ,

# Database Verification has been failed for USER: $DATABASE_USER_NAME_sadmin in server $(hostname).


# Regards,
# Siebel Admin Team" | mailx -s "Database_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID

# else
# echo "Connected successfully with sadmin"
# fi


sqlplus -s /nolog <<-EOF>> "/vha/app/shared/sbl/scripts/sql_check_siebel_$(hostname).txt"
WHENEVER SQLERROR EXIT SQL.SQLCODE;
WHENEVER OSERROR EXIT 9;
connect ${DATABASE_USER_NAME_siebel}/${DATABASE_PASSWORD_siebel}@$TNS_Name
$EXTRACT_SQL2;
EOF

if [ "$?" -ne 0 ]
	then
		 echo "unable to connect to sql"
		  exit 1
	else
		  echo "Connected successfully with siebel"
fi

# sqlplus $DATABASE_USER_NAME_siebel/$DATABASE_PASSWORD_siebel@$TNS_Name exit > /vha/app/shared/sbl/scripts/sql_check_siebel_$(hostname).txt
# echo /vha/app/shared/sbl/scripts/sql_check_siebel_$(hostname).txt
# if [[ "$(grep -o -E 'ERROR' /vha/app/shared/sbl/scripts/sql_check_siebel_$(hostname).txt)" ]]
# then
# echo "unable to connect to sql"
# echo "Hi Team ,

# Database Verification has been failed for USER: $DATABASE_USER_NAME_siebel in server $(hostname).


# Regards,
# Siebel Admin Team" | mailx -s "Database_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID

# else
# echo "Connected successfully with siebel"
# fi


echo "Hi Team ,

Database Verification Done Successfully in Server: $(hostname).


Regards,
Siebel Admin Team" 
#| mailx -s "Database_Verification | $(hostname)" -r $FROM_MAIL_ID $TO_MAIL_ID
filename=/vha/home/sbladm/.bash_profile
search_String=ORACLE_HOME
result=$(cat $filename|grep $search_String|wc -l)

echo "$result"

if [ $result -eq 0 ]; then
echo 'oracle home not present'
echo 'updating to bash profile'
#cat envinfo_setup.dat >> $filename

echo 'Setting Environment Variables'
cat >> $filename  <<- EOM
export ORACLE_HOME=/vha/app/oracle/product/12.2.0/client_1
LD_LIBRARY_PATH=/vha/app/oracle/product/12.2.0/client_1/lib
export LD_LIBRARY_PATH

PATH=/usr/local/bin:/usr/sbin:\${ORACLE_HOME}/bin:\$PATH:\$LD_LIBRARY_PATH:/vha/app/jdk1.8.0_191/bin;export PATH
JAVA_HOME=/vha/app/jdk1.8.0_191;export JAVA_HOME
export RESOLV_MULTI=off;

alias sbl='cd /vha/app/siebel/ses/siebsrvr/; pwd'
alias ai='cd /vha/app/siebel/ai/applicationcontainer/bin; pwd'
alias ses='cd /vha/app/siebel/ses/applicationcontainer/bin ;pwd'
alias fs='cd /vha/app/shared/sbl/filesystem ; pwd'
alias log='cd /vha/app/siebel/ses/siebsrvr/enterprises/SBLSIT10/SBLSRVR01/log ; pwd'
alias uf='cd /vha/app/shared/sbl/filesystem/userpref ; pwd'

alias a='ls -lrt'


#alias srvr='srvrmgr -g vvsl50938:2320 -e SBL_SIT10 -s vvsl50938 -u sadmin -p SADMIN'

ulimit -s 10240
#export DISPLAY=localhost:10.0
EOM

else
echo 'oracle home present'
#cat envinfo_setup.dat >> $filename
fi


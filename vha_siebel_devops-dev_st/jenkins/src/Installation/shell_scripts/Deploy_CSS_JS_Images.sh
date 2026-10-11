echo "Deploying CSS Files"
cd /vha/app/shared/sbl/siebel_dumps/Non_Repository/CSS
cp * /vha/app/siebel/ai/applicationcontainer/webapps/siebel/files/custom
cp theme-aurora.css login.css /vha/app/siebel/ai/applicationcontainer/webapps/siebel/files
echo "Deploying JS Files"
cd /vha/app/shared/sbl/siebel_dumps/Non_Repository/JS
cp * /vha/app/siebel/ai/applicationcontainer/webapps/siebel/scripts/siebel/custom
echo "Deploying Image Files"
cd /vha/app/shared/sbl/siebel_dumps/Non_Repository/Images
cp * /vha/app/siebel/ai/applicationcontainer/webapps/siebel/images/custom

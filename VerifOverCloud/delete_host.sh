OSTNAME=$1
if [ -z $1 ]
then
        echo "please host name check"
else
        curl -p 'http://118.130.73.120:8086/query?db=telegraf' --data-urlencode "q=drop series where host='$HOSTNAME'"
fi

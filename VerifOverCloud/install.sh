MONITOR_SERVER="http://118.130.73.120:8086"

# Data Center Name(or Tag), EX) “us-east-1”
DATA_CENTER=$1
if [ -z $1 ]
then
        DATA_CENTER="default"
fi

#Rack Name(or Tag) = “1”, EX) “1a”
RACK=$2
if [ -z $2 ]
then
        RACK="1a"
fi

apt-get install sysstat -y

wget https://dl.influxdata.com/telegraf/releases/telegraf_1.4.3-1_amd64.deb
dpkg -i telegraf_1.4.3-1_amd64.deb

sed -i 's#http://localhost:8086#'$MONITOR_SERVER'#g' /etc/telegraf/telegraf.conf
sed -i 's/# dc = "us-east-1"/'dc=\""$DATA_CENTER"\"'/g' /etc/telegraf/telegraf.conf
sed -i 's/# rack = "1a"/'rack=\""$RACK"\"'/g' /etc/telegraf/telegraf.conf

systemctl restart telegraf

rm telegraf_1.4.3-1_amd64.deb

service telegraf status

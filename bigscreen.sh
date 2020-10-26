read -p "Step 1 数据下载" -n1

curl -L https://etherpad.opendev.org/p/PRC-OpenSource-Hackathon-11-ChangSha/export/txt -o ./statistic/input/data

cat ./statistic/input/data | head -10

read -p "Step 2 数据分析" -n1
hadoop fs -put ./statistic/input/data input
hadoop jar ./statistic/target/statistic-1.0-SNAPSHOT.jar org.hackathon.data.Statistic input output
hadoop fs -get output ./statistic/
tree ./statistic/output

read -p "Step 3 后端" -n1
sudo apt update
sudo apt install -y python3 git python3-pip
pip3 install flask
cd ./bigscreen
python3 export.py
python3 -m http.server 5000

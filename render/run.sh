set -x

rm pic.jpg
hadoop jar render.jar renderer.main.Main -Dmapreduce.job.queuename=test
sleep 3
hdfs dfs -get pic.jpg .
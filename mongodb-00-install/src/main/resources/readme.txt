（在新机上！！！）
1.安装mongodb服务
config不要复制集init
mongos不要启动

====================================================================

（在老机上！！！）
2.改配置文件（运行start.bat，选择目录后点第2步）
config的配置文件的bindIp改成0.0.0.0
mongos的配置文件的bindIp改成0.0.0.0
shard1-10的配置文件的bindIp改成0.0.0.0

3.重启服务
net stop MongoDBMongos
net stop MongoDBShard1
net stop MongoDBShard2
net stop MongoDBShard3
net stop MongoDBShard4
net stop MongoDBShard5
net stop MongoDBShard6
net stop MongoDBShard7
net stop MongoDBShard8
net stop MongoDBShard9
net stop MongoDBShard10
net stop MongoDBConfig

net start MongoDBConfig
net start MongoDBShard1
net start MongoDBShard2
net start MongoDBShard3
net start MongoDBShard4
net start MongoDBShard5
net start MongoDBShard6
net start MongoDBShard7
net start MongoDBShard8
net start MongoDBShard9
net start MongoDBShard10
net start MongoDBMongos

4.重新配置config复制集
${oldMongodbHome}/bin/mongo --port 27009
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27009"
rs.reconfig(cfg)
exit

5.重新配置shard复制集
${oldMongodbHome}/bin/mongo --port 27011
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27011"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27012
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27012"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27013
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27013"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27014
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27014"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27015
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27015"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27016
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27016"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27017
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27017"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27018
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27018"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27019
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27019"
rs.reconfig(cfg)
exit

${oldMongodbHome}/bin/mongo --port 27020
cfg = rs.conf()
cfg.members[0].host = "${oldIp}:27020"
rs.reconfig(cfg)
exit

6.改配置文件（运行start.bat，选择目录后点第6步）
mongos的配置文件的config的ip改成老机ip

7.重启服务
net stop MongoDBMongos
net stop MongoDBShard1
net stop MongoDBShard2
net stop MongoDBShard3
net stop MongoDBShard4
net stop MongoDBShard5
net stop MongoDBShard6
net stop MongoDBShard7
net stop MongoDBShard8
net stop MongoDBShard9
net stop MongoDBShard10
net stop MongoDBConfig

net start MongoDBConfig
net start MongoDBShard1
net start MongoDBShard2
net start MongoDBShard3
net start MongoDBShard4
net start MongoDBShard5
net start MongoDBShard6
net start MongoDBShard7
net start MongoDBShard8
net start MongoDBShard9
net start MongoDBShard10
net start MongoDBMongos

8.添加新机分片
${oldMongodbHome}/bin/mongo --port 27010
sh.addShard("shardReplicaSet11/${newIp}:27011")
sh.addShard("shardReplicaSet12/${newIp}:27012")
sh.addShard("shardReplicaSet13/${newIp}:27013")
sh.addShard("shardReplicaSet14/${newIp}:27014")
sh.addShard("shardReplicaSet15/${newIp}:27015")
sh.addShard("shardReplicaSet16/${newIp}:27016")
sh.addShard("shardReplicaSet17/${newIp}:27017")
sh.addShard("shardReplicaSet18/${newIp}:27018")
sh.addShard("shardReplicaSet19/${newIp}:27019")
sh.addShard("shardReplicaSet20/${newIp}:27020")
sh.addShard("shardReplicaSet21/${newIp}:27021")
sh.addShard("shardReplicaSet22/${newIp}:27022")
sh.addShard("shardReplicaSet23/${newIp}:27023")
sh.addShard("shardReplicaSet24/${newIp}:27024")
sh.addShard("shardReplicaSet25/${newIp}:27025")
sh.addShard("shardReplicaSet26/${newIp}:27026")
sh.addShard("shardReplicaSet27/${newIp}:27027")
sh.addShard("shardReplicaSet28/${newIp}:27028")
sh.addShard("shardReplicaSet29/${newIp}:27029")
sh.addShard("shardReplicaSet30/${newIp}:27030")
sh.addShard("shardReplicaSet31/${newIp}:27031")
sh.addShard("shardReplicaSet32/${newIp}:27032")
sh.addShard("shardReplicaSet33/${newIp}:27033")
sh.addShard("shardReplicaSet34/${newIp}:27034")
sh.addShard("shardReplicaSet35/${newIp}:27035")
sh.addShard("shardReplicaSet36/${newIp}:27036")
sh.addShard("shardReplicaSet37/${newIp}:27037")
sh.addShard("shardReplicaSet38/${newIp}:27038")
sh.addShard("shardReplicaSet39/${newIp}:27039")
sh.addShard("shardReplicaSet40/${newIp}:27040")
sh.addShard("shardReplicaSet41/${newIp}:27041")
sh.addShard("shardReplicaSet42/${newIp}:27042")
sh.addShard("shardReplicaSet43/${newIp}:27043")
sh.addShard("shardReplicaSet44/${newIp}:27044")
sh.addShard("shardReplicaSet45/${newIp}:27045")
sh.addShard("shardReplicaSet46/${newIp}:27046")
sh.addShard("shardReplicaSet47/${newIp}:27047")
sh.addShard("shardReplicaSet48/${newIp}:27048")
sh.addShard("shardReplicaSet49/${newIp}:27049")
sh.addShard("shardReplicaSet50/${newIp}:27050")

9.设置主分片
use admin
db.runCommand({"movePrimary": "zd_pd_data_middle_platform", "to": "shardReplicaSet11"})
db.runCommand({"flushRouterConfig": 1})

10.删除分片1-10
db.runCommand({"removeShard": "shardReplicaSet1"})
db.runCommand({"removeShard": "shardReplicaSet2"})
db.runCommand({"removeShard": "shardReplicaSet3"})
db.runCommand({"removeShard": "shardReplicaSet4"})
db.runCommand({"removeShard": "shardReplicaSet5"})
db.runCommand({"removeShard": "shardReplicaSet6"})
db.runCommand({"removeShard": "shardReplicaSet7"})
db.runCommand({"removeShard": "shardReplicaSet8"})
db.runCommand({"removeShard": "shardReplicaSet9"})
db.runCommand({"removeShard": "shardReplicaSet10"})
exit

11.添加新机config，本机config降级
${oldMongodbHome}/bin/mongo --port 27009
rs.add("${newIp}:26999")
rs.stepDown()
exit

====================================================================

（在新机上！！！）
13.启动MongoDBMongos服务

####################################################################
####################################################################
####################################################################
###
###   以下等数据迁移完后再执行！！！
###   以下等数据迁移完后再执行！！！
###   以下等数据迁移完后再执行！！！
###
####################################################################
####################################################################
####################################################################

（在新机上！！！）
14.删除分片1-10
${newMongodbHome}/bin/mongo --port 27000
use admin
db.runCommand({"removeShard": "shardReplicaSet1"})
db.runCommand({"removeShard": "shardReplicaSet2"})
db.runCommand({"removeShard": "shardReplicaSet3"})
db.runCommand({"removeShard": "shardReplicaSet4"})
db.runCommand({"removeShard": "shardReplicaSet5"})
db.runCommand({"removeShard": "shardReplicaSet6"})
db.runCommand({"removeShard": "shardReplicaSet7"})
db.runCommand({"removeShard": "shardReplicaSet8"})
db.runCommand({"removeShard": "shardReplicaSet9"})
db.runCommand({"removeShard": "shardReplicaSet10"})
exit

15.删除老机config
${newMongodbHome}/bin/mongo --port 26999
rs.remove("${oldIp}:27009")
exit

====================================================================

（在老机上！！！）
16.停止并删除所有mongodb服务
net stop MongoDBMongos
net stop MongoDBShard1
net stop MongoDBShard2
net stop MongoDBShard3
net stop MongoDBShard4
net stop MongoDBShard5
net stop MongoDBShard6
net stop MongoDBShard7
net stop MongoDBShard8
net stop MongoDBShard9
net stop MongoDBShard10
net stop MongoDBConfig

sc delete MongoDBMongos
sc delete MongoDBShard1
sc delete MongoDBShard2
sc delete MongoDBShard3
sc delete MongoDBShard4
sc delete MongoDBShard5
sc delete MongoDBShard6
sc delete MongoDBShard7
sc delete MongoDBShard8
sc delete MongoDBShard9
sc delete MongoDBShard10
sc delete MongoDBConfig

17.删除mongodb的两个目录以释放硬盘空间
D:/new/sjzt/mongodb-cluster
D:/new/sjzt/mongodb-win32-x86_64-2012plus-4.2.7

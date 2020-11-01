package com.test.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MongodbParam {

	String mongodbCluster;
	String mongodbHome;

	String configBindIp;
	String configIp;
	int configPort;

	String mongosBindIp;
	String mongosIp;
	int mongosPort;

	String shardBindIp;
	String shardIp;
	int beginShardPort;

	int beginShardNumber;
	int endShardNumber;

}

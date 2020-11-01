package com.test.domain;

import com.mongodb.client.MongoDatabase;

public interface MongoDatabaseCallback<T> {

	T doWithMongoDatabase(MongoDatabase mongoDatabase);

}

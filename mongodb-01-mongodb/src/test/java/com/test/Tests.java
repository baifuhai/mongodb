package com.test;

import com.test.entity.Person;
import com.test.util.MongoDBUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class Tests {

	private MongoClient mongoClient;
	private MongoDatabase mongoDatabase;
	private MongoCollection<Document> mongoCollection;

	@Before
	public void init() {
		String host = "127.0.0.1";
		int port = 27017;
		String databaseName = "test";
		String collectionName = "person";
		
		mongoClient = MongoDBUtil.getMongoClient(host, port);
		mongoDatabase = mongoClient.getDatabase(databaseName);
		mongoCollection = mongoDatabase.getCollection(collectionName);
	}

	@After
	public void destroy() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	// 新增
	@Test
	public void testInsert() {
		for (int i = 1; i <= 4; i++) {
			Document interests = new Document();
			interests.put("game", "game" + i);
			interests.put("ball", "ball" + i);
			
			Document doc = new Document();
			doc.put("name", "bfh");
			doc.put("age", 20 + i);
			doc.put("interests", interests);
			
			mongoCollection.insertOne(doc);
		}
	}
	
	// 删除database
	@Test
	public void testDropDatabase() {
		mongoDatabase.drop();
	}

	// 删除collection
	@Test
	public void testDropCollection() {
		mongoCollection.drop();
	}
	
	// 查询所有database名称
	@Test
	public void testGetAllDatabaseNames() {
		MongoIterable<String> iter = mongoClient.listDatabaseNames();
		for (String s : iter) {
			System.out.println(s);
		}
	}

	// 查询某database下所有collection名称
	@Test
	public void testGetAllCollectionNames() {
		MongoIterable<String> iter = mongoDatabase.listCollectionNames();
		for (String s : iter) {
			System.out.println(s);
		}
	}
	
	// 根据id查询
	@Test
	public void testFindById() {
		String id = "1";
		ObjectId _id = new ObjectId(id);
		Bson filter = Filters.eq("_id", _id);
		Document first = mongoCollection.find(filter).first();
		System.out.println(first.toJson());
	}

	// 查询
	@Test
	public void testFind() {
		int pageNo = 1;
		int pageSize = 10;
		
		FindIterable<Document> iter = mongoCollection
//				.find()
				.find(Filters.eq("name", "bfh"))
//				.find(Filters.and(Filters.eq("name", "bfh"), Filters.lt("age", 24)))
//				.find(Filters.elemMatch("interests", Filters.eq("game", "game1")))
//				.find(new Document("$or", Arrays.asList(new Document("owner", "tom"), new Document("words", new Document("$gt", 350)))))
				.projection(Projections.fields(Projections.include("name", "age", "interests"), Projections.excludeId()))
//				.projection(Projections.fields(Projections.elemMatch("affInfo"), Projections.excludeId()))
//				.projection(Projections.include("affInfo", "ssid"))
				.sort(Sorts.descending("age"))
//				.sort(new Document("id", 1))
//				.sort(new BasicDBObject("_id", 1))
				.skip((pageNo - 1) * pageSize)
				.limit(pageSize);
		for (Document doc : iter) {
			System.out.println(doc.toJson());
		}

		FindIterable<Person> iter2 = mongoCollection.find(Filters.and(Filters.eq("name", "bfh"), Filters.lt("age", 24)), Person.class);
		for (Person p : iter2) {
			System.out.println(p);
		}
	}
	
	// 统计数
	@Test
	public void testCount() {
		long count = mongoCollection.countDocuments();
		System.out.println(count);
		
		long count2 = mongoCollection.countDocuments(Filters.elemMatch("affInfo", Filters.elemMatch("affiliationJGList", Filters.eq("sid", 0))));
		System.out.println(count2);
	}
	
	// 通过id更新
	@Test
	public void testUpdateById() {
		String id = "1";
		Document newdoc = new Document("age", 23);
		
		ObjectId _id = new ObjectId(id);
		Bson filter = Filters.eq("_id", _id);
		UpdateResult updateResult = mongoCollection.updateOne(filter, new Document("$set", newdoc));
		int count = (int) updateResult.getModifiedCount();
		System.out.println(count);
	}

	// 更新
	@Test
	public void testUpdate() {
		mongoCollection.replaceOne(Filters.eq("name", "bfh"), new Document("$set", new Document("age", 22))); // 完全替代一个
		
		mongoCollection.updateMany(Filters.eq("name", "bfh"), Updates.set("age", 21));// 修改字段值
		mongoCollection.updateMany(Filters.eq("name", "bfh"), new Document("$set", new Document("age", 22)));
		mongoCollection.updateMany(Filters.eq("name", "bfh"), new Document("$unset", new Document("age", "")));// 删除某个字段
		mongoCollection.updateMany(Filters.eq("name", "bfh"), new Document("$rename", new Document("age", "age2")));// 修改某个字段名
		mongoCollection.updateMany(Filters.eq("name", "bfh"), new Document("$rename", new Document("sid", "ssid")), new UpdateOptions().upsert(false));
	}
	
	// 通过id删除
	@Test
	public void testDeleteById() {
		String id = "";
		ObjectId _id = new ObjectId(id);
		Bson filter = Filters.eq("_id", _id);
		DeleteResult deleteResult = mongoCollection.deleteOne(filter);
		int count = (int) deleteResult.getDeletedCount();
		System.out.println(count);
	}
	
	// 索引
	@Test
	public void index() {
		mongoCollection.createIndex(new Document("validata", 1));// 创建索引
		mongoCollection.createIndex(new Document("id", 1));
		mongoCollection.createIndex(new Document("ut_wos", 1), new IndexOptions().unique(true));// 创建唯一索引
		
		mongoCollection.dropIndexes();// 删除所有索引
		mongoCollection.dropIndex("validata_1");// 根据索引名删除某个索引
		
		ListIndexesIterable<Document> iter = mongoCollection.listIndexes();// 查询所有索引
		for (Document document : iter) {
			System.out.println(document.toJson());
		}
	}

}

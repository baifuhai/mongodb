package com.test.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.CompositeTransactionAttributeSource;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MongoConfig {

	@Autowired
	MongoDbFactory factory;

	@Autowired
	MongoMappingContext context;

	@Autowired
	MongoCustomConversions conversions;

	@Bean
	public MappingMongoConverter mappingMongoConverter() {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
		mappingConverter.setCustomConversions(conversions);
		mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
		return mappingConverter;
	}

	@Bean
	public MongoTransactionManager mongoTransactionManager() {
		return new MongoTransactionManager(factory);
	}

	@Bean
	public TransactionInterceptor txAdviceMongo() {
		NameMatchTransactionAttributeSource txAttributeSource = new NameMatchTransactionAttributeSource();

		RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
		readOnlyTx.setPropagationBehavior(3);
		readOnlyTx.setReadOnly(true);

		RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
		requiredTx.setPropagationBehavior(0);
		requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));

		Map<String, TransactionAttribute> txAttributeMap = new HashMap<>();
		txAttributeMap.put("*", requiredTx);
		txAttributeMap.put("get*", readOnlyTx);
		txAttributeMap.put("find*", readOnlyTx);
		txAttributeMap.put("query*", readOnlyTx);
		txAttributeMap.put("count*", readOnlyTx);

		txAttributeSource.setNameMap(txAttributeMap);

		CompositeTransactionAttributeSource compositeTransactionAttributeSource = new CompositeTransactionAttributeSource(new AnnotationTransactionAttributeSource(), txAttributeSource);
		return new TransactionInterceptor(mongoTransactionManager(), compositeTransactionAttributeSource);
	}

	@Bean
	public Advisor txAdviceAdvisorMongo() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(* com.test.service.MeasureInfoService.*(..))");
		return new DefaultPointcutAdvisor(pointcut, txAdviceMongo());
	}

}

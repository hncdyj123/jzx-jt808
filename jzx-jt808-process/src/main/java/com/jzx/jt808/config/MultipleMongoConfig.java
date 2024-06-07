package com.jzx.jt808.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import com.mongodb.client.MongoClients;

/**
 * mongo多数据源config类
 * 
 * @author yangjie
 * @date 2023/2/8
 * @version 1.0.0
 */
@Configuration
public class MultipleMongoConfig {
    @Autowired
    private MultipleMongoProperties mongoProperties;

    private final static String CLAZZ_CONSTANTS = "_class";

    protected static final String PRIMARY_MONGO_TEMPLATE = "primaryMongoTemplate";

    protected static final String SECONDARY_MONGO_TEMPLATE = "secondaryMongoTemplate";

    @Primary
    @Bean(name = PRIMARY_MONGO_TEMPLATE)
    public MongoTemplate primaryMongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(primaryFactory(this.mongoProperties.getPrimary()));
        MongoConverter converter = mongoTemplate.getConverter();
        if (converter.getTypeMapper().isTypeKey(CLAZZ_CONSTANTS)) {
            ((MappingMongoConverter)converter).setTypeMapper(new DefaultMongoTypeMapper(null));
        }
        return mongoTemplate;
    }

    @Bean(name = SECONDARY_MONGO_TEMPLATE)
    public MongoTemplate secondaryMongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(secondaryFactory(this.mongoProperties.getSecondary()));
        MongoConverter converter = mongoTemplate.getConverter();
        if (converter.getTypeMapper().isTypeKey(CLAZZ_CONSTANTS)) {
            ((MappingMongoConverter)converter).setTypeMapper(new DefaultMongoTypeMapper(null));
        }
        return mongoTemplate;
    }

    public MongoDatabaseFactory primaryFactory(MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }

    public MongoDatabaseFactory secondaryFactory(MongoProperties mongo) throws Exception {
        return new SimpleMongoClientDatabaseFactory(MongoClients.create(mongo.getUri()), mongo.getDatabase());
    }
}

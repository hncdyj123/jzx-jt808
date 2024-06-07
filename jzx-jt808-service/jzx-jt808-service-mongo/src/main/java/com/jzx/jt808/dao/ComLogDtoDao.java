package com.jzx.jt808.dao;

import com.jzx.jt808.dao.base.BaseMongoDaoImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.jzx.jt808.entity.ComLogDto;

/**
 * 类描述：指令操作相关Dao
 *
 * @author yangjie
 * @date 2023-09-11 19:41
 **/
@Repository
public class ComLogDtoDao extends BaseMongoDaoImpl<ComLogDto> {

    public ComLogDtoDao(@Qualifier("secondaryMongoTemplate") MongoTemplate mongoTemplate) {
        super.setMongoTemplate(mongoTemplate);
    }
}

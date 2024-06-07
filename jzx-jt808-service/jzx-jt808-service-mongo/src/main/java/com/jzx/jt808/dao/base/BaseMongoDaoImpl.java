package com.jzx.jt808.dao.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.fastjson2.JSONObject;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义MongoDB操作接口抽象实现类
 *
 * @author 杨杰
 * @version 2022-3-30
 * @see BaseMongoDaoImpl
 * @since
 */
@Slf4j
public abstract class BaseMongoDaoImpl<T> implements BaseMongoDao<T> {
    protected Class<T> getEntityClass;
    @Setter
    @Getter
    private MongoTemplate mongoTemplate;

    @SuppressWarnings("unchecked")
    protected BaseMongoDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        Type trueType = ((ParameterizedType)type).getActualTypeArguments()[0];
        this.getEntityClass = (Class<T>)trueType;
    }

    @Override
    public <T> T insert(T t) {
        return this.insert(t, getEntityClass.getSimpleName());
    }

    @Override
    public <T> T insert(T t, String collectionName) {
        return mongoTemplate.insert(t, collectionName);
    }

    @Override
    public Collection<T> insertBatch(Collection<T> collections) {
        return this.insert(collections, getEntityClass.getSimpleName());
    }

    @Override
    public Collection<T> insertBatch(Collection<T> collections, String collectionName) {
        return mongoTemplate.insert(collections, collectionName);
    }

    @Override
    public DeleteResult remove(Serializable id) {
        return this.remove(id, getEntityClass.getSimpleName());
    }

    @Override
    public DeleteResult remove(Serializable id, String collectionName) {
        return mongoTemplate.remove(id, collectionName);
    }

    @Override
    public <T> T findAndRemove(Serializable id, Class<T> clazz) {
        return this.findAndRemove(id, clazz, getEntityClass.getSimpleName());
    }

    @Override
    public <T> T findAndRemove(Serializable id, Class<T> t, String collectionName) {
        Criteria criteria = new Criteria().and("_id").is(new ObjectId(String.valueOf(id)));
        return mongoTemplate.findAndRemove(new Query(criteria), t, collectionName);
    }

    @Override
    public void removeIds(Collection<Serializable> ids) {
        this.removeIds(ids, getEntityClass.getSimpleName());
    }

    @Override
    public void removeIds(Collection<Serializable> ids, String collectionName) {
        if (ids != null && ids.size() > 0) {
            for (Serializable id : ids) {
                mongoTemplate.remove(id, collectionName);
            }
        }
    }

    @Override
    public void removeByCriteriaMap(Map<String, Object> criteriaMap) {
        this.removeByCriteriaMap(criteriaMap, getEntityClass.getSimpleName());
    }

    @Override
    public void removeByCriteriaMap(Map<String, Object> criteriaMap, String collectionName) {
        mongoTemplate.remove(this.buildCriteria(criteriaMap), collectionName);
    }

    @Override
    public void removeByQuery(Query query) {
        mongoTemplate.remove(query, getEntityClass.getSimpleName());
    }

    @Override
    public void removeByQuery(Query query, String collectionName) {
        mongoTemplate.remove(query, collectionName);
    }

    @Override
    public void updateById(Serializable id, T t) {
        this.updateById(id, t, getEntityClass.getSimpleName());
    }

    @Override
    public void updateById(Serializable id, T t, String collectionName) {
        Criteria criteria = new Criteria().and("_id").is(new ObjectId(id.toString()));
        mongoTemplate.updateFirst(new Query(criteria), this.buildUpdate(t), getEntityClass, collectionName);
    }

    @Override
    public UpdateResult upsertByCriteriaMap(Map<String, Object> criteriaMap, T t) {
        return this.upsertByCriteriaMap(criteriaMap, t, getEntityClass.getSimpleName());
    }

    @Override
    public UpdateResult upsertByCriteriaMap(Map<String, Object> criteriaMap, T t, String collectionName) {
        Query query = new Query().addCriteria(this.buildCriteria(criteriaMap));
        return mongoTemplate.upsert(query, this.buildUpdate(t), collectionName);
    }

    @Override
    public UpdateResult updateByCriteriaMap(Map<String, Object> criteriaMap, T t) {
        return this.updateByCriteriaMap(criteriaMap, t, getEntityClass.getSimpleName(), false);
    }

    @Override
    public UpdateResult updateByCriteriaMap(Map<String, Object> criteriaMap, T t, String collectionName) {
        return this.updateByCriteriaMap(criteriaMap, t, collectionName, false);
    }

    @Override
    public UpdateResult updateByCriteriaMap(Map<String, Object> criteriaMap, T t, String collectionName,
        boolean hasAll) {
        Query query = new Query().addCriteria(this.buildCriteria(criteriaMap));
        UpdateResult result = null;
        if (!hasAll) {
            result = mongoTemplate.updateFirst(query, this.buildUpdate(t), getEntityClass, collectionName);
        } else {
            result = mongoTemplate.updateMulti(query, this.buildUpdate(t), getEntityClass, collectionName);
        }
        return result;
    }

    @Override
    public UpdateResult updateByQuery(Query query, Update update) {
        return this.updateByQuery(query, update, getEntityClass.getSimpleName(), false);
    }

    @Override
    public UpdateResult updateByQuery(Query query, Update update, String collectionName) {
        return this.updateByQuery(query, update, collectionName, false);
    }

    @Override
    public UpdateResult updateByQuery(Query query, Update update, String collectionName, boolean hasAll) {
        UpdateResult result = null;
        if (!hasAll) {
            result = mongoTemplate.updateFirst(query, update, getEntityClass, collectionName);
        } else {
            result = mongoTemplate.updateMulti(query, update, getEntityClass, collectionName);
        }
        return result;
    }

    @Override
    public T findOne(Map<String, Object> criteriaMap) {
        return this.findOne(criteriaMap, getEntityClass.getSimpleName());
    }

    @Override
    public T findOne(Map<String, Object> criteriaMap, String collectionName) {
        return this.findOneAndSort(criteriaMap, collectionName, null);
    }

    @Override
    public T findOneAndSort(Map<String, Object> criteriaMap, String collectionName, Sort sort) {
        Query query = new Query().addCriteria(this.buildCriteria(criteriaMap));
        if (sort != null) {
            query.with(sort);
        }
        return mongoTemplate.findOne(query, getEntityClass, collectionName);
    }

    @Override
    public T find(Serializable id) {
        return this.find(id, getEntityClass.getSimpleName());
    }

    @Override
    public T find(Serializable id, String collectionName) {
        Criteria criteria = new Criteria().and("_id").is(new ObjectId(id.toString()));;
        return mongoTemplate.findOne(new Query(criteria), getEntityClass, collectionName);
    }

    @Override
    public List<T> findAll() {
        return this.findAll(getEntityClass.getSimpleName());
    }

    @Override
    public List<T> findAll(String collectionName) {
        return mongoTemplate.findAll(getEntityClass, collectionName);
    }

    @Override
    public List<T> findAll(Map<String, Object> criteriaMap, String collectionName) {
        Query query = new Query().addCriteria(this.buildCriteria(criteriaMap));
        return mongoTemplate.find(query, getEntityClass, collectionName);
    }

    @Override
    public long count() {
        return this.count(getEntityClass.getSimpleName());
    }

    @Override
    public long count(String collectionName) {
        return mongoTemplate.count(null, collectionName);
    }

    @Override
    public long countByCriteriaMap(Map<String, Object> criteriaMap, String collectionName) {
        Query query = new Query().addCriteria(this.buildCriteria(criteriaMap));
        return mongoTemplate.count(query, collectionName);
    }

    @Override
    public long countByQuery(Query query) {
        return this.countByQuery(query, getEntityClass.getSimpleName());
    }

    @Override
    public long countByQuery(Query query, String collectionName) {
        return mongoTemplate.count(query, collectionName);
    }

    @Override
    public T findByQuery(Query query) {
        return this.findByQuery(query, getEntityClass.getSimpleName());
    }

    @Override
    public T findByQuery(Query query, String collectionName) {
        return mongoTemplate.findOne(query, getEntityClass, collectionName);
    }

    @Override
    public List<T> findAllByQuery(Query query) {
        return this.findAllByQuery(query, getEntityClass.getSimpleName());
    }

    @Override
    public List<T> findAllByQuery(Query query, String collectionName) {
        return mongoTemplate.find(query, getEntityClass, collectionName);
    }

    @Override
    public void createIndex(List<Index> indexList) {
        this.createIndex(getEntityClass.getSimpleName(), indexList);
    }

    @Override
    public void createIndex(String collectionName, List<Index> indexList) {
        for (Index index : indexList) {
            mongoTemplate.indexOps(collectionName).ensureIndex(index);
        }
    }

    /**
     * 构建查询条件
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @return {@link Criteria}
     */
    public Criteria buildCriteria(Map<String, Object> criteriaMap) {
        Criteria criteria = new Criteria();
        for (Map.Entry<String, Object> entry : criteriaMap.entrySet()) {
            if (StrUtil.equals("_id", entry.getKey())) {
                criteria.and(entry.getKey()).is(new ObjectId((String)entry.getValue()));
                continue;
            }
            criteria.and(entry.getKey()).is(entry.getValue());
        }
        return criteria;
    }

    /**
     * 构建修改对象<br>
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param t 需要修改的对象,可以是JSONObject,Map,对象
     * @return {@link Update}
     */
    public Update buildUpdate(T t) {
        Update update = new Update();
        if (t instanceof JSONObject) {
            Map<String, Object> objectMap = (LinkedHashMap)t;
            objectMap.forEach((k, v) -> {
                update.set(k, v);
            });
        } else if (t instanceof Map) {
            ((Map<String, ?>)t).forEach((k, v) -> {
                update.set(k, v);
            });
        } else {
            Field[] fields = t.getClass().getDeclaredFields();
            Stream.of(fields).forEach(field -> {
                field.setAccessible(true);
                Object val = null;
                try {
                    val = field.get(t);
                } catch (IllegalArgumentException e) {
                    e.getMessage();
                } catch (IllegalAccessException e) {
                    e.getMessage();
                }
                String key = field.getName();
                if (val != null && !"id".equals(key)) {
                    update.set(key, val);
                }
            });
        }
        return update;
    }
}

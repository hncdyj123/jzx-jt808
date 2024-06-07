package com.jzx.jt808.dao.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

/**
 * 自定义MongoDB操作接口类
 * 
 * @author yangjie
 * @date 2023/12/28
 * @version 1.0.0
 */
public interface BaseMongoDao<T> {
    /**
     * 新增数据
     * 
     * @author yangjie
     * @date 2023/12/27
     * @param t
     * @return {@link T}
     */
    public <T> T insert(T t);

    /**
     * 新增数据
     * 
     * @author yangjie
     * @date 2023/12/27
     * @param t
     * @param collectionName
     * @return {@link T}
     */
    public <T> T insert(T t, String collectionName);

    /**
     * 批量新增数据
     * 
     * @author yangjie
     * @date 2023/12/27
     * @param collections
     * @return
     */
    public Collection<T> insertBatch(Collection<T> collections);

    /**
     * 批量新增数据
     * 
     * @author yangjie
     * @date 2023/12/27
     * @param collections
     * @param collectionName
     * @return
     */
    public Collection<T> insertBatch(Collection<T> collections, String collectionName);

    /**
     * 根据ID删除
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param id
     * @return {@link DeleteResult}
     */
    public DeleteResult remove(Serializable id);

    /**
     * 根据ID删除
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param id
     * @param collectionName
     * @return {@link DeleteResult}
     */
    public DeleteResult remove(Serializable id, String collectionName);

    /**
     * 根据ID删除并返回对象
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param id
     * @return {@link T}
     */
    public <T> T findAndRemove(Serializable id, Class<T> clazz);

    /**
     * 根据ID删除并返回对象
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param id
     * @param collectionName
     * @return {@link T}
     */
    public <T> T findAndRemove(Serializable id, Class<T> clazz, String collectionName);

    /**
     * 根据ID集合删除
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param ids
     * @return
     */
    public void removeIds(Collection<Serializable> ids);

    /**
     * 根据ID集合删除
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param ids
     * @param collectionName
     * @return
     */
    public void removeIds(Collection<Serializable> ids, String collectionName);

    /**
     * 根据Map中条件删除
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @return
     */
    public void removeByCriteriaMap(Map<String, Object> criteriaMap);

    /**
     * 根据Map中条件删除
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param collectionName
     * @return
     */
    public void removeByCriteriaMap(Map<String, Object> criteriaMap, String collectionName);

    /**
     * 根据条件删除
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @return
     */
    public void removeByQuery(Query query);

    /**
     * 根据条件删除
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @param collectionNam
     * @return
     */
    public void removeByQuery(Query query, String collectionName);

    /**
     * 根据ID修改对象
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param id
     * @param t
     * @return
     */
    public void updateById(Serializable id, T t);

    /**
     * 根据ID修改对象
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param id
     * @param t
     * @param collectionName
     * @return
     */
    public void updateById(Serializable id, T t, String collectionName);

    /**
     * 根据Map条件插入或修改<br/>
     * 存在更新,不存在插入<br/>
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param t
     * @return
     */
    public UpdateResult upsertByCriteriaMap(Map<String, Object> criteriaMap, T t);

    /**
     * 根据Map条件插入或修改<br/>
     * 存在更新,不存在插入<br/>
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param t
     * @param collectionName
     * @return
     */
    public UpdateResult upsertByCriteriaMap(Map<String, Object> criteriaMap, T t, String collectionName);

    /**
     * 根据Map条件修改
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param t
     * @return
     */
    public UpdateResult updateByCriteriaMap(Map<String, Object> criteriaMap, T t);

    /**
     * 根据Map条件修改
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param t
     * @param collectionName
     * @return
     */
    public UpdateResult updateByCriteriaMap(Map<String, Object> criteriaMap, T t, String collectionName);

    /**
     * 根据Map条件修改
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param t
     * @param collectionName
     * @param hasAll
     * @return
     */
    public UpdateResult updateByCriteriaMap(Map<String, Object> criteriaMap, T t, String collectionName,
        boolean hasAll);

    /**
     * 根据条件修改
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @param update
     * @return {@link UpdateResult}
     */
    public UpdateResult updateByQuery(Query query, Update update);

    /**
     * 根据条件修改
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @param update
     * @param collectionName
     * @return {@link UpdateResult}
     */
    public UpdateResult updateByQuery(Query query, Update update, String collectionName);

    /**
     * 根据条件修改
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @param update
     * @param collectionName
     * @param hasAll
     * @return {@link UpdateResult}
     */
    public UpdateResult updateByQuery(Query query, Update update, String collectionName, boolean hasAll);

    /**
     * 查询一条满足条件记录
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @return {@link T}
     */
    public T findOne(Map<String, Object> criteriaMap);

    /**
     * 查询一条满足条件记录
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param collectionName
     * @return {@link T}
     */
    public T findOne(Map<String, Object> criteriaMap, String collectionName);

    /**
     * 查询一条满足条件记录并且排序
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param collectionName
     * @param sort
     * @return {@link T}
     */
    public T findOneAndSort(Map<String, Object> criteriaMap, String collectionName, Sort sort);

    /**
     * 根据ID查询
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param id
     * @return {@link T}
     */
    public T find(Serializable id);

    /**
     * 根据ID查询
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param id
     * @param collectionName
     * @return {@link T}
     */
    public T find(Serializable id, String collectionName);

    /**
     * 查询所有
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param
     * @return {@link List<T>}
     */
    public List<T> findAll();

    /**
     * 查询所有
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param collectionName
     * @return {@link List<T>}
     */
    public List<T> findAll(String collectionName);

    /**
     * 根据条件查询所有
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param collectionName
     * @return {@link List<T>}
     */
    public List<T> findAll(Map<String, Object> criteriaMap, String collectionName);

    /**
     * 查询count
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param
     * @return {@link long}
     */
    public long count();

    /**
     * 查询count
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param collectionName
     * @return {@link long}
     */
    public long count(String collectionName);

    /**
     * 根据条件查询Count
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param criteriaMap
     * @param collectionName
     * @return {@link long}
     */
    public long countByCriteriaMap(Map<String, Object> criteriaMap, String collectionName);

    /**
     * 根据条件查询Count
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @return {@link long}
     */
    public long countByQuery(Query query);

    /**
     * 根据条件查询Count
     *
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @return {@link long}
     */
    public long countByQuery(Query query, String collectionName);

    /**
     * 根据条件查询
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @return {@link T}
     */
    public T findByQuery(Query query);

    /**
     * 根据条件查询
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @return {@link T}
     */
    public T findByQuery(Query query, String collectionName);

    /**
     * 根据条件查询
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @return {@link T}
     */
    public List<T> findAllByQuery(Query query);

    /**
     * 根据条件查询
     * 
     * @author yangjie
     * @date 2023/12/28
     * @param query
     * @return {@link T}
     */
    public List<T> findAllByQuery(Query query, String collectionName);

    /**
     * 创建索引
     * 
     * @author yangjie
     * @date 2024/1/2
     * @param indexList
     * @return
     */
    public void createIndex(List<Index> indexList);

    /**
     * 创建索引
     * 
     * @author yangjie
     * @date 2024/1/2
     * @param collectionName
     * @param indexList
     * @return
     */
    public void createIndex(String collectionName, List<Index> indexList);
}

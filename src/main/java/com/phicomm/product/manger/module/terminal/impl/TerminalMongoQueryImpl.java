package com.phicomm.product.manger.module.terminal.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.phicomm.product.manger.model.terminal.TerminalCommonEntity;
import com.phicomm.product.manger.module.terminal.MongoQueryFactory;
import com.phicomm.product.manger.utils.MongoDbUtil;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 设备信息查询
 *
 * @author wei.yang on 2017/12/28
 */
@Component
public class TerminalMongoQueryImpl implements MongoQueryFactory {

    private static final Logger logger = Logger.getLogger(TerminalMongoQueryImpl.class);

    private static final String GROUP_PLATFORM_KEY = "equipmentTerminalInfo.systemInfo.platform";

    private static final String COLLECTION_NAME = "equipment_terminal_detail_trace";

    private static final String APP_ID = "equipmentTerminalInfo.appInfo.appId";

    private static final String GROUP_DATE_TIME_KEY = "createDate";

    private MongoTemplate mongoTemplate;

    @Autowired
    public TerminalMongoQueryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        Assert.notNull(this.mongoTemplate);
    }

    /**
     * 聚合查询，截止昨天
     *
     * @param keys 查询条件
     * @return 数据集合
     */
    @Override
    public GroupByResults<BasicDBObject> groupQuery(String... keys) {
        Criteria criteria = Criteria.where(GROUP_DATE_TIME_KEY).lte(obtainYesterday());
        GroupBy groupBy = GroupBy
                .key(keys)
                .initialDocument("{ count: 0 }")
                .reduceFunction("function(doc, prev) { prev.count += 1 }");
        return mongoTemplate.group(criteria, COLLECTION_NAME, groupBy, BasicDBObject.class);
    }

    /**
     * 分组
     *
     * @param key key
     * @return 格式化好的数据
     */
    @Override
    public List<TerminalCommonEntity> historyKeyGroup(String key) throws ParseException {
        MongoCollection<Document> collection = mongoTemplate.getCollection(COLLECTION_NAME);
        Document time = MongoDbUtil.timeFormat("%Y-%m-%d", "timestamp");
        Document match = new Document("timestamp", new Document("$lte", obtainMidNightTimestamp()));
        Document project = new Document("createTime", time)
                .append("appId", String.format("$%s", APP_ID))
                .append("platform", String.format("$%s", GROUP_PLATFORM_KEY))
                .append("compareObject", String.format("$%s", key));
        Document group = new Document("_id", new Document("platform", "$platform")
                .append("appId", "$appId")
                .append("createTime", "$createTime")
                .append("compareObject", "$compareObject"))
                .append("count", new Document("$sum", 1));
        AggregateIterable<Document> result = collection
                .aggregate(Arrays.asList(new Document("$match", match), new Document("$project", project),
                        new Document("$group", group)));
        return parseDocData(result);
    }

    /**
     * 聚合查询:只聚合昨天的数据
     *
     * @param keys 查询的条件
     * @return 聚合昨天的数据
     */
    @Override
    public GroupByResults<BasicDBObject> groupYesterdayData(String... keys) {
        Criteria criteria = Criteria.where(GROUP_DATE_TIME_KEY).is(obtainYesterday());
        GroupBy groupBy = GroupBy
                .key(keys)
                .initialDocument("{ count: 0 }")
                .reduceFunction("function(doc, prev) { prev.count += 1 }");
        return mongoTemplate.group(criteria, COLLECTION_NAME, groupBy, BasicDBObject.class);
    }

    /**
     * 历史查询
     *
     * @return map类型数据
     */
    public List<TerminalCommonEntity> historyGroup(String compareObjectKey) {
        GroupByResults<BasicDBObject> results = groupQuery(GROUP_PLATFORM_KEY, compareObjectKey, GROUP_DATE_TIME_KEY, APP_ID);
        return parseData(results, compareObjectKey);
    }

    /**
     * 获取昨天的数据
     *
     * @return map类型数据
     */
    public List<TerminalCommonEntity> yesterdayGroup(String compareObjectKey) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(COLLECTION_NAME);
        Document match = new Document(GROUP_DATE_TIME_KEY, new Document("$eq", obtainYesterday()));
        Document project = new Document("createTime", String.format("$%s", GROUP_DATE_TIME_KEY))
                .append("appId", String.format("$%s", APP_ID))
                .append("platform", String.format("$%s", GROUP_PLATFORM_KEY))
                .append("compareObject", String.format("$%s", compareObjectKey));
        Document group = new Document("_id", new Document("platform", "$platform")
                .append("appId", "$appId")
                .append("createTime", "$createTime")
                .append("compareObject", "$compareObject"))
                .append("count", new Document("$sum", 1));
        AggregateIterable<Document> result = collection
                .aggregate(Arrays.asList(new Document("$match", match), new Document("$project", project),
                        new Document("$group", group)));
        return parseDocData(result);
    }

    /**
     * 数据解析、格式化
     * while循环不能简化，格式转不成BasicDBObject，会报错
     *
     * @param results          查询结果
     * @param compareObjectKey 通用比较对象
     * @return list数据
     */
    @SuppressWarnings("all")
    private List<TerminalCommonEntity> parseData(GroupByResults<BasicDBObject> results, String compareObjectKey) {
        logger.info(JSON.toJSONString(results));
        List<TerminalCommonEntity> terminalCommonEntities = Lists.newArrayList();
        Iterator<BasicDBObject> iterator = results.iterator();
        while (iterator.hasNext()) {
            Map map = JSON.toJavaObject((JSON) JSON.toJSON(iterator.next()), Map.class);
            TerminalCommonEntity terminalCommonEntity = new TerminalCommonEntity()
                    .setPlatform((String) map.get(GROUP_PLATFORM_KEY));
            terminalCommonEntity.setCompareObject((String) map.get(compareObjectKey));
            terminalCommonEntity.setCount(((Double) map.get("count")).intValue());
            terminalCommonEntity.setCreateTime((String) map.get(GROUP_DATE_TIME_KEY));
            terminalCommonEntity.setAppId((String) map.get(APP_ID));
            terminalCommonEntities.add(terminalCommonEntity);
        }
        return terminalCommonEntities
                .stream()
                .filter(o -> !Strings.isNullOrEmpty(o.getCompareObject()))
                .collect(Collectors.toList());
    }

    /**
     * doc解析
     *
     * @param documents doc
     * @return 格式化好的数据
     */
    private List<TerminalCommonEntity> parseDocData(AggregateIterable<Document> documents) {
        List<TerminalCommonEntity> result = Lists.newArrayList();
        documents.forEach((Consumer<Document>) document -> {
            logger.info(document.toJson());
            Map objectMap = JSON.toJavaObject(JSON.parseObject(document.toJson()), Map.class);
            TerminalCommonEntity entity = new TerminalCommonEntity();
            entity.setCount((Integer) objectMap.get("count"));
            Map body = JSON.toJavaObject((JSON) objectMap.get("_id"), Map.class);
            entity.setCompareObject((String) body.get("compareObject"));
            entity.setPlatform((String) body.get("platform"));
            entity.setCreateTime((String) body.get("createTime"));
            entity.setAppId((String) body.get("appId"));
            result.add(entity);
        });
        return result.stream()
                .filter(terminalCommonEntity -> !Strings.isNullOrEmpty(terminalCommonEntity.getCompareObject()))
                .collect(Collectors.toList());
    }

    /**
     * 获取昨天的时间
     *
     * @return 字符串时间
     */
    private String obtainYesterday() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("UTC+8")).minusDays(1);
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 获取昨天
     *
     * @return 昨天
     * @throws ParseException 解析错误
     */
    private long obtainMidNightTimestamp() throws ParseException {
        LocalDateTime yesterday = LocalDateTime.now(ZoneId.of("UTC+8"));
        return new SimpleDateFormat("yyyy-MM-dd").parse(yesterday.toString()).getTime();
    }
}

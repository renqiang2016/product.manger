package com.phicomm.product.manger.service;

import com.google.common.collect.Lists;
import com.phicomm.product.manger.dao.BpmMeasureStatisticMapper;
import com.phicomm.product.manger.model.bpm.BpmMeasureBean;
import com.phicomm.product.manger.module.dds.CustomerContextHolder;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 用户测量数据统计
 *
 * Created by yafei.hou on 2017/11/8.
 * @author yafei.hou
 */
@Service
public class BpmMeasureStatisticService {

    private static final Logger logger = Logger.getLogger(BpmMeasureStatisticService.class);

    private static final int LIMIT_MONTH_NUM = 12;
    private static final int LIMIT_DAY_NUM = 12;
    private static final int HOURS = 24;

    private BpmMeasureStatisticMapper bpmMeasureStatisticMapper;

    private BpmStatisticService bpmStatisticService;

    @Autowired
    public BpmMeasureStatisticService(BpmMeasureStatisticMapper bpmMeasureStatisticMapper,
                                      BpmStatisticService bpmStatisticService) {
        this.bpmStatisticService = bpmStatisticService;
        this.bpmMeasureStatisticMapper = bpmMeasureStatisticMapper;
        Assert.notNull(this.bpmStatisticService);
        Assert.notNull(this.bpmMeasureStatisticMapper);
    }

    /**
     * 每天自动执行统计信息
     */
    public void cronTask() {

        for (BpmMeasureBean beanMonth : msp2List(statisticMeasureMonth())) {
            CustomerContextHolder.selectLocalDataSource();
            bpmMeasureStatisticMapper.insertMonth(beanMonth);
            CustomerContextHolder.clearDataSource();
        }
        logger.info("更新blood_pressure_measure_data_size_every_month表");
        for (BpmMeasureBean beanDay : msp2List(statisticMeasureDay())) {
            CustomerContextHolder.selectLocalDataSource();
            bpmMeasureStatisticMapper.insertDay(beanDay);
            CustomerContextHolder.clearDataSource();
        }
        logger.info("更新blood_pressure_measure_data_size_every_day表");
        for (BpmMeasureBean beanHour : msp2List(statisticMeasureHour())) {
            CustomerContextHolder.selectLocalDataSource();
            bpmMeasureStatisticMapper.insertHour(beanHour);
            CustomerContextHolder.clearDataSource();
        }
        logger.info("更新blood_pressure_measure_data_size_every_hour表");

    }

    private Map<String, Integer> statisticMeasureMonth() {
        return bpmStatisticService.obtainBpmMeasureByMonth(LIMIT_MONTH_NUM);
    }

    private Map<String, Integer> statisticMeasureDay() {
        return bpmStatisticService.obtainBPMeasureCountByDay(LIMIT_DAY_NUM);
    }

    private Map<String, Integer> statisticMeasureHour() {
        return bpmStatisticService.obtainBpmMeasureByHour();
    }

    @Contract("null -> null")
    private List<BpmMeasureBean> msp2List(Map<String, Integer> map) {
        List<BpmMeasureBean> list = Lists.newArrayList();
        if (map == null) {
            return null;
        }
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(new BpmMeasureBean(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    /**
     * 查询服务
     * 测量数据，最近12个月的数据
     *
     * @return 返回key：日期，value：数量；的映射表
     */
    public Map<String, Integer> bpmMeasureDataSizeByMonth() {
        CustomerContextHolder.selectLocalDataSource();
        List<BpmMeasureBean> list = bpmMeasureStatisticMapper.selectMonth(LIMIT_MONTH_NUM);
        CustomerContextHolder.clearDataSource();
        return getStringIntegerMap(list);
    }

    public Map<String, Integer> bpmMeasureDataSizeByDay() {
        CustomerContextHolder.selectLocalDataSource();
        List<BpmMeasureBean> list = bpmMeasureStatisticMapper.selectDay(LIMIT_DAY_NUM);
        CustomerContextHolder.clearDataSource();
        return getStringIntegerMap(list);
    }

    public Map<String, Integer> bpmMeasureDataSizeByHour() {
        CustomerContextHolder.selectLocalDataSource();
        List<BpmMeasureBean> list = bpmMeasureStatisticMapper.selectHour(HOURS);
        CustomerContextHolder.clearDataSource();
        return getStringIntegerMap(list);
    }

    @NotNull
    private Map<String, Integer> getStringIntegerMap(List<BpmMeasureBean> list) {
        Map<String, Integer> map = new TreeMap<>();
        if (list == null) {
            return new HashMap<>(0);
        }
        for (BpmMeasureBean bean : list) {
            map.put(bean.getMeasureTime(), bean.getMeasureCount());
        }
        logger.info(map);
        return map;
    }

}

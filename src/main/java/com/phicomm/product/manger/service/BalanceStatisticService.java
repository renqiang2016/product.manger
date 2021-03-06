package com.phicomm.product.manger.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.phicomm.product.manger.dao.*;
import com.phicomm.product.manger.exception.DataFormatException;
import com.phicomm.product.manger.model.statistic.*;
import com.phicomm.product.manger.module.analysis.UserCacheImpl;
import com.phicomm.product.manger.module.dds.CustomerContextHolder;
import com.phicomm.product.manger.utils.RegexUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 主要统计一下balance_status_info新增的量
 * Created by wei.yang on 2017/6/30.
 */
@Service
public class BalanceStatisticService {

    private BalanceUserManagerMapper balanceUserManagerMapper;

    private BalanceStatusMapper balanceStatusMapper;

    private LianbiActiveMapper lianbiActiveMapper;

    private UserCacheImpl userCache;

    private BalanceCronStatisticMapper balanceCronStatisticMapper;

    @Autowired
    public BalanceStatisticService(BalanceUserManagerMapper balanceUserManagerMapper,
                                   BalanceStatusMapper balanceStatusMapper,
                                   LianbiActiveMapper lianbiActiveMapper,
                                   UserCacheImpl userCache,
                                   BalanceCronStatisticMapper balanceCronStatisticMapper) {
        this.balanceUserManagerMapper = balanceUserManagerMapper;
        this.balanceStatusMapper = balanceStatusMapper;
        this.lianbiActiveMapper = lianbiActiveMapper;
        this.userCache = userCache;
        this.balanceCronStatisticMapper = balanceCronStatisticMapper;
        Assert.notNull(this.balanceUserManagerMapper);
        Assert.notNull(this.balanceStatusMapper);
        Assert.notNull(this.lianbiActiveMapper);
        Assert.notNull(this.userCache);
        Assert.notNull(this.balanceCronStatisticMapper);
    }

    /**
     * 每个月的上传量:不包含今天
     *
     * @return 上传量
     */
    public Map<String, Integer> obtainCountByMonth(int month, String type) {
        DateTime dateTime = new DateTime(DateTime.now()).minusDays(1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        List<CountBean> countBeans = Lists.newArrayList();
        CustomerContextHolder.selectProdDataSource();
        if ("balance".equalsIgnoreCase(type) || "mac".equalsIgnoreCase(type)) {
            countBeans = balanceStatusMapper.obtainCountByMonth(month);
        }
        CustomerContextHolder.clearDataSource();
        if (countBeans.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, Integer> result = new TreeMap<>();
        for (CountBean countBean : countBeans) {
            result.put(countBean.getGenerateTime(), countBean.getGenerateCount());
        }
        for (int i = 0; i < month; i++) {
            String generateTime = dateFormat.format(dateTime.toDate());
            if (!result.containsKey(generateTime)) {
                result.put(generateTime, 0);
            }
            dateTime = dateTime.minusMonths(1);
        }

        return result;
    }

    /**
     * 获取N天的上传量
     *
     * @return 上传量
     */
    public Map<String, Integer> obtainCountByDay(int day, String type) {
        DateTime dateTime = new DateTime(DateTime.now()).minusDays(1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd");
        List<CountBean> countBeans = Lists.newArrayList();
        CustomerContextHolder.selectProdDataSource();
        if ("balance".equalsIgnoreCase(type) || "mac".equalsIgnoreCase(type)) {
            countBeans = balanceStatusMapper.obtainCountByDay(day);
        }
        CustomerContextHolder.clearDataSource();
        if (countBeans.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Integer> result = new TreeMap<>();
        for (CountBean countBean : countBeans) {
            result.put(countBean.getGenerateTime(), countBean.getGenerateCount());
        }
        for (int i = 0; i < day; i++) {
            String generateTime = dateFormat.format(dateTime.toDate());
            if (!result.containsKey(generateTime)) {
                result.put(generateTime, 0);
            }
            dateTime = dateTime.minusDays(1);
        }

        return result;
    }

    /**
     * 根据mac地址/SN号获取关于这个mac地址/SN号的相关信息
     *
     * @param param mac地址
     * @return mac的相关信息
     * @throws DataFormatException mac地址
     */
    public BalanceMacStatus obtainBalanceStatusInfo(String param) throws DataFormatException {
        CustomerContextHolder.selectProdDataSource();
        String location;
        String mac;
        if (param.length() == 15) {
            LianBiActiveBean lianBiActiveBean = lianbiActiveMapper.obtainActiveInfo(param.toUpperCase());
            location = formatLocation(lianBiActiveBean);
            mac = lianBiActiveBean.getMac();
        } else {
            boolean right = RegexUtil.checkMacFormat(param);
            if (!right) {
                throw new DataFormatException();
            }
            mac = formatMac(param);
            location = formatLocation(lianbiActiveMapper.obtainActiveCity(mac));
        }
        BalanceMacStatus balanceMacStatus = new BalanceMacStatus();
        balanceMacStatus.setActiveLocation(location)
                .setCreateTime(balanceStatusMapper.obtainStatusCreateTime(mac))
                .setMemberCount(balanceUserManagerMapper.obtainMemberCount(mac));
        CustomerContextHolder.clearDataSource();
        return balanceMacStatus;
    }

    /**
     * 获取相关数量信息
     *
     * @return 数量：电子秤mac、成员数、用户数
     */
    public BalanceAccountInfo obtainBalanceAccountInfo() {
        CustomerContextHolder.selectLocalDataSource();
        BalanceAccountInfo accountInfo = balanceCronStatisticMapper.getBalanceAccountInfo();
        CustomerContextHolder.clearDataSource();
        return accountInfo;
    }

    /**
     * 格式化mac地址
     *
     * @param mac mac地址
     * @return mac地址
     */
    private String formatMac(String mac) {
        int len = mac.length();
        StringBuilder builder = new StringBuilder();
        if (mac.length() == 17) {
            return mac.toLowerCase();
        } else {
            for (int i = 0; i < len / 2; i++) {
                builder.append(mac.subSequence(2 * i, 2 * i + 2));
                if (!(i == len / 2 - 1)) {
                    builder.append(":");
                }
            }
            return builder.toString().toLowerCase();
        }
    }

    /**
     * 格式化未知
     *
     * @param object 相关信息
     * @return 位置信息
     */
    private String formatLocation(Object object) {
        if (object == null) {
            return "无激活信息";
        }
        StringBuilder builder = new StringBuilder();
        String country;
        String province;
        String city;
        String county;
        boolean activated;
        if (object instanceof LianBiActiveBean) {
            country = ((LianBiActiveBean) object).getActiveCountry();
            province = ((LianBiActiveBean) object).getActiveProvince();
            city = ((LianBiActiveBean) object).getActiveCity();
            county = ((LianBiActiveBean) object).getActiveCounty();
            activated = ((LianBiActiveBean) object).isActivated();
        } else if (object instanceof BalanceLocationBean) {
            country = ((BalanceLocationBean) object).getActiveCountry();
            province = ((BalanceLocationBean) object).getActiveProvince();
            city = ((BalanceLocationBean) object).getActiveCity();
            county = ((BalanceLocationBean) object).getActiveCounty();
            activated = ((BalanceLocationBean) object).isActivated();
        } else {
            return "无激活信息";
        }
        if (activated) {
            if (!Strings.isNullOrEmpty(country)) {
                if ("Reserved Address".equalsIgnoreCase(country)) {
                    return "保留地址";
                }
                builder.append(country);
                if (!Strings.isNullOrEmpty(province)) {
                    builder.append("、").append(province);
                }
                if (!Strings.isNullOrEmpty(city)) {
                    builder.append("、").append(city);
                }
                if (!Strings.isNullOrEmpty(county)) {
                    builder.append("、").append(county);
                }
                return builder.toString();
            } else {
                return "位置缺失";
            }
        } else {
            return "无激活信息";
        }

    }

    /**
     * 按年龄统计用户中的男女数量
     *
     * @return 不同年龄段的男女数量
     */
    public Map<String, int[]> statisticUserByAge() {
        return userCache.getUserCache().getUserResult();
    }

    /**
     * 统计用户中的男女数量
     *
     * @return 用户中的男女数量
     */
    @SuppressWarnings("unchecked")
    public Map<String, Integer> statisticUser() {
        Map<String, Integer> result = new HashMap<>();
        CustomerContextHolder.selectLocalDataSource();
        Map<String, Map<String, Integer>> userCountInfo = balanceCronStatisticMapper.getBalanceUserCountInfo();
        result.put("男", userCountInfo.get("男").get("count"));
        result.put("女", userCountInfo.get("女").get("count"));
        CustomerContextHolder.clearDataSource();
        return result;
    }

    /**
     * 按年龄统计成员中的男女数量
     *
     * @return 不同年龄段的男女数量
     */
    @SuppressWarnings("unchecked")
    public Map<String, int[]> statisticMemberByAge() {
        return userCache.getUserCache().getMemberResult();
    }

    /**
     * 统计成员中的男女数量
     *
     * @return 成员中的男女数量
     */
    @SuppressWarnings("unchecked")
    public Map<String, Integer> statisticMember() {
        Map<String, Integer> result = new HashMap<>();
        CustomerContextHolder.selectLocalDataSource();
        Map<String, Map<String, Integer>> memberCountInfo = balanceCronStatisticMapper.getBalanceMemberCountInfo();
        result.put("男", memberCountInfo.get("男").get("count"));
        result.put("女", memberCountInfo.get("女").get("count"));
        CustomerContextHolder.clearDataSource();
        return result;
    }
}

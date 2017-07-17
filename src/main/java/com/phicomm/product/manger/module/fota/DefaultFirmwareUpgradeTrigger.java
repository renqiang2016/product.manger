package com.phicomm.product.manger.module.fota;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.phicomm.product.manger.utils.HttpsUtil;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * 默认的操作
 *
 * Created by yufei.liu on 2017/7/12.
 */
public class DefaultFirmwareUpgradeTrigger extends AbstractFirmwareUpgradeTrigger {

    @Override
    public void trigger(FirmwareUpgradeContext firmwareUpgradeContext) throws NoSuchAlgorithmException,
            KeyManagementException, IOException {
        getLogger().info("trigger fota firmware upgrade.");
        getLogger().info(firmwareUpgradeContext);
        String param = firmwareUpgradeContext.getParam();
        JSONObject jsonObject = JSON.parseObject(param);
        String testCallbackUrl = String.valueOf(JSONPath.eval(jsonObject, "$.wristband.testCallback"));
        String prodCallbackUrl = String.valueOf(JSONPath.eval(jsonObject, "$.wristband.prodCallback"));
        String data = firmwareUpgradeContext.getFirmwareData();
        getLogger().info(data);
        if(firmwareUpgradeContext.isTestEnvironment() && !Strings.isNullOrEmpty(testCallbackUrl)) {
            getLogger().info(String.format("testCallbackUrl is %s.", testCallbackUrl));
            HttpsUtil.post(testCallbackUrl, data, Charsets.UTF_8.name());
        }
        if(firmwareUpgradeContext.isProdEnvironment() && !Strings.isNullOrEmpty(prodCallbackUrl)) {
            getLogger().info(String.format("prodCallbackUrl is %s.", prodCallbackUrl));
            HttpsUtil.post(prodCallbackUrl, data, Charsets.UTF_8.name());
        }
    }

}

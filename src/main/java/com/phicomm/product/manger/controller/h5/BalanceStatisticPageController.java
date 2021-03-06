package com.phicomm.product.manger.controller.h5;

import com.phicomm.product.manger.annotation.FunctionPoint;
import com.phicomm.product.manger.enumeration.SessionKeyEnum;
import com.phicomm.product.manger.model.user.AdminUserInfo;
import com.phicomm.product.manger.module.navigation.NavigationManger;
import com.phicomm.product.manger.utils.VelocityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

/**
 * 体脂秤统计页面接口
 * <p>
 * @author yufei.liu on 2017/7/1.
 */
@Controller
public class BalanceStatisticPageController {

    private NavigationManger navigationManger;

    @Autowired
    public BalanceStatisticPageController(NavigationManger navigationManger) {
        this.navigationManger = navigationManger;
        Assert.notNull(this.navigationManger);
    }

    /**
     * 返回首页
     */
    @RequestMapping(value = "balance/statistic/page/k/code/active", method = RequestMethod.GET)
    @ApiIgnore("首页展示")
    @FunctionPoint(value = "common")
    public ModelAndView showBalanceStatisticKCode(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("framework/main_layout");
        AdminUserInfo adminUserInfo = (AdminUserInfo) session.getAttribute(SessionKeyEnum.USER_INFO.getKeyName());
        modelAndView.getModel().put("context", "statistic/balance_k_code_active.vm");
        modelAndView.getModelMap().put("uuid", VelocityUtil.getUUID());
        modelAndView.getModelMap().put("adminUserInfo", adminUserInfo);
        modelAndView.getModelMap().put("navigation", navigationManger.getNavigationModel("balanceStatisticKCodeActive"));
        return modelAndView;
    }

    /**
     * 电子秤数量统计
     */
    @RequestMapping(value = "balance/statistic/count", method = RequestMethod.GET)
    @ApiIgnore("首页展示")
    @FunctionPoint(value = "common")
    public ModelAndView showBalanceStatisticCount(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("framework/main_layout");
        AdminUserInfo adminUserInfo = (AdminUserInfo) session.getAttribute(SessionKeyEnum.USER_INFO.getKeyName());
        modelAndView.getModel().put("context", "statistic/balance_count.vm");
        modelAndView.getModelMap().put("uuid", VelocityUtil.getUUID());
        modelAndView.getModelMap().put("adminUserInfo", adminUserInfo);
        modelAndView.getModelMap().put("navigation", navigationManger.getNavigationModel("balanceStatisticCount"));
        return modelAndView;
    }

    /**
     * 获取电子秤位置信息统计：包括销售地区和balance_location_info
     */
    @RequestMapping(value = "balance/statistic/balance/sales/location", method = RequestMethod.GET)
    @ApiIgnore("首页展示")
    @FunctionPoint(value = "common")
    public ModelAndView showBalanceSalesLocation(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("framework/main_layout");
        AdminUserInfo adminUserInfo = (AdminUserInfo) session.getAttribute(SessionKeyEnum.USER_INFO.getKeyName());
        modelAndView.getModel().put("context", "statistic/balance_sales_location.vm");
        modelAndView.getModelMap().put("uuid", VelocityUtil.getUUID());
        modelAndView.getModelMap().put("adminUserInfo", adminUserInfo);
        modelAndView.getModelMap().put("navigation", navigationManger.getNavigationModel("balanceStatisticLocation"));
        return modelAndView;
    }

    /**
     * 获取电子秤账户信息
     */
    @RequestMapping(value = "balance/statistic/account/info/page", method = RequestMethod.GET)
    @ApiIgnore("首页展示")
    @FunctionPoint(value = "common")
    public ModelAndView showBalanceAccountPage(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("framework/main_layout");
        AdminUserInfo adminUserInfo = (AdminUserInfo) session.getAttribute(SessionKeyEnum.USER_INFO.getKeyName());
        modelAndView.getModel().put("context", "statistic/balance_account_info.vm");
        modelAndView.getModelMap().put("uuid", VelocityUtil.getUUID());
        modelAndView.getModelMap().put("adminUserInfo", adminUserInfo);
        modelAndView.getModelMap().put("navigation", navigationManger.getNavigationModel("balanceAccount"));
        return modelAndView;
    }

    /**
     * 获取用户成分分析的页面
     */
    @RequestMapping(value = "statistic/page/user/analysis", method = RequestMethod.GET)
    @ApiIgnore("获取用户成分分析的页面")
    @FunctionPoint(value = "common")
    public ModelAndView showStatisticPageUserAnalysis(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("framework/main_layout");
        AdminUserInfo adminUserInfo = (AdminUserInfo) session.getAttribute(SessionKeyEnum.USER_INFO.getKeyName());
        modelAndView.getModel().put("context", "statistic/user_analysis.vm");
        modelAndView.getModelMap().put("uuid", VelocityUtil.getUUID());
        modelAndView.getModelMap().put("adminUserInfo", adminUserInfo);
        modelAndView.getModelMap().put("navigation", navigationManger.getNavigationModel("userAnalysis"));
        return modelAndView;
    }

    /**
     * 统计每日体脂秤活跃量
     */
    @RequestMapping(value = "statistic/page/balance/active/statistic", method = RequestMethod.GET)
    @ApiIgnore("统计每日体脂秤活跃量")
    @FunctionPoint(value = "common")
    public ModelAndView showBalanceActiveStatistic(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("framework/main_layout");
        AdminUserInfo adminUserInfo = (AdminUserInfo) session.getAttribute(SessionKeyEnum.USER_INFO.getKeyName());
        modelAndView.getModelMap().put("context", "statistic/balance_active_statistic.vm");
        modelAndView.getModelMap().put("uuid", VelocityUtil.getUUID());
        modelAndView.getModelMap().put("adminUserInfo", adminUserInfo);
        modelAndView.getModelMap().put("navigation", navigationManger.getNavigationModel("balanceActiveStatistic"));
        return modelAndView;
    }

    @RequestMapping(value = "statistic/page/balance/24hour/display", method = RequestMethod.GET)
    @ApiIgnore("统计24小时统计分布")
    @FunctionPoint(value = "common")
    public ModelAndView showBalance24hourDisplay(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("framework/main_layout");
        AdminUserInfo adminUserInfo = (AdminUserInfo) session.getAttribute(SessionKeyEnum.USER_INFO.getKeyName());
        modelAndView.getModelMap().put("context", "statistic/balance_24hour_display.vm");
        modelAndView.getModelMap().put("uuid", VelocityUtil.getUUID());
        modelAndView.getModelMap().put("adminUserInfo", adminUserInfo);
        modelAndView.getModelMap().put("navigation", navigationManger.getNavigationModel("balance24HourDisplay"));
        return modelAndView;
    }
    @RequestMapping(value = "statistic/page/balance/electrode/display", method = RequestMethod.GET)
    @ApiIgnore("统计24小时统计分布")
    @FunctionPoint(value = "common")
    public ModelAndView showBalanceElectrodeDisplay(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("framework/main_layout");
        AdminUserInfo adminUserInfo = (AdminUserInfo) session.getAttribute(SessionKeyEnum.USER_INFO.getKeyName());
        modelAndView.getModelMap().put("context", "statistic/balance_electrode_display.vm");
        modelAndView.getModelMap().put("uuid", VelocityUtil.getUUID());
        modelAndView.getModelMap().put("adminUserInfo", adminUserInfo);
        modelAndView.getModelMap().put("navigation", navigationManger.getNavigationModel("balanceElectrodeDisplay"));
        return modelAndView;
    }
}

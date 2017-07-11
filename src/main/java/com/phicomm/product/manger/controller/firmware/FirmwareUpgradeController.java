package com.phicomm.product.manger.controller.firmware;

import com.phicomm.product.manger.annotation.FunctionPoint;
import com.phicomm.product.manger.exception.DataFormatException;
import com.phicomm.product.manger.exception.UploadFileException;
import com.phicomm.product.manger.exception.VersionHasExistException;
import com.phicomm.product.manger.model.common.CommonResponse;
import com.phicomm.product.manger.service.FirmwareUpgradeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 固件文件上传
 * <p>
 * Created by yufei.liu on 2017/7/10.
 */
@Controller
public class FirmwareUpgradeController {

    private FirmwareUpgradeService firmwareUpgradeService;

    @Autowired
    public FirmwareUpgradeController(FirmwareUpgradeService firmwareUpgradeService) {
        this.firmwareUpgradeService = firmwareUpgradeService;
        Assert.notNull(this.firmwareUpgradeService);
    }

    /**
     * 固件升级下面手环项目文件上传接口
     *
     * @return 上传成功返回0，失败返回1
     */
    @RequestMapping(value = "firmware/upgrade/wristband/file/upload", method = RequestMethod.POST,
            consumes = "multipart/form-data", produces = "application/json")
    @ApiOperation("固件升级下面手环项目文件上传接口")
    @ResponseBody
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "正常情况", response = CommonResponse.class),
            @ApiResponse(code = 2, message = "数据格式错误", response = CommonResponse.class),
            @ApiResponse(code = 7, message = "文件上传失败", response = CommonResponse.class),
            @ApiResponse(code = 8, message = "固件版本已经存在", response = CommonResponse.class)

    })
    @FunctionPoint(value = "common")
    public CommonResponse firmwareUpgradeWristbandFileUpload(@RequestParam("firmwareType") String firmwareType,
                                                             @RequestParam("hardwareVersion") String hardwareVersion,
                                                             @RequestParam("firmwareVersion") String firmwareVersion,
                                                             @RequestParam("environment") String environment,
                                                             @RequestPart("file") MultipartFile file)
            throws DataFormatException, UploadFileException, VersionHasExistException {
        firmwareUpgradeService.firmwareUpgradeWristbandFileUpload(firmwareType,
                hardwareVersion, firmwareVersion, environment, file);
        return CommonResponse.ok();
    }

}
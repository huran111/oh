package com.tykj.wx.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jfinal.core.Controller;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.exception.BusinessException;
import com.tykj.utils.UUIDUtils;
import com.tykj.wx.dto.UserInfoDTO;
import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.service.IQrcodeService;

import com.tykj.wx.service.ITmpQrcodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huran
 * @since 2019-05-11
 */
@Api(tags = "二维码")
@Slf4j
@RestController
@RequestMapping("/rest/wx/qrcode")
public class QrcodeController extends Controller {
    @Autowired
    IQrcodeService qrcodeService;
    @Autowired
    ITmpQrcodeService tmpQrcodeService;


    /**
     * 判断用户是否绑定二维码信息
     *
     * @param qrParam
     * @return
     */
    @ApiOperation(value = "判断用户是否绑定二维码信息", notes = "判断用户是否绑定二维码信息")
    @GetMapping(value = "/bindingUserInfo")
    public ApiResponse isbindingUserInfo(@RequestParam(value = "qrParam") String qrParam, @RequestParam(value = "openId") String openId) throws Exception {
        if (qrParam.contains("tmp")) {
            QueryWrapper<TmpQrcode> queryWrapper = new QueryWrapper<TmpQrcode>();
            queryWrapper.lambda().eq(TmpQrcode::getQrParam, qrParam);
            TmpQrcode var1 = this.tmpQrcodeService.getOne(queryWrapper);
            if (null != var1) {
                if (var1.getOpenId().equals(openId)) {
                    log.info("=========>>>" + var1);
                    return new ApiResponse(ApiCode.IS_ONESELF, var1);
                }
            }
            queryWrapper = new QueryWrapper<TmpQrcode>();
            queryWrapper.lambda().eq(TmpQrcode::getQrParam, qrParam);
            TmpQrcode var2 = this.tmpQrcodeService.getOne(queryWrapper);
            if (null != var2) {
                log.info("=========>>>" + var2);
                //已经绑定
                return new ApiResponse(ApiCode.BINDING, var2);
            } else {
                log.info("=========>>>" + "未绑定");

                //未绑定
                return new ApiResponse(ApiCode.NOT_BINDING);
            }
        }
        QueryWrapper<Qrcode> queryWrapper = new QueryWrapper<Qrcode>();
        queryWrapper.lambda().eq(Qrcode::getQrParam, qrParam);
        Qrcode qrcodeVar = this.qrcodeService.getOne(queryWrapper);
        if (null != qrcodeVar) {
            if (qrcodeVar.getOpenId().equals(openId)) {
                log.info("=========>>>" + qrcodeVar);
                return new ApiResponse(ApiCode.IS_ONESELF, qrcodeVar);
            }
        }
        queryWrapper = new QueryWrapper<Qrcode>();
        queryWrapper.lambda().eq(Qrcode::getQrParam, qrParam).eq(Qrcode::getIsBinding, "1");
        Qrcode qrcode = this.qrcodeService.getOne(queryWrapper);
        if (null != qrcode) {
            //已经绑定
            log.info("=========>>>" + qrcode);

            return new ApiResponse(ApiCode.BINDING, qrcode);
        } else {
            log.info("=========>>>" + ApiCode.NOT_BINDING);

//          //未绑定
            return new ApiResponse(ApiCode.NOT_BINDING);
        }
    }

    /**
     * 绑定用户信息 ---线下扫描的
     *
     * @return
     */
    @ApiOperation(value = "绑定用户信息", notes = "绑定用户信息")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/bindingQr")
    public ApiResponse bindingQr(@RequestBody @Valid UserInfoDTO userInfoDTO, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().stream().forEach(fieldError -> {
                log.info("==============>>>" + fieldError.getDefaultMessage());
                throw new BusinessException(ApiCode.EMPTY_PARAM, fieldError.getDefaultMessage());
            });
        }
        /**
         * 编辑
         */
        if (StringUtils.isNoneEmpty(userInfoDTO.getId()) && StringUtils.isNoneEmpty(userInfoDTO.getQrParam())) {
            if (userInfoDTO.getQrParam().contains("tmp")) {
                TmpQrcode tmpQrcode = tmpQrcodeService.getOne(new QueryWrapper<TmpQrcode>()
                        .lambda().eq(TmpQrcode::getOpenId, userInfoDTO.getOpenId()).eq(TmpQrcode::getQrParam, userInfoDTO.getQrParam()));
                if (null != tmpQrcode) {
                    tmpQrcode.setPhoneNum(userInfoDTO.getPhone())
                            .setPlateNum(userInfoDTO.getPlatNum());
                    tmpQrcodeService.updateById(tmpQrcode);
                    return new ApiResponse(ApiCode.REQUEST_SUCCESS, tmpQrcode);

                }
            } else {
                Qrcode tmpQrcode = qrcodeService.getOne(new QueryWrapper<Qrcode>()
                        .lambda().eq(Qrcode::getOpenId, userInfoDTO.getOpenId()).eq(Qrcode::getQrParam, userInfoDTO.getQrParam()));
                if (null != tmpQrcode) {
                    tmpQrcode.setPhoneNum(userInfoDTO.getPhone())
                            .setPlateNum(userInfoDTO.getPlatNum());
                    qrcodeService.updateById(tmpQrcode);
                    return new ApiResponse(ApiCode.REQUEST_SUCCESS, tmpQrcode);
                }
            }
        } else {
            //只保持 线下的
            Qrcode qrcode = new Qrcode();
            qrcode.setId(UUIDUtils.getUUID()).setOpenId(userInfoDTO.getOpenId())
                    .setPhoneNum(userInfoDTO.getPhone())
                    .setPlateNum(userInfoDTO.getPlatNum().toUpperCase())
                    .setQrParam(userInfoDTO.getQrParam())
                    .setCreateTime(new Date()).setIsSwitch("1")
                    .setIsBinding("1").setImgUrl("");
            this.qrcodeService.saveOrUpdate(qrcode);
            return new ApiResponse(ApiCode.BINDING_SUCCESS);
        }
        return null;
    }

    @ApiOperation(value = "获取图片信息", notes = "获取图片信息")
    @GetMapping(value = "/getQrParamImg/{tmpQrParam}/{jpg}", produces = MediaType.IMAGE_JPEG_VALUE)
    public void getQrParamImg(@PathVariable String tmpQrParam,@PathVariable String jpg,  HttpServletResponse response) throws IOException {
        String path = "D:/img/tmpQrParam/1.jpg";
        System.out.println(path);
        BufferedImage img = new BufferedImage(300, 150, BufferedImage.TYPE_INT_RGB);
        img = ImageIO.read(new FileInputStream(new File(path)));
        response.setContentType("");
        ImageIO.write(img, "JPEG", response.getOutputStream());
    }

    @GetMapping(value = "/onOrOffQr")
    public ApiResponse onOrOffQr(@RequestParam(value = "qrParam") String qrParam, @RequestParam(value = "openId") String openId
            , @RequestParam(value = "isSwitch") String isSwitch) throws Exception {
        if (qrParam.contains("tmp")) {
            TmpQrcode tmpQrcode = tmpQrcodeService.getOne(new QueryWrapper<TmpQrcode>().lambda().eq(TmpQrcode::getQrParam, qrParam)
                    .eq(TmpQrcode::getOpenId, openId));
            if (null != tmpQrcode) {
                tmpQrcode.setIsSwitch(isSwitch);
                tmpQrcodeService.updateById(tmpQrcode);
                return new ApiResponse(ApiCode.LONG_SUCCESS);
            }
        } else {
            Qrcode qrcode = qrcodeService.getOne(new QueryWrapper<Qrcode>().lambda().eq(Qrcode::getQrParam, qrParam)
                    .eq(Qrcode::getOpenId, openId));
            if (null != qrcode) {
                qrcode.setIsSwitch(isSwitch);
                qrcodeService.updateById(qrcode);
                return new ApiResponse(ApiCode.LONG_SUCCESS);
            }
        }
        return null;
    }

    @GetMapping(value = "deleteTmpQr")
    public ApiResponse deleteTmpQr(@RequestParam(value = "qrParam") String qrParam,
                                   @RequestParam(value = "openId") String openId) {
        if("tmp".equals(qrParam)){
            QueryWrapper<TmpQrcode> qrcodeQueryWrapper=new QueryWrapper<TmpQrcode>();
            qrcodeQueryWrapper.lambda().eq(TmpQrcode::getQrParam,qrParam).eq(TmpQrcode::getOpenId,openId);
            tmpQrcodeService.remove(qrcodeQueryWrapper);
            return new ApiResponse(ApiCode.DELETE_SUCCESS,"删除体验码成功");
        }else {
            QueryWrapper<Qrcode> qrcodeQueryWrapper=new QueryWrapper<Qrcode>();
            qrcodeQueryWrapper.lambda().eq(Qrcode::getQrParam,qrParam).eq(Qrcode::getOpenId,openId);
            qrcodeService.remove(qrcodeQueryWrapper);
            return new ApiResponse(ApiCode.DELETE_SUCCESS,"删除正式码成功");
        }
    }
}

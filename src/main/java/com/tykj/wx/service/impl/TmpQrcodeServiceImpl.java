package com.tykj.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jfinal.aop.Duang;
import com.jfinal.weixin.sdk.utils.IOUtils;
import com.jfinal.wxaapp.api.WxaQrcodeApi;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.common.SysConstant;
import com.tykj.listener.AddImageTask;
import com.tykj.utils.UUIDUtils;
import com.tykj.wx.dto.UserInfoDTO;
import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.mapper.TmpQrcodeMapper;
import com.tykj.wx.service.IQrcodeService;
import com.tykj.wx.service.ITmpQrcodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huran
 * @since 2019-05-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TmpQrcodeServiceImpl extends ServiceImpl<TmpQrcodeMapper, TmpQrcode> implements ITmpQrcodeService {
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() + 1, 100L, TimeUnit.MILLISECONDS, new
            LinkedBlockingQueue<Runnable>());
    @Autowired
    ITmpQrcodeService tmpQrcodeService;
    @Autowired
    IQrcodeService qrcodeService;

    /**
     * 删除旧临时码保存新的
     *
     * @param userInfoDTO
     * @return
     * @throws IOException
     */
    @Override
    public TmpQrcode deleteOpenIdAndSaveTmpQrCode(UserInfoDTO userInfoDTO ) throws IOException {
        //删除之前得体验码
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("openId", userInfoDTO.getOpenId());
        boolean b = tmpQrcodeService.remove(queryWrapper);
        log.info("删除是否成功:[{}]", b);
        TmpQrcode tmpQrcode = new TmpQrcode();
        String qrParamId = UUIDUtils.getQrTmpUUID();
        log.info("openID:" + userInfoDTO.getOpenId());
        tmpQrcode.setId(UUIDUtils.getUUID()).setOpenId(userInfoDTO.getOpenId()).setCreateTime(new Date()).setImgUrl
                (SysConstant.DICTORY_TMP + qrParamId + ".png").setQrParam(qrParamId).setIsSwitch("1").setPlateNum
                (userInfoDTO.getPlatNum()).setPhoneNum(userInfoDTO.getPhone());
        WxaQrcodeApi wxaQrcodeApi1 = Duang.duang(WxaQrcodeApi.class);
        //生成二维码到指定目录
        InputStream inputStream = wxaQrcodeApi1.getUnLimit(qrParamId, "pages/home/home");
        IOUtils.toFile(inputStream, new File("/home/images/tmpQrParam/" + qrParamId + ".png"));
        log.info("生成体验码的信息为:[{}]", tmpQrcode);
        tmpQrcodeService.save(tmpQrcode);
        poolExecutor.execute(new AddImageTask(tmpQrcode.getQrParam(),tmpQrcodeService));
        return tmpQrcode;
    }

    /**
     * 保存或者编辑二维码
     *
     * @param userInfoDTO
     * @return
     */
    @Override
    public ApiResponse editOrSave(UserInfoDTO userInfoDTO) throws Exception {
        log.info("传递的参数:[{}]",userInfoDTO.toString());
        //编辑
        if (StringUtils.isNoneEmpty(userInfoDTO.getId()) && StringUtils.isNoneEmpty(userInfoDTO.getQrParam())) {
            if (userInfoDTO.getQrParam().contains(SysConstant.TMP_QRPARAM)) {
                TmpQrcode tmpQrcode = tmpQrcodeService.getOne(new QueryWrapper<TmpQrcode>().lambda().eq
                        (TmpQrcode::getOpenId, userInfoDTO.getOpenId()).eq(TmpQrcode::getQrParam, userInfoDTO
                        .getQrParam()));
                if (null != tmpQrcode) {
                    tmpQrcode.setPhoneNum(userInfoDTO.getPhone()).setPlateNum(userInfoDTO.getPlatNum());
                    tmpQrcodeService.updateById(tmpQrcode);
                    return new ApiResponse(ApiCode.REQUEST_SUCCESS, tmpQrcode);
                }
            } else {
                Qrcode qrcode = qrcodeService.getOne(new QueryWrapper<Qrcode>().lambda().eq(Qrcode::getOpenId,
                        userInfoDTO.getOpenId()).eq(Qrcode::getQrParam, userInfoDTO.getQrParam()));
                if (null != qrcode) {
                    qrcode.setPhoneNum(userInfoDTO.getPhone()).setPlateNum(userInfoDTO.getPlatNum());
                    qrcodeService.updateById(qrcode);
                    return new ApiResponse(ApiCode.REQUEST_SUCCESS, qrcode);
                }
            }
        } else {
            //只保存 线下的
            Qrcode qrcode = new Qrcode();
            qrcode.setId(UUIDUtils.getUUID()).setOpenId(userInfoDTO.getOpenId()).setPhoneNum(userInfoDTO.getPhone())
                    .setPlateNum(userInfoDTO.getPlatNum().toUpperCase()).setQrParam(userInfoDTO.getQrParam())
                    .setCreateTime(new Date()).setIsSwitch("1").setIsBinding("1").setImgUrl("qrParam/"+userInfoDTO.getQrParam()+".png");
            this.qrcodeService.saveOrUpdate(qrcode);
            return new ApiResponse(ApiCode.BINDING_SUCCESS);
        }
        return null;
    }

    /**
     * 判断用户是否绑定二维码信息
     *
     * @param qrParam
     * @param openId
     * @return
     */
    @Override
    public ApiResponse isbindingUserInfo(String qrParam, String openId) {
        log.info("qrParam:[{}],openId:[{}]", qrParam, openId);
        if (qrParam.contains(SysConstant.TMP_QRPARAM)) {
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
            //未绑定
            return new ApiResponse(ApiCode.NOT_BINDING);
        }
    }

    @Override
    public ApiResponse onOrOffQr(String qrParam, String openId, String isSwitch) {
        if (qrParam.contains(SysConstant.TMP_QRPARAM)) {
            TmpQrcode tmpQrcode = tmpQrcodeService.getOne(new QueryWrapper<TmpQrcode>().lambda().eq
                    (TmpQrcode::getQrParam, qrParam).eq(TmpQrcode::getOpenId, openId));
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

    /**
     * 删除临时码
     *
     * @param qrParam
     * @param openId
     * @return
     */
    @Override
    public ApiResponse deleteTmpQr(String qrParam, String openId) throws Exception {
        if (SysConstant.TMP_QRPARAM.equals(qrParam)) {
            QueryWrapper<TmpQrcode> qrcodeQueryWrapper = new QueryWrapper<TmpQrcode>();
            qrcodeQueryWrapper.lambda().eq(TmpQrcode::getQrParam, qrParam).eq(TmpQrcode::getOpenId, openId);
            tmpQrcodeService.remove(qrcodeQueryWrapper);
            return new ApiResponse(ApiCode.DELETE_SUCCESS, "删除体验码成功");
        } else {
            QueryWrapper<Qrcode> qrcodeQueryWrapper = new QueryWrapper<Qrcode>();
            qrcodeQueryWrapper.lambda().eq(Qrcode::getQrParam, qrParam).eq(Qrcode::getOpenId, openId);
            qrcodeService.remove(qrcodeQueryWrapper);
            return new ApiResponse(ApiCode.DELETE_SUCCESS, "删除正式码成功");
        }
    }
}

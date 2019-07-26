package com.tykj.wx.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.exception.BusinessException;
import com.tykj.wx.dto.UserInfoDTO;
import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.service.IQrcodeService;
import com.tykj.wx.service.ITmpQrcodeService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huran
 * @since 2019-05-12
 */
@Slf4j
@RestController
@RequestMapping("rest/wx/tmp/qrcode")
public class TmpQrcodeController /*extends BaseController<ITmpQrcodeService, TmpQrcode>*/ {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ITmpQrcodeService tmpQrcodeService;
    @Resource
    private IQrcodeService qrcodeService;


    /**
     * 生成体验码
     *
     * @param userInfoDTO   用户信息
     * @param bindingResult 校验
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "生成体验码-体验", notes = "生成体验码-体验")
    @PostMapping(value = "/generateTmpQr")
    public ApiResponse generateTmpQr(@RequestBody @Valid UserInfoDTO userInfoDTO, BindingResult bindingResult) throws
            Exception {
        log.info("生成体验码:" + userInfoDTO.toString());
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().stream().forEach(fieldError -> {
                log.info("==============>>>" + fieldError.getDefaultMessage());
                throw new BusinessException(ApiCode.EMPTY_PARAM, fieldError.getDefaultMessage());
            });
        }
        String redisOpenId = stringRedisTemplate.opsForValue().get(userInfoDTO.getOpenId());
        if (StringUtils.isNotEmpty(redisOpenId)) {
            Long seconds = stringRedisTemplate.getExpire(userInfoDTO.getOpenId());
            return new ApiResponse(ApiCode.BINDING, "您已生成体验码，请稍后再试", seconds);
        }

        TmpQrcode tmpQrcode = tmpQrcodeService.deleteOpenIdAndSaveTmpQrCode(userInfoDTO);
        stringRedisTemplate.opsForValue().set(userInfoDTO.getOpenId(), tmpQrcode.getQrParam(), 5L, TimeUnit.MINUTES);
        return new ApiResponse(ApiCode.REQUEST_SUCCESS, tmpQrcode);
    }

    /**
     * 查看我的挪车码
     *
     * @return
     */
    @ApiOperation(value = "查看我的挪车码-体验", notes = "查看我的挪车码-体验")
    @Transactional(rollbackFor = Exception.class)
    @GetMapping(value = "toViewQrParam")
    public ApiResponse toViewQrParam(@RequestParam(value = "openId") String openId) throws Exception {
        log.info("查看我的挪车码:[{}]:" + openId);
        //正式挪车码
        QueryWrapper<Qrcode> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Qrcode::getOpenId, openId);
        List<Qrcode> qrcodes = qrcodeService.list(queryWrapper);
        //体验挪车码
        QueryWrapper<TmpQrcode> tmpQrcodeQueryWrapper = new QueryWrapper<>();
        tmpQrcodeQueryWrapper.lambda().eq(TmpQrcode::getOpenId, openId);
        List<TmpQrcode> tmpQrcodes = tmpQrcodeService.list(tmpQrcodeQueryWrapper);
        if (CollectionUtils.isNotEmpty(qrcodes)) {
            //添加正式二维码到临时二维码集合里面
            qrcodes.forEach(data -> {
                TmpQrcode tmpQrcode = new TmpQrcode();
                BeanUtils.copyProperties(data, tmpQrcode);
                tmpQrcodes.add(tmpQrcode);
            });
        }
        if (CollectionUtils.isNotEmpty(tmpQrcodes)) {
            return new ApiResponse(ApiCode.REQUEST_SUCCESS, tmpQrcodes);
        }
        return ApiResponse.success();
    }
}

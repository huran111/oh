package com.tykj.wx.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.common.SysConstant;
import com.tykj.exception.BusinessException;
import com.tykj.utils.UUIDUtils;
import com.tykj.utils.WxUtils;
import com.tykj.wx.dto.LoginSessionKeyDTO;
import com.tykj.wx.dto.UserInfoDTO;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.service.ITmpQrcodeService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tykj.core.web.BaseController;

import javax.validation.Valid;
import java.util.Date;
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
public class TmpQrcodeController extends BaseController<ITmpQrcodeService, TmpQrcode> {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ITmpQrcodeService tmpQrcodeService;

    @ApiOperation(value = "生成体验码-体验", notes = "生成体验码-体验")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/generateTmpQr")
    public ApiResponse generateTmpQr(@RequestBody @Valid UserInfoDTO userInfoDTO, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().stream().forEach(fieldError -> {
                log.info("==============>>>" + fieldError.getDefaultMessage());
                throw new BusinessException(ApiCode.EMPTY_PARAM, fieldError.getDefaultMessage());
            });
        }

      /* String openId = stringRedisTemplate.opsForValue().get(userInfoDTO.getOpenId());
        if (StringUtils.isNotEmpty(openId)) {
            Long seconds = stringRedisTemplate.getExpire(userInfoDTO.getOpenId());
            return new ApiResponse(ApiCode.BINDING, "您已生成体验码，请稍后再试", seconds);
        }*/
        TmpQrcode tmpQrcode = new TmpQrcode();
      String uuid=  UUIDUtils.getUUID();
        tmpQrcode.setId(UUIDUtils.getUUID())
                .setOpenId(uuid)
                .setCreateTime(new Date())
                .setImgUrl(SysConstant.DICTORY_TMP + "1" + ".jpg")
                .setQrParam(UUIDUtils.getQrTmpUUID()).setIsSwitch("1").setPlateNum(userInfoDTO.getPlatNum())
                .setPhoneNum(userInfoDTO.getPhone()).setQrParam(UUIDUtils.getQrTmpUUID());

        //生成带参数的二维码
          tmpQrcodeService.saveOrUpdate(tmpQrcode);
          try {
              stringRedisTemplate.opsForValue().set(userInfoDTO.getOpenId(),tmpQrcode.getQrParam(),5L,TimeUnit.MINUTES);
          }catch (Exception e){
              e.printStackTrace();
          }
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
    public ApiResponse toViewQrParam(@RequestParam(value = "openId") String openId) throws Exception{
        QueryWrapper<TmpQrcode> qrcodeQueryWrapper = new QueryWrapper<>();
        qrcodeQueryWrapper.lambda().eq(TmpQrcode::getOpenId, openId);
        List<TmpQrcode> tmpQrcodes = tmpQrcodeService.list(qrcodeQueryWrapper);
        if (CollectionUtils.isNotEmpty(tmpQrcodes)) {
            return new ApiResponse(ApiCode.REQUEST_SUCCESS, tmpQrcodes);
        }
        return ApiResponse.success();
    }


}
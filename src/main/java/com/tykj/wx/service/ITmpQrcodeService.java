package com.tykj.wx.service;

import com.tykj.common.ApiResponse;
import com.tykj.wx.dto.UserInfoDTO;
import com.tykj.wx.entity.TmpQrcode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
* <p>
    *  服务类
    * </p>
*
* @author huran
* @since 2019-05-12
*/
public interface ITmpQrcodeService extends IService<TmpQrcode> {


    /**
     * 删除旧临时码保存新的临时码
     * @param userInfoDTO
     * @return
     * @throws IOException
     */
    TmpQrcode   deleteOpenIdAndSaveTmpQrCode(UserInfoDTO userInfoDTO) throws IOException;

    /**
     * 编辑或者绑定信息
     * @param userInfoDTO
     * @return
     */
    ApiResponse editOrSave(UserInfoDTO userInfoDTO) throws Exception;

    /**
     * 判断用户是否绑定二维码信息
     * @param qrParam
     * @param openId
     * @return
     */
    ApiResponse isbindingUserInfo(String qrParam, String openId);

    /**
     * 开启或者关闭通知
     * @param qrParam
     * @param openId
     * @param isSwitch
     * @return
     */
    ApiResponse onOrOffQr(String qrParam, String openId, String isSwitch);

    /**
     * 删除临时码
     * @param qrParam
     * @param openId
     * @return
     */
    ApiResponse deleteTmpQr(String qrParam, String openId) throws Exception;
}

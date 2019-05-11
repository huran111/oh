package com.tykj.wx.controller;


import com.tykj.wx.entity.User;
import com.tykj.wx.service.IUserService;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tykj.core.web.BaseController;
/**
* <p>
    *  前端控制器
    * </p>
*
* @author huran
* @since 2019-05-11
*/
@RestController
@RequestMapping("/wx/user")
    public class UserController extends BaseController<IUserService,User> {

}

package com.tykj.wx.service.impl;

import com.tykj.wx.entity.User;
import com.tykj.wx.mapper.UserMapper;
import com.tykj.wx.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author huran
* @since 2019-05-11
*/
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}

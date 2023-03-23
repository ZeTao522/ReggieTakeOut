package com.zzt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.domain.User;
import com.zzt.mapper.UserMapper;
import com.zzt.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

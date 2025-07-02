package com.indiegeeker.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.indiegeeker.mapper.UsersMapper;
import com.indiegeeker.pojo.entity.Users;
import com.indiegeeker.service.UsersService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wen
 * @since 2025-06-30 
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {

}

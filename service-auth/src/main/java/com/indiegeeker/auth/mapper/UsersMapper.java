package com.indiegeeker.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.indiegeeker.pojo.entity.Users;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author wen
 * @since 2025-06-30 
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

}

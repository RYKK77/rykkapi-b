package com.rykk.rykapicommon.service;

import com.rykk.rykapicommon.model.entity.User;

/**
 * 用户服务
 *
 * @author rykk
 */
public interface InnerUserService{
    /**
     * 在数据库中查询该accessKey是否被分配给了客户
     * @param accessKey accessKey
     * @return 用户对象
     */
    User getInvokeUser(String accessKey);
}

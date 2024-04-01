package com.rykk.rykapi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rykk.rykapi.model.entity.InterfaceInfo;
import com.rykk.rykapi.model.entity.User;
import com.rykk.rykapi.model.entity.UserInterfaceInfo;

/**
* @author Tyrrell
* @description 针对表【user_interface_info(用户)】的数据库操作Service
* @createDate 2024-01-19 22:54:42
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 校验
     *
     * @param userInterfaceInfo 联系表信息
     * @param add 是否是添加的校验
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     *
     * @param interfaceInfoId   接口ID
     * @param userId    用户ID
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}

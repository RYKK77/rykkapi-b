package com.rykk.rykapicommon.service;


import com.rykk.rykapicommon.model.entity.UserInterfaceInfo;

/**
* @author Tyrrell
* @description 针对表【user_interface_info(用户)】的数据库操作Service
* @createDate 2024-01-19 22:54:42
*/
public interface InnerUserInterfaceInfoService{

    /**
     * 校验
     *
     * @param userInterfaceInfo 联系表信息
     * @param add 是否是添加的校验
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 接口调用次数+1
     * @param interfaceInfoId   接口ID
     * @param userId    用户ID
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}

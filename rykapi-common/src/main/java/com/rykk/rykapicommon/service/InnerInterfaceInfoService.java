package com.rykk.rykapicommon.service;

import com.rykk.rykapicommon.model.entity.InterfaceInfo;

/**
* @author Tyrrell
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-12-30 20:57:26
*/
public interface InnerInterfaceInfoService{

    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 查询接口是否存在
     * @param path 接口路径
     * @param method    请求方法
     * @return 接口信息
     */
    InterfaceInfo getInterfaceInfo(String path, String method);

}

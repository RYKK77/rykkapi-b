package com.rykk.rykapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rykk.rykapicommon.model.entity.InterfaceInfo;

/**
* @author Tyrrell
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-12-30 20:57:26
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

}

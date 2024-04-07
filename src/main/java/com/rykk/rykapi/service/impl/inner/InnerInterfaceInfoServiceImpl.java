package com.rykk.rykapi.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rykk.rykapi.common.ErrorCode;
import com.rykk.rykapi.exception.BusinessException;
import com.rykk.rykapi.service.InterfaceInfoService;
import com.rykk.rykapicommon.model.entity.InterfaceInfo;
import com.rykk.rykapicommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;


    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        return ;
    }

    @Override
    public InterfaceInfo getInterfaceInfo(String path, String method) {
        if (StringUtils.isAnyBlank(path, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> interfaceInfoQueryWrapper = new QueryWrapper<>();
        interfaceInfoQueryWrapper.eq("url", path);
        interfaceInfoQueryWrapper.eq("method", method);

        return interfaceInfoService.getBaseMapper().selectOne(interfaceInfoQueryWrapper);
    }
}

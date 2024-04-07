package com.rykk.rykapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rykk.rykapi.common.ErrorCode;
import com.rykk.rykapi.exception.BusinessException;
import com.rykk.rykapi.exception.ThrowUtils;
import com.rykk.rykapi.service.UserInterfaceInfoService;
import com.rykk.rykapi.mapper.UserInterfaceInfoMapper;
import com.rykk.rykapicommon.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
* @author Tyrrell
* @description 针对表【user_interface_info(用户)】的数据库操作Service实现
* @createDate 2024-01-19 22:54:42
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{


    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {

        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Integer leftNum = userInterfaceInfo.getLeftNum();
        Long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
        Long userId = userInterfaceInfo.getUserId();

        // 创建时，接口和用户的id的合法性校验
        if (add) {
            ThrowUtils.throwIf(interfaceInfoId <= 0 || userId <= 0,ErrorCode.PARAMS_ERROR, "接口或用户参数错误");
        }
        // 校验信息
        ThrowUtils.throwIf(leftNum < 0, ErrorCode.PARAMS_ERROR, "初始剩余次数不能小于0");
    }

    @Override
    @Transactional
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId < 0 || userId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        return this.update(updateWrapper);
    }
}





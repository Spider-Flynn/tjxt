package com.tianji.remark.service;

import com.tianji.remark.domain.dto.LikeRecordFormDTO;
import com.tianji.remark.domain.po.LikedRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.Valid;

/**
 * <p>
 * 点赞记录表 服务类
 * </p>
 *
 * @author zhao
 * @since 2025-08-24
 */
public interface ILikedRecordService extends IService<LikedRecord> {

    void addLikeRecord(@Valid LikeRecordFormDTO recordDTO);
}

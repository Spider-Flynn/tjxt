package com.tianji.remark.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.remark.domain.dto.LikeRecordFormDTO;
import com.tianji.remark.domain.po.LikedRecord;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 点赞记录表 服务类
 * </p>
 * @author zhao
 * @since 2025-08-24
 */
public interface ILikedRecordService extends IService<LikedRecord> {

    void addLikeRecord(@Valid LikeRecordFormDTO recordDTO);

    Set<Long> isBizLiked(List<Long> bizIds);

    void readLikedTimesAndSendMessage(String bizType, int maxBizSize);
}

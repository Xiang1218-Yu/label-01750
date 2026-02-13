package com.dorm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.PageResult;
import com.dorm.entity.Announcement;
import com.dorm.mapper.AnnouncementMapper;
import com.dorm.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    public PageResult<Announcement> page(int current, int size, Integer type, String title) {
        Page<Announcement> page = new Page<>(current, size);
        return PageResult.of(announcementMapper.selectPageWithInfo(page, type, title));
    }

    public List<Announcement> latest(int limit) {
        return announcementMapper.selectList(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, 1)
                .orderByDesc(Announcement::getId)
                .last("LIMIT " + limit));
    }

    public void create(Announcement announcement) {
        announcement.setPublisherId(UserContext.getUserId());
        announcementMapper.insert(announcement);
    }

    public void update(Announcement announcement) {
        announcementMapper.updateById(announcement);
    }

    public void delete(Long id) {
        announcementMapper.deleteById(id);
    }

    public void updateStatus(Long id, Integer status) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(status);
        announcementMapper.updateById(announcement);
    }
}

package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.enums.ItemTypeEnum;
import com.yuan.entity.Translation;
import com.yuan.mapper.TranslationMapper;
import com.yuan.service.ArticleService;
import com.yuan.service.LikeService;
import com.yuan.service.TranslationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * <p>
 * 翻译表 服务实现类
 * </p>
 *
 * @author yuan
 * @since 2020-07-17
 */
@Service
public class TranslationServiceImpl extends ServiceImpl<TranslationMapper, Translation> implements TranslationService {

    @Autowired
    ArticleService articleService;
    @Autowired
    LikeService likeService;

    @CacheEvict(cacheNames = "tranCache", allEntries = true)
    @Override
    public void commitOrEdit(Translation t, Integer uid) {
        Translation tran;
        Integer tid = t.getId();
        if (tid == null || tid < 1) {   // 发布
            tran = new Translation();
            BeanUtils.copyProperties(t, tran, "id", "likes");
            tran.setUid(uid).setStatus(1);
            articleService.addTranslations(t.getAid());
        } else {                        // 编辑
            tran = this.getById(tid);
            Assert.isTrue(tran.getUid().equals(uid), "无操作权限");
            BeanUtils.copyProperties(t, tran, "id", "pid", "uid", "likes");
        }
        this.saveOrUpdate(tran);
    }

    @Cacheable(cacheNames = "tranCache")
    @Override
    public IPage<Translation> selectPage(Integer num, Integer size, Integer aid) {
        QueryWrapper<Translation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1).eq("aid", aid);
        Page<Translation> p = this.page(new Page<>(num, size), queryWrapper);
        p.getRecords().forEach(translation -> translation.setLikes(translation.getLikes()
                + likeService.getLikeCountByType(ItemTypeEnum.TRANSLATION.getType(), translation.getId())));
        return p;
    }

    @Override
    public void updateLikes(int id, int likes) {
        this.update(new UpdateWrapper<Translation>()
                .eq("id", id).setSql("likes=likes+" + likes)
        );
    }
}

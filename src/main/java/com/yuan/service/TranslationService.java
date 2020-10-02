package com.yuan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.jiekou.ItemCanLike;
import com.yuan.entity.Translation;

/**
 * <p>
 * 翻译表 服务类
 * </p>
 *
 * @author yuan
 * @since 2020-07-17
 */
public interface TranslationService extends IService<Translation>, ItemCanLike {

    void commitOrEdit(Translation t, Integer uid);

    IPage<Translation> selectPage(Integer num, Integer size, Integer aid);
}

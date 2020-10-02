package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.enums.ItemTypeEnum;
import com.yuan.common.enums.NoticeTypeEnum;
import com.yuan.entity.Comment;
import com.yuan.entity.Notice;
import com.yuan.entity.Post;
import com.yuan.entity.Translation;
import com.yuan.mapper.NoticeMapper;
import com.yuan.mapper.UserMapper;
import com.yuan.service.CommentService;
import com.yuan.service.NoticeService;
import com.yuan.service.PostService;
import com.yuan.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 通知表 服务实现类
 * </p>
 *
 * @author yuan
 * @since 2020-07-19
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Autowired
    TranslationService ts;
    @Autowired
    CommentService cs;
    @Autowired
    PostService ps;
    @Resource
    UserMapper userMapper;

    @Override
    public void read(Integer id, Integer uid) {
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("receiver", uid).set("status", 1);
        if (id != null && id != 0) updateWrapper.eq("id", id);
        this.update(updateWrapper);
    }

    /**
     * @param notifier 通知者id
     * @param itemId   资源id
     * @param itemType
     */
    @Override
    public void likeNotice(Integer notifier, Integer itemId, Integer itemType) {
        Notice n = new Notice();
        int receiver;
        String Nickname = userMapper.getNicknameById(notifier);
        StringBuilder title = new StringBuilder(Nickname + NoticeTypeEnum.LIKE + "了你的");
        // 选择接收者并生成标题 xxx点赞了你的xxx:xxxxxx
        if (itemType == ItemTypeEnum.TRANSLATION.getType()) {
            Translation item = ts.getById(itemId);
            receiver = item.getUid();
            title.append(ItemTypeEnum.TRANSLATION.getDesc() + ":" + item.getTitle());
        } else if (itemType == ItemTypeEnum.COMMENT.getType()) {
            Comment item = cs.getById(itemId);
            receiver = item.getFromId();
            title.append(ItemTypeEnum.COMMENT.getDesc() + ":" + item.getContent());
        } else if (itemType == ItemTypeEnum.POST.getType()) {
            Post item = ps.getById(itemId);
            receiver = item.getUid();
            title.append(ItemTypeEnum.POST.getDesc() + ":" + item.getTitle());
        } else {
            receiver = -1; // else 要结尾.随便搞个-1
        }
        // 自己就不给自己发消息 或者前端实现 你给你自己点了个赞
        if (receiver == notifier) return;
        n.setType(NoticeTypeEnum.LIKE.getType())
                .setItemId(itemId)
                .setItemType(itemType)
                .setNotifier(notifier)
                .setReceiver(receiver)
                .setTitle(title.toString());
        this.save(n);
    }

    @Override
    public void cancelLikeNotice(Integer notifier, Integer itemId, Integer itemType) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notifier", notifier)
                .eq("item_id", itemId)
                .eq("item_type", itemType)
                .eq("type", 2)
                .eq("status", 0);
        this.remove(queryWrapper);
    }

    @Override
    public void commentNotice(Integer notifier, Comment comment) {
        int receiver; // 接收者要具体判断
        String nickname = userMapper.getNicknameById(notifier);
        Notice n = new Notice();
        // pid==0 是对资源的评论
        // 标题和内容的设置
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            // 待用枚举类优化
            int type = comment.getItemType();
            String tmp = null;
            if ((type == ItemTypeEnum.TRANSLATION.getType())) {
                receiver = ts.getById(comment.getItemId()).getUid();
                tmp = ItemTypeEnum.TRANSLATION.getDesc();
            } else {
                receiver = ps.getById(comment.getItemId()).getUid();
                tmp = ItemTypeEnum.ARTICLE.getDesc();
            }
            n.setTitle(nickname + "评论了你的" + tmp + ":点击跳转")
                    .setContent(comment.getContent());
        } else {
            // pid!=0 说明是个子评论 接收者是toId
            receiver = comment.getToId();
            n.setTitle(nickname + "回复/评论了你的评论")
                    .setContent(comment.getContent());
        }
        n.setNotifier(notifier)
                .setReceiver(receiver)
                // 评论要指定资源,通知也要
                .setItemType(comment.getItemType())
                .setItemId(comment.getItemId())
                .setType(4);// 评论通知
        this.save(n);
    }


}

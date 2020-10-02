package com.yuan.service.impl;

import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.Platform;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.entity.Message;
import com.yuan.entity.User;
import com.yuan.mapper.MessageMapper;
import com.yuan.service.MessageService;
import com.yuan.service.UserService;
import com.yuan.util.JwtUtils;
import com.yuan.util.SensitiveFilterUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 留言表 服务实现类
 * </p>
 *
 * @author yuan
 * @since 2020-07-20
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    SensitiveFilterUtil sensitiveFilterUtil;

    @Autowired
    UserService userservice;

    @CacheEvict(cacheNames = "messageCache", allEntries = true)
    @Override
    public void save(Message message, HttpServletRequest request) {
        Message m = new Message();
        BeanUtils.copyProperties(message, m, "uid", "ip", "os", "id", "status", "gmtCreate");
        m.setContent(sensitiveFilterUtil.filter(m.getContent()));
        try {   // 如果登录的用户留言,可以标记为用户并有头像,未登录的则为游客
            Integer uid = jwtUtils.getUidByRequest(request);
            User u = userservice.getById(uid);
            m.setNickname(u.getNickname());
            m.setAvatar(u.getAvatar());
            m.setUserId(uid);
        } catch (Exception ignored) {
        }
        // 获取请求相关信息 os,browse,ip
        UserAgent ua = UserAgentUtil.parse(request.getHeader("User-Agent"));
        Browser browser = ua.getBrowser();
        Platform platform = ua.getPlatform();
        String version = ua.getVersion();
        m.setBrowser(browser.toString() + version).setOs(platform.toString()).setIp(request.getRemoteAddr());
        this.save(m);
    }


    @Cacheable(cacheNames = "messageCache")
    @Override
    public IPage<Message> selectPageParent(Integer num, Integer size) {
        if (size > 10) size = 10;
        QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>()
                .eq("status", 1)
                .eq("parent_id", 0)
                .orderByDesc("id");
        IPage<Message> parent = this.page(new Page<>(num, size), queryWrapper);
        //  携带子留言,隐藏ip
        parent.getRecords().forEach(message -> {
            message.setIp(null);
            IPage<Message> children = this.selectPageChildren(message.getId(), 1, 3);
            List<Message> childrenArr = children.getRecords();
            message.setChildren(childrenArr).setChildrenCount((int) children.getTotal());
        });
        return parent;
    }

    @Cacheable(cacheNames = "messageCache")
    @Override
    public IPage<Message> selectPageChildren(Integer pid, Integer num, Integer size) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<Message>()
                .eq("status", 1)
                .eq("parent_id", pid);
        IPage<Message> page = this.page(new Page<>(num, size), queryWrapper);
        page.getRecords().forEach(message -> message.setIp(null));
        return page;
    }
}

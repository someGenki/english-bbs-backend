package com.yuan.shiro;

import cn.hutool.core.bean.BeanUtil;
import com.yuan.entity.User;
import com.yuan.service.UserService;
import com.yuan.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AccountRealm是shiro进行登录或者权限校验的逻辑所在，算是核心了，我们需要重写3个方法，分别是
 * supports：为了让realm支持jwt的凭证校验
 * doGetAuthorizationInfo：权限校验
 * doGetAuthenticationInfo：登录认证校验
 * <p>
 * 其实主要就是doGetAuthenticationInfo登录认证这个方法，
 * 可以看到我们通过jwt获取到用户信息，判断用户的状态，最后异常就抛出对应的异常信息，
 * 否者封装成SimpleAuthenticationInfo返回给shiro。
 * 接下来我们逐步分析里面出现的新类：
 * <p>
 * 1、
 * shiro默认supports的是UsernamePasswordToken，
 * 而我们现在采用了jwt的方式，所以这里我们自定义一个JwtToken，
 * 来完成shiro的supports方法。
 * 2.
 * JwtUtils是个生成和校验jwt的工具类，其中有些jwt相关的密钥信息是从项目配置文件中配置的
 * 3.
 * 而在AccountRealm我们还用到了AccountProfile，这是为了登录成功之后返回的一个用户信息的载体，
 * 4.
 * 另外，如果你项目有使用spring-boot-devtools，
 * 需要添加一个配置文件，在resources目录下新建文件夹META-INF，然后新建文件spring-devtools.properties，这样热重启时候才不会报错。
 * 5.
 * 定义jwt的过滤器JwtFilter。
 * 这个过滤器是我们的重点，这里我们继承的是Shiro内置的AuthenticatingFilter，
 * 一个可以内置了可以自动登录方法的的过滤器，有些同学继承BasicHttpAuthenticationFilter也是可以的。
 * <p>
 * 额外参考:https://www.jianshu.com/p/3b2678fa3999
 */
@Slf4j
@Component
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserService userService;

    //告诉所支持的是jwt的Token,而不是其他token
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    //  权限认证
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    // 身份认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 执行时机
        // Subject currentUser = SecurityUtils.getSubject();
        // currentUser.login(token);
        JwtToken jwt = (JwtToken) token;
//        log.info("jwt----------------->{}", jwt);
        //因为前面把userId放到subject字段里
        // Assert.notNull(jwtUtils.getClaimByToken((String) jwt.getPrincipal()), "token无效");
        String userId = jwtUtils.getClaimByToken((String) jwt.getPrincipal()).getSubject();
        User user = userService.getById(Long.parseLong(userId));
        if (user == null) {
            throw new UnknownAccountException("账户不存在!");
        }
        if (user.getStatus() == -1) {
            throw new LockedAccountException("账户已被锁定!");
        }
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        // 这里的profile不知道怎么用 
//        log.info("profile->{}", profile.toString());
        //登录成功后,要返回一些信息,为了不返回私密的信息,就封装一个类
        return new SimpleAuthenticationInfo(profile, jwt.getCredentials(), getName());
    }
}
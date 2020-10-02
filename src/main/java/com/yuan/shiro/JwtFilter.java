package com.yuan.shiro;


import cn.hutool.json.JSONUtil;
import com.yuan.common.lang.Result;
import com.yuan.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <b>登陆处理</b>
 * 我们需要重写几个方法：
 * <p>
 * createToken：实现登录，我们需要生成我们自定义支持的JwtToken
 * <p>
 * onAccessDenied：拦截校验，当头部没有Authorization时候，我们直接通过，不需要自动登录；
 * 当带有的时候，首先我们校验jwt的有效性，没问题我们就直接执行executeLogin方法实现自动登录
 * <p>
 * onLoginFailure：登录异常时候进入的方法，我们直接把异常信息封装然后抛出
 * <p>
 * preHandle：拦截器的前置拦截，因为我们是前后端分析项目，项目中除了需要跨域全局配置之外，
 * 我们再拦截器中也需要提供跨域支持。这样，拦截器才不会在进入Controller之前就被限制了。
 */
@Component
public class JwtFilter extends AuthenticatingFilter {
    @Autowired
    JwtUtils jwtUtils;

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // 获取 token
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        if (StringUtils.isEmpty(jwt)) {
            return null;
        }
        return new JwtToken(jwt);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            // 没用jwt就放过,交给后面Controller里的注解进行拦截
            return true;
        } else {
            // 判断是否已过期 可能要设置页面跳转
            Claims claim = jwtUtils.getClaimByToken(token);
            if (claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
                System.out.println("token过期或无效");
                // throw new ExpiredCredentialsException("token已失效，请重新登录！");
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json");
                response.setStatus(401);
                response.getWriter().print("{\"msg\":\"tokenException(token过期或无效)\",\"code\":201}");
                // 或者设置跳转到登录页面
                return false;
            }
            HttpServletResponse servletResponse1 = (HttpServletResponse) servletResponse;
            servletResponse1.setHeader("set-cookie","jojo=1;");
        }
        // 执行自动登录

        return executeLogin(servletRequest, servletResponse);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            //处理登录失败的异常
            System.out.println("login failure!！");
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            Result r = Result.fail(throwable.getMessage());
            String json = JSONUtil.toJsonStr(r);
            httpResponse.getWriter().print(json);//把登录的异常返回给前端
        } catch (IOException ignored) {
        }
        return false;
    }

    /**
     * 对跨域提供支持,jwt是在controller之前的
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Set-Cookie","Max-Age=66666");
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
package com.zzt.filter;

import com.alibaba.fastjson.JSON;
import com.zzt.common.BaseContext;
import com.zzt.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户访问特定页面时是否完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")//拦截所有
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //向下转型，不然可能会有莫名其妙bug
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //定义不需要处理的请求路径,需要定义路径匹配器才能使得通配符匹配上
        String[] urlsWhiteList = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",//移动端发验证码
                "/user/login",//移动端登录
        };

        //1,获取本次请求的URI
        String requestURI = request.getRequestURI();

        //2,判断本次请求是否需要处理
        boolean check = check(urlsWhiteList, requestURI);

        //3,如果不需要处理,则直接放行
        if (check) {
            //{}是占位符
            log.debug("本次请求{}不需要处理,放行", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4,判断登录状态，如果已登录，则直接放行
        //同时,将用户id记录到ThreadLocal中
        if (request.getSession().getAttribute("employee") != null) {
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            log.info("本次请求{}已登录,用户id:{},放行", requestURI, BaseContext.getCurrentId());
            filterChain.doFilter(request, response);
            return;
        }

        //4.2移动用户,判断登录状态，如果已登录，则直接放行
        //同时,将用户id记录到ThreadLocal中
        if (request.getSession().getAttribute("user") != null) {
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            log.info("本次移动端请求{}已登录,用户id:{},放行", requestURI, BaseContext.getCurrentId());
            filterChain.doFilter(request, response);
            return;
        }

        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据,msg==NOTLOGIN时前端js代码做了页面跳转处理
        log.info("用户未登录,拦截本次请求{},跳转至登录页面", requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        //filterChain.doFilter(request,response);//这里不能加,不然就是放行了
        return;
    }

    /**
     * 路径匹配,检查本次请求是否需要放行.返回true代表不需要处理.
     *
     * @param urls
     * @param requestURI
     * @return
     */
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            //if (requestURI.equals(url)) return true;这是匹配不上的,因为String的equals不支持通配符
            if (PATH_MATCHER.match(url, requestURI)) return true;//用上路径匹配器才能匹配成功
        }
        return false;
    }
}

package com.boy.sso.client;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boy.sso.client.constants.SSOClientConstants;

/**
 * Servlet Filter implementation class SSOFilter
 * @author wesley
 *
 */
public class SSOFilter implements Filter{
	private static final Logger logger = LoggerFactory.getLogger(SSOFilter.class);
	
	private List<Pattern> exclusions = new ArrayList<Pattern>();
	
	private String ssoService;  //sso认证中心
	
	private String cookieName; //cookie名字
	
	private String logoutStr; // 退出字符串
	
	private String setCookieStr; //设置cookie字符串

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String path = request.getContextPath();
		String gotoURL = request.getParameter(SSOClientConstants.GOTO_URL); //目标地址路径
		if(gotoURL == null)
			gotoURL = request.getRequestURL().toString();
		//拼接请求路径
		String URL = ssoService + "?action=preLogin&setCookieURL=" + request.getScheme() + "://"
				+ request.getServerName() + ":" + request.getServerPort()
				+ path + "/setCookie&gotoURL=" + gotoURL;
		logger.info("----URL----"+URL);
		
		String reqUri = request.getRequestURI();
        if (isExcluded(reqUri)) {
            chain.doFilter(request, response);//放行
            return;
        }
		//判断本地是不是有认证票据
		Cookie ticket = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null)
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals(cookieName)) {
					ticket = cookie;
					break;
				}
			}
		//根据不同的请求地址做不同的处理
		if(request.getRequestURI().equals(path + logoutStr)){ //退出
			logger.info("-----logout----");
			SSOClient.doLogout(request, response, chain, ticket, URL);
		} else if(request.getRequestURI().equals(path + setCookieStr)){ //设置浏览器本地cookie （把认证中心返回的票据在本地生成一份）
			logger.info("-----setCookie----");
			SSOClient.setCookie(request, response);
		} else if(ticket != null){
			logger.info("-----ticket is not null----");
			SSOClient.authTicket(request, response, chain, ticket, URL);
		} else {
			logger.info("-----other----");
			response.sendRedirect(URL);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		ssoService = fConfig.getInitParameter(SSOClientConstants.ConfigParam.SSO_SERVICE);
		if(ssoService == null){
			throw new ServletException("Missing init parameter " + SSOClientConstants.ConfigParam.SSO_SERVICE);
		}
		cookieName = fConfig.getInitParameter(SSOClientConstants.ConfigParam.COOKIE_NAME);
		if(cookieName == null){
			throw new ServletException("Missing init parameter " + SSOClientConstants.ConfigParam.COOKIE_NAME);
		}
		SSOClient.setSsoService(ssoService);
		SSOClient.setCookieName(cookieName);
		logoutStr = fConfig.getInitParameter(SSOClientConstants.ConfigParam.LOGUT_URL_STR);
		if(logoutStr == null){
			throw new ServletException("Missing init parameter " + SSOClientConstants.ConfigParam.LOGUT_URL_STR);
		}
		setCookieStr = fConfig.getInitParameter(SSOClientConstants.ConfigParam.SET_COOKIE_URL_STR);
		if(setCookieStr == null){
			throw new ServletException("Missing init parameter " + SSOClientConstants.ConfigParam.SET_COOKIE_URL_STR);
		}
		String exclusionStr = fConfig.getInitParameter(SSOClientConstants.ConfigParam.EXCLUSIONS);
		if(exclusionStr == null){
			throw new ServletException("Missing init parameter " + SSOClientConstants.ConfigParam.EXCLUSIONS);
		}
        if (exclusionStr != null && !exclusionStr.isEmpty()) {
            String[] inputs = exclusionStr.split(SSOClientConstants.SEPARATOR);
            for (String input : inputs) {
                Pattern pattern = regexCompile(input.trim());
                if (pattern != null) {
                    exclusions.add(pattern);
                }
            }
        }
	}
	

	private Pattern regexCompile(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        String regex = input.replace("*", "(.*)").replace("?", "(.{1})");
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    private boolean isExcluded(String uri) {
        for (Pattern exclusion : exclusions) {
            Matcher matcher = exclusion.matcher(uri);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
}

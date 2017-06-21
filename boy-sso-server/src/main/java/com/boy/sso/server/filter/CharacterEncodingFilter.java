package com.boy.sso.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharacterEncodingFilter implements Filter{

	private String encoding;
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {	
        if(encoding==null){
            encoding = "UTF-8";//默认编码
        }
        request.setCharacterEncoding(encoding);//只能解决POST请求参数的中文问题
        response.setCharacterEncoding(encoding);//输出流编码
        response.setContentType("text/html;charset="+encoding);//输出流编码，通知了客户端应该使用的编码
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		encoding = filterConfig.getInitParameter("encoding");//用户可能忘记了配置该参数
	}

}

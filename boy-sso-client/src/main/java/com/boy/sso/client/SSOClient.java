package com.boy.sso.client;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boy.sso.client.constants.SSOClientConstants;

/**
 * sso客户端，提供一些基本的方法。
 * @author wesley
 *
 */
public class SSOClient {

	private static final Logger logger = LoggerFactory.getLogger(SSOClient.class);
	
	/** sso认证中心 */
	private static String ssoService; 
	/** cookike名  */
	private static String cookieName; 
	
	/**
	 * 校验票据
	 * @param request
	 * @param response
	 * @param chain
	 * @param ticket
	 * @param URL
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void authTicket(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Cookie ticket, String URL) throws IOException, ServletException {
		logger.info("SSOClient---authTicket----");
		NameValuePair[] params = new NameValuePair[2];
		params[0] = new NameValuePair(SSOClientConstants.ACTION, SSOClientConstants.AUTH_TICKET_ACTION);
		params[1] = new NameValuePair(SSOClientConstants.TICKET, ticket.getValue());
		logger.info("SSOClient---authTicket----params:"+params);
		try {
			JSONObject result = sendPost(request, response, chain, params);
			logger.info("SSOClient---authTicket----result:"+result);
			if(result.getBoolean("error")) {
				response.sendRedirect(URL);
			} else {
				request.setAttribute("username", result.get("username"));
				chain.doFilter(request, response);
			}
		} catch (JSONException e) {
			response.sendRedirect(URL);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 退出
	 * @param request
	 * @param response
	 * @param chain
	 * @param ticket
	 * @param URL
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void doLogout(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Cookie ticket, String URL) throws IOException, ServletException {
		logger.info("SSOClient---doLogout----");
		NameValuePair[] params = new NameValuePair[2];
		params[0] = new NameValuePair(SSOClientConstants.ACTION, SSOClientConstants.LOGOUT_ACTION);
		params[1] = new NameValuePair(SSOClientConstants.TICKET, ticket.getValue());
		try {
			JSONObject result = sendPost(request, response, chain, params);
			logger.info("SSOClient---doLogout----result:"+result);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		} finally {
			response.sendRedirect(URL);
		}
	}

	/**
	 * 设置浏览器端cookie(把认证中心返回的票据在本地生成一份)
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void setCookie(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Cookie ticket = new Cookie(cookieName, request.getParameter(SSOClientConstants.TICKET));
		ticket.setPath("/");
		ticket.setMaxAge(Integer.parseInt(request.getParameter(SSOClientConstants.EXPIRY)));
		response.addCookie(ticket);
		
		String gotoURL = request.getParameter(SSOClientConstants.GOTO_URL);
		if(gotoURL != null)
			response.sendRedirect(gotoURL);
	}
	
	/**
	 * 发送请求到认证中心
	 * @param request
	 * @param response
	 * @param chain
	 * @param params
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws JSONException
	 */
	private static JSONObject sendPost(HttpServletRequest request, HttpServletResponse response, FilterChain chain, NameValuePair[] params) throws IOException, ServletException, JSONException {
		logger.info("SSOClient---sendPost----");
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(ssoService);
		postMethod.addParameters(params);
		switch(httpClient.executeMethod(postMethod)) {
			case HttpStatus.SC_OK:
				JSONObject result = new JSONObject(postMethod.getResponseBodyAsString());
				logger.info("SSOClient---sendPost----result:"+result);
				return result;
			default:
				// 其它处理
				return null;
		}
	}
	
	public static void setSsoService(String service) {
		ssoService = service;
	}
	

	public static void setCookieName(String name) {
		cookieName = name;
	}
	
}

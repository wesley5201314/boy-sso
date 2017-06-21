package com.boy.sso.server.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.boy.sso.server.bean.Ticket;

public class SSOAuthConstants {

	/**------------动作指令------------**/
	public static final String LOGOUT_ACTION = "logout";
	
	public static final String LOGIN_ACTION = "login";
	
	public static final String PRE_LOGIN_ACTION = "preLogin";

	public static final String AUTH_TICKET_ACTION = "authTicket";
	
	public static final String REGISTER_ACTION = "register";
	
	/**--------------请求参数-----------------**/
	public static final String ACTION = "action";
	
	public static final String SET_COOKIE_URL = "setCookieURL";
	
	public static final String GOTO_URL = "gotoURL";
	
	public static final String AUTO_AUTH = "autoAuth";
	
	public static final String TICKET = "ticket";
	
	public static final String USER_NAME = "username";
	
	public static final String PASSWORD = "password";
	
	/**----------初始化配置参数---------**/
	public static class ConfigParam{
		
		public static final String SECURE = "secure";
		
		public static final String COOKIE_NAME = "cookieName";
		
		public static final String SECURE_KEY = "secretKey";
		
		public static final String TICKET_TIMEOUT = "ticketTimeout";

	}
	
	/** 单点登录标记 */
	public static final Map<String, Ticket> tickets  = new ConcurrentHashMap<String, Ticket>();

	
}

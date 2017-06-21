package com.boy.sso.client.constants;

public class SSOClientConstants {
	/**-------动作指令-------**/
	public static final String LOGOUT_ACTION = "logout";
	
	public static final String AUTH_TICKET_ACTION = "authTicket";
	
	/**---------请求参数----------**/
	public static final String ACTION = "action";
	
	public static final String TICKET = "ticket";
	
	public static final String GOTO_URL = "gotoURL";
	
	public static final String EXPIRY = "expiry";

	/**-----过滤规则分隔符------**/
	public static final String SEPARATOR = ",";
	
	/**-------初始化参数配置-------**/
	public static class ConfigParam {
		public static final String SSO_SERVICE = "ssoService";
		
		public static final String COOKIE_NAME = "cookieName";
		
		public static final String LOGUT_URL_STR = "logoutStr";
		
		public static final String SET_COOKIE_URL_STR = "setCookieStr";

		public static final String EXCLUSIONS = "exclusions";
    }
	
}

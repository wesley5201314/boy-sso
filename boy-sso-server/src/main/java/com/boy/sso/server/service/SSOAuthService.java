package com.boy.sso.server.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boy.sso.server.bean.RecoverTicket;
import com.boy.sso.server.bean.Ticket;
import com.boy.sso.server.constants.SSOAuthConstants;
import com.boy.sso.server.util.DESUtils;

/**
 * SSOAuthService
 * @author wesley
 *
 */
public class SSOAuthService {
	
	private static final Logger logger = LoggerFactory.getLogger(SSOAuthService.class);
	
	/** cookie名称 */
	private static String cookieName;
	
	/** 是否安全协议 */
	private static boolean secure;
	
	/** 密钥 */
	private static String secretKey;
	
	/** ticket有效时间 */
	private static int ticketTimeout;
	
	/** 账户信息 */
	private static Map<String, String> accounts;
	
	public static void InitUser(){
		logger.info("SSOAuthService: InitUser()");
		accounts = new HashMap<String, String>();
		accounts.put("zhangsan", "zhangsan");
		accounts.put("lisi", "lisi");
		accounts.put("wangwu", "wangwu");
	}
	
	/**
	 * 创建一个ScheduledExecutorService
	 * @return
	 */
	public static ScheduledExecutorService createScheduledExecutorService(){
		logger.info("SSOAuthService: createScheduledExecutorService()");
		ScheduledExecutorService schedulePool = Executors.newScheduledThreadPool(1);
		schedulePool.scheduleAtFixedRate(new RecoverTicket(SSOAuthConstants.tickets), ticketTimeout * 60, 1, TimeUnit.MINUTES);
		return schedulePool;
	}

	
	public static void preLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("SSOAuthService: preLogin()");
		Cookie ticket = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null)
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals(cookieName)) {
					ticket = cookie;
					break;
				}
			}
		if(ticket == null) {
			request.getRequestDispatcher("login.jsp").forward(request, response);
		} else {
			String encodedTicket = ticket.getValue();
			String decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
			if(SSOAuthConstants.tickets.containsKey(decodedTicket)) {
				String setCookieURL = request.getParameter(SSOAuthConstants.SET_COOKIE_URL);
				String gotoURL = request.getParameter(SSOAuthConstants.GOTO_URL);
				if(setCookieURL != null)
	                response.sendRedirect(setCookieURL + "?ticket=" + encodedTicket + "&expiry=" + ticket.getMaxAge() + "&gotoURL=" + gotoURL);
			} else {
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}
		}
	}

	public static void authTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("SSOAuthService: authTicket()");
		StringBuilder result = new StringBuilder("{");
		PrintWriter out = response.getWriter();
		String encodedTicket = request.getParameter(SSOAuthConstants.TICKET);
		if(encodedTicket == null) {
			result.append("\"error\":true,\"errorInfo\":\"Ticket can not be empty!\"");
		} else {
			String decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
			if(SSOAuthConstants.tickets.containsKey(decodedTicket))
				result.append("\"error\":false,\"username\":").append(SSOAuthConstants.tickets.get(decodedTicket).getUsername());
			else
				result.append("\"error\":true,\"errorInfo\":\"Ticket is not found!\"");
		}
		result.append("}");
		out.print(result);
	}

	public static void doLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("SSOAuthService: doLogout()");
		StringBuilder result = new StringBuilder("{");
		PrintWriter out = response.getWriter();
		String encodedTicket = request.getParameter(SSOAuthConstants.TICKET);
		if(encodedTicket == null) {
			result.append("\"error\":true,\"errorInfo\":\"Ticket can not be empty!\"");
		} else {
			String decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
			SSOAuthConstants.tickets.remove(decodedTicket);
			result.append("\"error\":false");
		}
		result.append("}");
		out.print(result);
	}

	public static void doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		logger.info("SSOAuthService: doLogin()");
		String username = request.getParameter(SSOAuthConstants.USER_NAME);
		String password = request.getParameter(SSOAuthConstants.PASSWORD);
		String pass = accounts.get(username);
		if(pass == null || !pass.equals(password)) {
			request.getRequestDispatcher("login.jsp?errorInfo=username or password is wrong!").forward(request, response);
		} else {
			String ticketKey = UUID.randomUUID().toString().replace("-", "");
			String encodedticketKey = DESUtils.encrypt(ticketKey, secretKey);
			
			Timestamp createTime = new Timestamp(System.currentTimeMillis());
			Calendar cal = Calendar.getInstance();
			cal.setTime(createTime);
			cal.add(Calendar.MINUTE, ticketTimeout);
			Timestamp recoverTime = new Timestamp(cal.getTimeInMillis());
			Ticket ticket = new Ticket(username, createTime, recoverTime);
			
			SSOAuthConstants.tickets.put(ticketKey, ticket);
			//是否记住用户名
			String[] checks = request.getParameterValues(SSOAuthConstants.AUTO_AUTH);
			int expiry = -1;
			if(checks != null && "1".equals(checks[0]))
				expiry = 7 * 24 * 3600;
			Cookie cookie = new Cookie(cookieName, encodedticketKey);
			cookie.setSecure(secure);// 为true时用于https
			cookie.setMaxAge(expiry);
			cookie.setPath("/");
			response.addCookie(cookie);

			String setCookieURL = request.getParameter(SSOAuthConstants.SET_COOKIE_URL);
			String gotoURL = request.getParameter(SSOAuthConstants.GOTO_URL);
			
			PrintWriter out = response.getWriter();
			out.print("<script type='text/javascript'>");
			out.print("document.write(\"<form id='url' method='post' action='" + setCookieURL + "'>\");");
			out.print("document.write(\"<input type='hidden' name='gotoURL' value='" + gotoURL + "' />\");");
			out.print("document.write(\"<input type='hidden' name='ticket' value='" + encodedticketKey + "' />\");");
			out.print("document.write(\"<input type='hidden' name='expiry' value='" + expiry + "' />\");");
			out.print("document.write('</form>');");
			out.print("document.getElementById('url').submit();");
			out.print("</script>");
		}
	}
	
	public static void register(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		logger.info("SSOAuthService: doLogin()");
		String username = request.getParameter(SSOAuthConstants.USER_NAME);
		String password = request.getParameter(SSOAuthConstants.PASSWORD);
		String repassword = request.getParameter("repassword");
		if(password == null || !repassword.equals(password)) {
			request.getRequestDispatcher("register.jsp?errorInfo=username or password is wrong!").forward(request, response);
		} else {
			accounts.put(username, password);
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}
	
	public static void setCookieName(String name) {
		cookieName = name;
	}
	
	public static void setSecure(boolean sec) {
		secure = sec;
	}

	public static void setSecretKey(String secKey) {
		secretKey = secKey;
	}
	
	public static void setTicketTimeout(int timeout) {
		ticketTimeout = timeout;
	}

}

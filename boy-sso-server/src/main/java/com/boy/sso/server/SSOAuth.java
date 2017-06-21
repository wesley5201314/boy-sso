package com.boy.sso.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boy.sso.server.constants.SSOAuthConstants;
import com.boy.sso.server.service.SSOAuthService;

/**
 * Servlet implementation class SSOAuth
 */
public class SSOAuth extends HttpServlet {
	
	private static final Logger logger = LoggerFactory.getLogger(SSOAuth.class);
	
	private static final long serialVersionUID = 1L;
	/** cookie名称 */
	private String cookieName;
	
	/** 是否安全协议 */
	private boolean secure;
	
	/** 密钥 */
	private String secretKey;
	
	/** ticket有效时间 */
	private int ticketTimeout;
	
	/** 回收ticket线程池 */
	private ScheduledExecutorService schedulePool;
	
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		logger.info("-----SSO service init----");
		cookieName = config.getInitParameter(SSOAuthConstants.ConfigParam.COOKIE_NAME);
		if(cookieName == null){
			logger.error("Missing init parameter " + SSOAuthConstants.ConfigParam.COOKIE_NAME);
			throw new ServletException("Missing init parameter " + SSOAuthConstants.ConfigParam.COOKIE_NAME);
		}
		SSOAuthService.setCookieName(cookieName);
		if(config.getInitParameter(SSOAuthConstants.ConfigParam.SECURE) == null){
			logger.error("Missing init parameter " + SSOAuthConstants.ConfigParam.SECURE);
			throw new ServletException("Missing init parameter " + SSOAuthConstants.ConfigParam.SECURE);
		}
		secure = Boolean.parseBoolean(config.getInitParameter(SSOAuthConstants.ConfigParam.SECURE));
		SSOAuthService.setSecure(secure);
		secretKey = config.getInitParameter(SSOAuthConstants.ConfigParam.SECURE_KEY);
		if(secretKey == null){
			logger.error("Missing init parameter " + SSOAuthConstants.ConfigParam.SECURE_KEY);
			throw new ServletException("Missing init parameter " + SSOAuthConstants.ConfigParam.SECURE_KEY);
		}
		SSOAuthService.setSecretKey(secretKey);
		if(config.getInitParameter(SSOAuthConstants.ConfigParam.TICKET_TIMEOUT) == null){
			logger.error("Missing init parameter " + SSOAuthConstants.ConfigParam.TICKET_TIMEOUT);
			throw new ServletException("Missing init parameter " + SSOAuthConstants.ConfigParam.TICKET_TIMEOUT);
		}
		ticketTimeout = Integer.parseInt(config.getInitParameter(SSOAuthConstants.ConfigParam.TICKET_TIMEOUT));
		SSOAuthService.setTicketTimeout(ticketTimeout);
		schedulePool = SSOAuthService.createScheduledExecutorService();
		SSOAuthService.InitUser();
		logger.info("-----SSO service init end----");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String action = request.getParameter(SSOAuthConstants.ACTION);
		if(SSOAuthConstants.PRE_LOGIN_ACTION.equals(action)) {
			logger.info("----Client request action: perLogin-----");
			SSOAuthService.preLogin(request, response);
		} else if(SSOAuthConstants.LOGIN_ACTION.equals(action)) {
			logger.info("----Server request action: login-----");
			SSOAuthService.doLogin(request, response);
		} else if(SSOAuthConstants.LOGOUT_ACTION.equals(action)) {
			logger.info("----Client request action: logout-----");
			SSOAuthService.doLogout(request, response);
		} else if(SSOAuthConstants.AUTH_TICKET_ACTION.equals(action)) {
			logger.info("----Client request action: authTicket-----");
			SSOAuthService.authTicket(request, response);
		} else if(SSOAuthConstants.REGISTER_ACTION.equals(action)){
			SSOAuthService.register(request,response);
		} else {
			logger.error("Action can not be empty！");
			out.print("Action can not be empty！");
		}
		out.close();
	}

	@Override
	public void destroy() {
		if(schedulePool != null)    schedulePool.shutdown();
	}
}

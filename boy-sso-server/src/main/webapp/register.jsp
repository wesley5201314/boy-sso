<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>统一认证中心</title>
<link rel="stylesheet" href="${pageContext.request.contextPath }/css/reset.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath }/css/common.css" />
</head>
<body>
	<div class="wrap login_wrap">
		<div class="content">

			<div class="logo"></div>

			<div class="login_box">

				<div class="login_form">
					<div class="login_title">注册</div>
					<form action="${pageContext.request.contextPath }/SSOAuth"
						method="post">
						<input name="action" type="hidden" value="register">
						<div class="ececk_warning" style="display: block;">
							<span>${param.errorInfo }</span>
						</div>
						<div class="form_text_ipt">
							<input name="username" type="text" placeholder="用户名">
						</div>
						<div class="ececk_warning">
							<span>请输入用户名</span>
						</div>
						<div class="form_text_ipt">
							<input name="password" type="password" placeholder="密码">
						</div>
						<div class="ececk_warning">
							<span>请输入密码</span>
						</div>
						<div class="form_text_ipt">
							<input name="repassword" type="password" placeholder="重复密码">
						</div>
						<div class="ececk_warning">
							<span>请再次输入密码</span>
						</div>
						<!-- <div class="form_text_ipt">
							<input name="code" type="text" placeholder="验证码">
						</div>
						<div class="ececk_warning">
							<span>数据不能为空</span>
						</div> -->
						<div class="form_btn">
							<button type="button">注册</button>
						</div>
						<div class="form_reg_btn">
							<span>已有帐号？</span><a
								href="${pageContext.request.contextPath }/login.jsp">马上登录</a>
						</div>
					</form>
					<div class="other_login">
						<div class="left other_left">
							<span>其它登录方式</span>
						</div>
						<div class="right other_right">
							<a href="#">QQ登录</a> <a href="#">微信登录</a> <a href="#">微博登录</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath }/js/common.js"></script>
</body>
</html>
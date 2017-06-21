<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Application System&nbsp;-&nbsp;homepage</title>
	</head>
	<body>
		<center><h1>Application System 2</h1></center>
		<hr/>
		<center><font color="green" size="35">${username }</font>,Welcome to sso application example2!</center>
		<hr/>
		<a href="${pageContext.request.contextPath }/logout?gotoURL=${pageContext.request.requestURL }">Logout</a>
	</body>
</html>
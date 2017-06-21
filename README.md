# boy-sso
# 单点登录系统 #

## 简单使用方法 ##
1、boy-sso-example项目中直接在web.xml中添加如下代码，需要注意的是需要依赖boy-sso-client这个项目。

		<filter>
			<filter-name>SSOAuth</filter-name>
			<filter-class>com.boy.sso.client.SSOFilter</filter-class>
			<init-param>
				<!-- 认证系统服务 -->
				<param-name>ssoService</param-name>
				<param-value>http://passport.com:8080/boy-sso-server/SSOAuth</param-value>
			</init-param>
			<init-param>
				<!-- 认证系统ticket名称 -->
				<param-name>cookieName</param-name>
				<param-value>boy-sso-client-id</param-value>
			</init-param>
			<init-param>
				<!-- 认证登出 -->
				<param-name>logoutStr</param-name>
				<param-value>/logout</param-value>
			</init-param>
			<init-param>
				<!-- 认证本地设置cookie -->
				<param-name>setCookieStr</param-name>
				<param-value>/setCookie</param-value>
			</init-param>
			<init-param>
				<!-- 过滤规则 -->
				<param-name>exclusions</param-name>
				<param-value>*/images/*,*/css/*,*/favicon.ico,*/js/*</param-value>
			</init-param>
		</filter>
		<filter-mapping>
			<filter-name>SSOAuth</filter-name>
			<url-pattern>/*</url-pattern>
		</filter-mapping>

2、部署运行boy-sso-server这个项目。

效果图：
## 登录页面 ##
![](http://i.imgur.com/TUu9XXy.png)

![](http://i.imgur.com/4IAjz05.png)

## 登录成功 ## （单个系统）

![](http://i.imgur.com/Jtc9M9W.png)

![](http://i.imgur.com/fVJOTO3.png)

## 登录成功 ## （多个系统）

![](http://i.imgur.com/J8miQ2D.png)

![](http://i.imgur.com/8cxlvNv.png)

![](http://i.imgur.com/ePlhdwB.png)

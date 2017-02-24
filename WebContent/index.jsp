<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String path = request.getContextPath();
	// 获得本项目的地址(例如: http://localhost:8080/MyApp/)赋值给basePath变量    
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
	// 将 "项目路径basePath" 放入pageContext中，待以后用EL表达式读出。    
	pageContext.setAttribute("basePath", basePath);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>yxe_splider</title>
</head>
<body>

	<p>编辑用户名:${biz_man}</p>
	<p>编辑tag:${biz_man_tag}</p>
	
		
	<br>
	<br>
	<br>
	<br>
	<h2>手动分类抓取(尚未完善,请勿使用)</h2>
	
	<!-- <form action="${basePath}/sayHello" method="post"> -->

		<select name="merchant_name">
			<c:forEach var="merchant" items="${merchants}" varStatus="status">
				<c:choose>
					<c:when test="${merchant.url== 'http://www.amazon.com'}">
						<option value="${merchant.url}" selected="selected">${merchant.name}</option>
					</c:when>
					<c:otherwise>
						<option value="${merchant.url}">${merchant.name}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
		
		<select name="goods_type">
			<c:forEach var="goodtype" items="${goods_types}" varStatus="status">
				<option value="${goodtype.id}">${goodtype.name}</option>
			</c:forEach>
		</select>

		<input type="button" value="comfirm" />
	<!-- </form> -->
	
	<br>
	<br>
	<br>
	<br>
	<h2>定时任务</h2><b><font color="#D50000">${start_job_info}</font></b>
	
	<form action="${basePath}/startJob" method="post">
		<c:choose>
			<c:when test="${is_test== 'on'}">
				测试模式:<input type="checkbox" name="is_test" checked="checked"/><br>
			</c:when>
			<c:otherwise>
				测试模式:<input type="checkbox" name="is_test"/><br>
			</c:otherwise>
		</c:choose>
		任务重复执行间隔时间:<input type="text" name="gap_time" value="${gap_time}"/><br>
		<p>勾选测试模式后每种类型的商品只抓取一件.</p>
		<input type="submit" value="点击开始定时任务" />
	</form>
	
	<br>
	<br>
	<form action="${basePath}/stopJob" method="post">
		<input type="hidden" name="gap_time" value="${gap_time}"/>
		<input type="submit" value="点击关闭定时任务" />
	</form>
	
	
</body>
</html>
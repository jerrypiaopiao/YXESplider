<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
<title>类型匹配编辑</title>
</head>
<body>
	<p><a href="${basePath}">返回</a></p>
	<table border="1">
		<tr>
			<th>id</th>
			<th>目标网站id(比如55haitao对应4)</th>
			<th>过滤词</th>
			<th>修改</th>
		</tr>
		
		<c:forEach var="splider_filter_word" items="${splider_filter_words}" varStatus="status">
			<tr>
				<td align="center">${splider_filter_word.id}</td>
				<td align="center">${splider_filter_word.h_src_id}</td>
				<td align="center">${splider_filter_word.h_filter_words}</td>
				<td align="center"><a href="gotoChangeFilterWord?opt=2&id=${splider_filter_word.id}">修改</a></td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="4"><a href="gotoChangeFilterWord?opt=1">添加过滤词</a></td>
		</tr>
	</table>
</body>
</html>
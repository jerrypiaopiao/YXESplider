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
	<form action="${basePath}/changeFilterWord" method="post">
		<input type="hidden" name="change_type" value="${change_type}">
		<c:choose>
			<c:when test="${change_type== 1}">
				<p>
					选择目标网站:
					<select name="h_src_id">
					<c:forEach var="target_info" items="${target_infos}" varStatus="status">
						<c:set value="${fn:substringAfter(target_info, '_')}" var="tmp_target_id" />
						<option value="${tmp_target_id}">${target_info}</option>
					</c:forEach>
					</select>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					过滤词:<input type="text" name="h_filter_words" value="" /><span>多个词组以","逗号隔开</span>					
				</p>
				<input type="submit" value="添加" />
			</c:when>
			<c:when test="${change_type== 2}">
				<p>过滤词组id:<input type="text" name="id" value="${filter_word.id}"></p>
				<p>目标网站id:<input type="text" name="h_src_id" value="${filter_word.h_src_id}"><span>多个词组以","逗号隔开</span></p>
				<p>商家对应:<input type="text" name="h_filter_words" value="${filter_word.h_filter_words}"></p>
				<input type="submit" value="修改" />
			</c:when>
			<c:otherwise>
				<p><a href="${basePath}">无相应操作,返回</a></p>
			</c:otherwise>
		</c:choose>
	</form>
</body>
</html>
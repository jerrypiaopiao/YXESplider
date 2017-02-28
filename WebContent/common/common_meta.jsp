<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
	String path = request.getContextPath();
	// 获得本项目的地址(例如: http://localhost:8080/MyApp/)赋值给basePath变量    
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+ request.getServerPort()+path+"/";
	// 将 "项目路径basePath" 放入pageContext中，待以后用EL表达式读出。    
	pageContext.setAttribute("basePath", basePath);
%>
<meta charset="UTF-8">
<base href="<%=basePath%>">
<meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>
<link rel="stylesheet" href="css/jquery.mobile-1.4.5.min.css" />
<script type="text/javascript" charset="utf-8" src="js/jquery-3.1.1.js"></script>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
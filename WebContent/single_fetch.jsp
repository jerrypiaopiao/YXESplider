<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String path = request.getContextPath();
	// 获得本项目的地址(例如: http://localhost:8080/MyApp/)赋值给basePath变量    
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	// 将 "项目路径basePath" 放入pageContext中，待以后用EL表达式读出。    
	pageContext.setAttribute("basePath", basePath);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>hello</title>
<script type="text/javascript">
function load(){
	/*var code= document.getElementById('code').value;
	code = code.replace(/^eval/, '');
	var decode = eval(code);
	var trueLink = decode.substring(decode.indexOf("smzdmhref='"));
	//trueLink = trueLink.substring("smzdmhref='".length, trueLink.indexOf("';ga"));
	trueLink = trueLink.substring("smzdmhref='".length, trueLink.indexOf("';smzdmhref1"));*/
	document.getElementById('code').value = "hello world";
}
</script>
</head>
<body>
	<h2>定时任务</h2><b><font color="#D50000">${fetch_message}</font></b>
</body>
</html>
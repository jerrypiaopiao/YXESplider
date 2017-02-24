<%@page import="cn.yxeht.app.utils.TextUtil"%>
<%@page import="org.jsoup.select.Elements"%>
<%@page import="org.jsoup.nodes.Element"%>
<%@page import="org.jsoup.nodes.Document"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java"  import="java.io.*, java.net.*, org.jsoup.*" %>
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
<title>decode</title>

<%
	String GET_URL = (String)request.getAttribute("detail_link");
	System.out.println("---->GET_URL:"+GET_URL);
	if(TextUtil.isEmpty(GET_URL)){
		GET_URL = request.getParameter("detail_link");
	}
	
	String s = "";
	
	if(!TextUtil.isEmpty(GET_URL)){
		StringBuilder sb = new StringBuilder();
		try {
			
			URL url = new URL(GET_URL); // 把字符串转换为URL请求地址
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
			connection.connect();// 连接会话
			// 获取输入流
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			
			while ((line = br.readLine()) != null) {// 循环读取流
				sb.append(line);
			}
			
			Document doc =Jsoup.parse(sb.toString());
			Elements elements = doc.getElementsByTag("script");
			if(elements.size() == 1){
				s = elements.get(0).html();
				s = s.substring(s.lastIndexOf("eval(function"), s.length());
			}
			
			System.out.println(s);
			
			br.close();// 关闭流
			connection.disconnect();// 断开连接
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	%>

</head>
<body>
	<textarea id=code cols=80 rows=20 style="display: none;"><%=s%></textarea>
	<span id="truelink"></span>
	<!-- <form id="saveData" action="${basePath}/save_smzdm_good" method="post">
		<input type="text" name="goodTitle" value="${goodDetail.goodTitle}"><br/>
		<input type="text" name="goodContent" value="${goodDetail.goodContent}"><br/>
		<input type="text" name="goodSrcLink" value="${goodDetail.goodSrcLink}"><br/>
		<input type="text" id="yxehtLink" name="yxehtLink" value="${goodDetail.yxehtLink}"><br/>
		<input type="text" name="imgLinks" value="${goodDetail.mImgLinks}"><br>
		<input type="hidden" name="goodType" value="${goodDetail.type}">
	</form> -->
	
	<script type="text/javascript">
		var code= document.getElementById('code').value;
		code = code.replace(/^eval/, '');
		var decode = eval(code);
		var trueLink = decode.substring(decode.indexOf("smzdmhref='"));
		trueLink = trueLink.substring("smzdmhref='".length, trueLink.indexOf("';ga"));
		//document.getElementById('code').innerText = trueLink;
		document.getElementById('truelink').innerText = trueLink;
		//document.getElementById("yxehtLink").value = trueLink;
		//$(document).ready(function(){
			//$("#saveData").submit();
		//});
		
	</script>
</body>
</html>
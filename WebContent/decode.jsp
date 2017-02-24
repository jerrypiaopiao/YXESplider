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
			
			br.close();// 关闭流
			connection.disconnect();// 断开连接
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	%>
<script type="text/javascript">
function decode(){
	var code= document.getElementById('code').value;
	code = code.replace(/^eval/, '');
	var decode = eval(code);
	var trueLink = decode.substring(decode.indexOf("smzdmhref='"));
	//trueLink = trueLink.substring("smzdmhref='".length, trueLink.indexOf("';ga"));
	trueLink = trueLink.substring("smzdmhref='".length, trueLink.indexOf("';smzdmhref1"));
	var linkStart = trueLink.indexOf("http");
	if(linkStart == -1){
		var tmpTrueLink = decode.substring(decode.indexOf("smzdmhref1='"));
		tmpTrueLink = tmpTrueLink.substring("smzdmhref1='".length, tmpTrueLink.indexOf("';ga"));
		document.getElementById('truelink').innerText = tmpTrueLink;
	}else{
		document.getElementById('truelink').innerText = trueLink;
	}
	
}
</script>
</head>
<%
	if(!TextUtil.isEmpty(GET_URL)){
		String url = TextUtil.replaceFirstHttpInUrl(GET_URL)[1];
		System.out.println("================>url:"+url);
		if(url.startsWith("www.amazon")){
			s = GET_URL;
			out.write("<body>");
			out.write("<textarea id=code cols=80 rows=20 style=\"display: none;\">");
			out.write(s);
			out.write("</textarea>");
			out.write("<span id=\"truelink\">");
			out.write(s);
			out.write("</span>");
			out.write("</body>");
		}else{
			out.write("<body onload=\"decode()\">");
			out.write("<textarea id=code cols=80 rows=20 style=\"display: none;\">");
			out.write(s);
			out.write("</textarea>");
			out.write("<span id=\"truelink\">");
			out.write("</span>");
			out.write("</body>");
		}	
	}
%>
</html>
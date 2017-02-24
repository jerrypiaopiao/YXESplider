<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@include file="/common/common_meta.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>YXESplider_INDEX</title>
<script type="text/javascript" charset="utf-8" src="js/yxe.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		  //$("#type_match_rule").click(function(){
			//  getTypeMatchRule();
		  //});
		  $("#load_target_web").click(function(){
			  getTargetWebSite();
		  });
		  $("#target_merchant").change(function(){
			  getMerInfos($("#target_merchant option:selected").val());
		  });
		  $("#merchant_name").change(function(){
			  //alert($("#merchant_name option:selected").val());
			  startFetch($("#target_merchant option:selected").val(), $("#merchant_name option:selected").val());
		  });
	});
</script>
</head>
<body>
	<p>当前项目根目录:${realPath}</p>
	<p><button id="load_target_web">加载目标网站信息</button></p>
	<p id="target_merchant_show"></p>
	<p id="merhant_show">
		抓取目标站点:
		<select id="target_merchant">
			<option>--</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		商家选择:
		<select id="merchant_name">
			<option>--</option>
		</select>
	</p>
	<p><button id="type_match_rule">加载分类列表信息</button></p>
	<p id="type_match_rule_show"></p>
	<table id="type_match_rule_show_table" border="1">
		<tr>
		<th align="center">yxe_type_id</td>
		<th align="center">yxe_type_name</td>
		<th align="center" style="width: 120px">对应分类信息</td>
		<th align="center">修改</td>
		</tr>
	</table>
	
	<%-- <p id="ds"></p>--%>
	<%--@include file="/common/common_footer.jsp"
	<%--<form action="${basePath}startFetchOnQuartz" method="post"> --%>
	<form action="${basePath}startFetchV2" method="post">
		间隔时间:<input type="text" name="gap_time" value="45"/><br/>
		是否刷新目标网站缓存:<input type="checkbox" name="is_refresh"></br>
		是否立即运行:<input type="checkbox" name="is_run_now" checked="checked"></br>
		选择目标网站:<select name="target_web">
			<option>--</option>
			<c:forEach var="target_web" items="${target_web_info}" varStatus="status">
						<option value="${target_web}">${target_web}</option>
			</c:forEach>
		</select></br>
		<input type="submit" value="开始抓取"/>
	</form>
</body>
<script type="text/javascript">
	getTypeMatchRule();
</script>
</html>
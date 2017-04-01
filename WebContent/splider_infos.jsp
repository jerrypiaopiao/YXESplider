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
		  $("#splider_fetch_state").change(function(){
			  //startFetch($("#target_merchant option:selected").val(), $("#merchant_name option:selected").val());
			  alert($(this).children('option:selected').val()); 
			  var p1=$(this).children('option:selected').val();//这就是selected的值
			  if(p1){
				  window.location.href="getAllSpliderInfo?fetch_state="+p1+"";//页面跳转并传参  
			  }
			 
		  });
	});
</script>
</head>
<body>
	<table id="splider_infos_table" border="1">
		<tr>
			<td><a href="${basePath}">返回</a></td>
			<td colspan="7">
				根据抓取状态筛选:
			<select id="splider_fetch_state" name="splider_fetch_state">
						<option>--</option>
						<option value="0">未抓取</option>
						<option value="1">抓取成功</option>
						<option value="2">抓取失败</option>
			</select>
			</td>
		</tr>
		<tr>
		<th align="center">商品链接ID</th>
		<th align="center">商品来源</th>
		<th align="center">商品链接</th>
		<th align="center">抓取状态</th>
		<th align="center" style="width: 120px">抓取状态信息</th>
		<th align="center">创建时间</th>
		<th align="center">更新时间</th>
		<th align="center">操作</th>
		</tr>
		<c:forEach var="splider_infos" items="${splider_infos}" varStatus="status">
		<tr>
			<td>${splider_infos.id}</td>
			<td>${splider_infos.h_rule_name}</td>
			<td>${splider_infos.h_good_link}</td>
			<td>
			<c:if test="${splider_infos.h_catch_state==0 }">
				未抓取
			</c:if>
			<c:if test="${splider_infos.h_catch_state==1 }">
				抓取成功
			</c:if>
			<c:if test="${splider_infos.h_catch_state==2 }">
				抓取失败
			</c:if>
			</td>
			<td>${splider_infos.h_catch_reson}</td>
			<td>${splider_infos.h_create_time}</td>
			<td>${splider_infos.h_update_time}</td>
			<td>
			<c:if test="${splider_infos.h_catch_state==0 }">
				<a href="singleFetch?fetch_url=${splider_infos.h_good_link}">抓取</a>
			</c:if>
			<c:if test="${splider_infos.h_catch_state!=0 }">
				--
			</c:if>
			</td>
			</tr>
		</c:forEach>
	</table>
	
</body>
</html>
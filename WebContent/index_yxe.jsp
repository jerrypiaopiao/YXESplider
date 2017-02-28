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
		  $("#handle_fetch").click(function(){
			  var variable1 = $("#target_merchant option:selected").val();
			  alert(variable1);
			  if (variable1 !== null || variable1 !== undefined || variable1 !== '') { 
				 startFetch($("#target_merchant option:selected").val(), $("#merchant_name option:selected").val());	
			}else{
				alert("请配置目标网站信息!");
			}
		  });
		  
		  $("#merchant_name").change(function(){
			  //startFetch($("#target_merchant option:selected").val(), $("#merchant_name option:selected").val());
		  });
	});
</script>
</head>
<body>
<table border="1">
	<tr>
		<td>项目根目录</td>
		<td colspan="2">${realPath}</td>
	</tr>
	<tr>
		<td><button id="load_target_web">加载目标网站信息</button></td>
		<td>
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
		</td>
		<td>
			<input type="button" id="handle_fetch" value="开始手动抓取"/>
		</td>
	</tr>
	<tr>
		<td colspan="3"><p id="target_merchant_show"></p></td>
	</tr>
	<tr>
		<td colspan="3"><button id="type_match_rule">加载分类列表信息</button></td>
	</tr>
</table>
	
	
	<p id="type_match_rule_show"></p>
	
	<form action="${basePath}startFetchV2" method="post">
		<table border="1">
			<tr>
				<td colspan="2">定时抓取</td>
			</tr>
			<!-- <tr>
				<td>间隔时间</td>
				<td><input type="text" name="gap_time" value="45"/></td>
			</tr> -->
			<tr>
				<td>是否刷新目标网站缓存</td>
				<td><input type="checkbox" name="is_refresh"></td>
			</tr>
			<tr>
				<td>是否立即运行</td>
				<td><input type="checkbox" name="is_run_now" checked="checked"></td>
			</tr>
			<!-- <tr>
				<td>选择目标网站</td>
				<td>
					<select name="target_web" >
						<option>--</option>
						<c:forEach var="target_web" items="${target_web_info}" varStatus="status">
							<option value="${target_web}">${target_web}</option>
						</c:forEach>
					</select>
				</td>
			</tr> -->
			<tr>
				<td colspan="2">
					<input type="submit" value="开始抓取"/>
				</td>
			</tr>
		</table>
	</form>
	
	<p><a href="loadFilterWord">关键词过滤规则管理</a></p>
	<p><input type="button" name="filter_word_mgr" value="关键词过滤规则管理" onclick="javascript:window.location.href='loadFilterWord'"/></p>
	<table id="type_match_rule_show_table" border="1">
		<tr>
		<th align="center">洋小二分类ID</th>
		<th align="center">洋小二分类名称</th>
		<th align="center">目标网站</th>
		<th align="center" style="width: 120px">对应分类信息</th>
		<th align="center">修改</th>
		</tr>
	</table>
	
</body>
<script type="text/javascript">
	getTypeMatchRule();
</script>
</html>
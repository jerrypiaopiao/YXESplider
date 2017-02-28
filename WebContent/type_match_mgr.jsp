<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/common/common_meta.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>类型匹配编辑</title>
<script type="text/javascript" charset="utf-8" src="js/yxe.js"></script>
<script type="text/javascript">

	function showYXETypeName(){
		 var type_name =  $("#yxe_type_id").find("option:selected").text();
		  $("#good_type_name").val(type_name);
	}
	
	function loadTargetName(){
		var target_name = $("#target_web_id").find("option:selected").text();
		 $("#target_web_name").val(target_name);
	}

	$(document).ready(function(){
		  $("#yxe_type_id").change(function(){
			  showYXETypeName();
		  });
		  $("#target_web_id").change(function(){
			  loadTargetName();
		  });
	});
</script>
</head>
<body>
	<p><a href="${basePath}">返回</a></p>
	<form action="${basePath}changeTypeMatch" method="post">
		<input type="hidden" name="change_type" value="${change_type}">
		<c:choose>
			<c:when test="${change_type== 1}">
				<p>
					选择洋小二分类:
					<select id="yxe_type_id" name="yxe_type_id">
					<c:forEach var="good_type" items="${goods_types}" varStatus="status">
						<option value="${good_type.id}">${good_type.name}</option>
					</c:forEach>
					</select>
					
					<input type="hidden" id="good_type_name" name="good_type_name"/>
					</p>
					<p>
					选择目标网站:
					<select name="target_web_id" id="target_web_id">
					<c:forEach var="target_info" items="${target_infos}" varStatus="status">
						<c:set value="${fn:substringAfter(target_info, '_')}" var="tmp_target_id" />
						<option value="${tmp_target_id}">${target_info}</option>
					</c:forEach>
					</select>
					<input type="hidden" id="target_web_name" name="target_web_name"/>
					</p>
					<p>目标网站分类词组:<textarea rows="15" cols="45" name="src_type_str">${good_type.h_src_type_str}</textarea><span>多个词组以","逗号隔开</span></p>
					<!-- <input type="text" name="src_type_str" value="${good_type.h_src_type_str}"> -->
				<input type="submit" value="添加" />
			</c:when>
			<c:when test="${change_type== 2}">
				<!-- <p>洋小二分类id:<input type="text" name="yxe_type_id" value="${good_type.h_yxe_type_id}" readonly="readonly"></p> -->
				<p>洋小二分类:
					<select id="yxe_type_id" name="yxe_type_id">
						<c:forEach var="good_type_obj" items="${goods_types}" varStatus="status">
							<c:choose>
								<c:when test="${good_type.h_yxe_type_id == good_type_obj.id}">
									<option value="${good_type_obj.id}" selected="selected">${good_type_obj.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${good_type_obj.id}">${good_type_obj.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select></p>
				<input type="hidden" id="good_type_name" name="good_type_name"/>
				<!-- <p>商家对应:<input type="text" name="target_web_id" value="${good_type.h_src_id}"></p> -->
				目标网站:
					<select name="target_web_id" id="target_web_id">
					<c:forEach var="target_info" items="${target_infos}" varStatus="status">
						<c:set value="${fn:substringAfter(target_info, '_')}" var="tmp_target_id" />
						<c:choose>
							<c:when test="${good_type.h_src_id == tmp_target_id}">
								<option value="${tmp_target_id}" selected="selected">${target_info}</option>
							</c:when>
							<c:otherwise>
								<option value="${tmp_target_id}">${target_info}</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					</select>
					<input type="hidden" id="target_web_name" name="target_web_name"/>
					<p>目标网站分类词组:<textarea rows="15" cols="45" name="src_type_str">${good_type.h_src_type_str}</textarea><span>多个词组以","逗号隔开</span></p>
					<!-- <input type="text" name="src_type_str" value="${good_type.h_src_type_str}"> -->
				<input type="submit" value="修改" />
			</c:when>
			<c:otherwise>
				<p><a href="${basePath}">无相应操作,返回</a></p>
			</c:otherwise>
		</c:choose>
	</form>
</body>
<script type="text/javascript">
showYXETypeName();
loadTargetName();
</script>
</html>
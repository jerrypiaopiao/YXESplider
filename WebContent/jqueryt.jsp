<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Jquery Test</title>
<script type="text/javascript" src="js/jquery-2.1.1.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
	
	$("#hello").click(function(){
		$.ajax({
			type: "POST",
	        url: "/YXESplider/jqueryTest",
	        contentType: "application/json; charset=utf-8",
	        data: '{"id":2, "name":"jerry", "datas":["aa", "bb", "cc"]}',
	        dataType: "json",
	        success:function(message){
	        	//TODO do nothing here...
	        },
	        error:function (message) {
	        	//TODO do nothing here...
	        }
		});
	});
	
});
	
	
</script>
</head>
<body>
	<input id="hello" type="button" value="hello"/>
</body>
</html>
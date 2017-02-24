//展示分配匹配信息
function getTypeMatchRule(){
		$.ajax({
            url: "loadTypeMatch",
            type: "POST",
            dataType: "json",
            success: function (data) {   
            	var arr = eval(data.type_match_rule);
            	var innerHtml;
            	var innerHtmlnew;
            	if(arr.length > 0){
            		$.each(arr, function(index, item){
                		if(innerHtml){
                			innerHtml = innerHtml + '<p>洋小二分类id:'+item.h_yxe_type_id+'&nbsp;&nbsp;&nbsp;&nbsp;分类信息:'+item.h_src_type_str+'&nbsp;&nbsp;&nbsp;&nbsp;<a href="gotoAddTypeMatch?opt=2&yxe_t='+item.h_yxe_type_id+'&target_web_id='+item.h_src_id+'">修改</a></p>';
                			innerHtmlnew = innerHtmlnew + '<tr><td>'+item.h_yxe_type_id+'</td>'
                											+'<td border="1" >--</td>'
                											+'<td style="width: 120px">'+item.h_src_type_str+'</td>'
                											+'<td><a href="gotoAddTypeMatch?opt=2&yxe_t='+item.h_yxe_type_id+'&target_web_id='+item.h_src_id+'">修改</a></td>'
                											+'</tr>';
                		}else{
                			innerHtml =  '<p>洋小二分类id:'+item.h_yxe_type_id+'&nbsp;&nbsp;&nbsp;&nbsp;分类信息:'+item.h_src_type_str+'&nbsp;&nbsp;&nbsp;&nbsp;<a href="gotoAddTypeMatch?opt=2&yxe_t='+item.h_yxe_type_id+'&target_web_id='+item.h_src_id+'">修改</a></p>';
                			innerHtmlnew = '<tr><td>'+item.h_yxe_type_id+'</td>'
                			                                +'<td>--</td>'
                			                                +'<td style="width: 120px">'+item.h_src_type_str+'</td>'
                			                                +'<td><a href="gotoAddTypeMatch?opt=2&yxe_t='+item.h_yxe_type_id+'&target_web_id='+item.h_src_id+'">修改</a></td>'
                			                                +'</tr>';
                		}
                	});
            	}else{
            		//innerHtml = '<p><a href="gotoAddTypeMatch?opt=1&yxe_t=-1">添加类型匹配规则</a></p>';
            	}
            	innerHtml = innerHtml + '<p><a href="gotoAddTypeMatch?opt=1&yxe_t=-1&target_web_id=-1">添加类型匹配规则</a></p>';
            	//$("#type_match_rule_show").html(innerHtml);
            	var oldHtml = $("#type_match_rule_show_table").html(); 
            	var addLink = '<tr><td colspan="4"><a href="gotoAddTypeMatch?opt=1&yxe_t=-1&target_web_id=-1">添加类型匹配规则</a></td></tr>';
            	var addFilterLink = '<tr><td colspan="4"><a href="loadFilterWord">关键词过滤规则管理</a></td></tr>';
            	$("#type_match_rule_show_table").html(oldHtml + innerHtmlnew + addLink + addFilterLink);
            	
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {   
            	alert(textStatus);   
            },
            complete: function(result){
            	//do nothing...
            }
        });
}

//展示蜘蛛目标网站信息
function getTargetWebSite(){
	$.ajax({
        url: "loadSpliderTarget",
        type: "POST",
        dataType: "json",
        success: function (data) {
        	$("#target_merchant_show").text(data.target_infos);
        	var arr = eval(data.target_infos);
        	var innerHtml = '<option>--</option>';
        	$.each(arr, function(index, item){
        		innerHtml = innerHtml + '<option value="'+item+'">'+item+'</option>';
        	});
        	$("#target_merchant").html(innerHtml);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {   
        	alert(textStatus);   
        },
        complete: function(result){
        	//do nothing...
        }
    });
}

//根据目标网站获取目标网站的具体信息
function getMerInfos(targetStr){
	$.ajax({
        url: "loadMerInfoByTarget",
        type: "GET",
        data:"mer_type="+targetStr,
        success: function (data) {
        	var arr = eval(data.mer_info_list);
        	var innerHtml = '<option>--</option>';
        	$.each(arr, function(index, item){
        		innerHtml = innerHtml + '<option value="'+item.merHost+'">'+item.merName+'</option>';
        	});
        	$("#merchant_name").html(innerHtml);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {   
        	alert(textStatus);   
        },
        complete: function(result){
        	//do nothing...
        }
    });
}

//根据目标网站获取目标网站的具体信息
function startFetch(merType, merHost){
	alert("mer_type:"+merType+", mer_host:"+merHost);
	$.ajax({
        url: "startFetch",
        type: "GET",
        data:{
        	mer_type:merType,
        	mer_host:merHost
        },
        success: function (data) {
        	alert(data.response_msg.merType+", "+data.response_msg.target);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
        	alert(textStatus);   
        },
        complete: function(result){
        	//do nothing...
        }
    });
}
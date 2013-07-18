/*
* Copyright 2013- Antonio Menendez Lopez (tonimenen)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
//Online chart
function display_offline_chart(actual_plot,tittle_id,in_namesrv,in_ipsrv,type_stat,type_metric,start_date,end_date,number_returned_metrics){
	
	var options = {
			lines: {
				show: true
			},
			xaxis: {
				mode: "time"
			},
			yaxis: {
				min: 0
			}
		};
		
		//We parse the array of servers in order to have the reqyest to send in HTTP
		var res_obj = parse_for_request(in_namesrv,in_ipsrv);
			
		var nameserver=res_obj.nameserver;
		var ipserver=res_obj.ipserver;
	
		//Get the offset in te browser
		var offset_browser = new Date().getTimezoneOffset();	
		myconsolelog("Offset is : " + offset_browser);

		var parameters_to_send = nameserver+"&"+ipserver+"&start_date="+start_date+"&end_date="+end_date+"&offset_browser="+offset_browser+"&type_stat="+type_stat+"&type_metric="+type_metric+"&num_metrics="+number_returned_metrics
		myconsolelog(parameters_to_send);
		
		//Add tittle to the plot		
		$("#"+tittle_id).html("Tittle : Offline Analysis - Stat ( "+type_stat+" ) - Metric ( "+type_metric+" )");		
		

		$.ajax({ 
			type:'GET',
			url: 'getMetricServerbyDate.jsp', // JQuery loads from jsp file
			data: parameters_to_send,
			dataType: 'json', // Choosing a JSON datatype
			success: function(result) // Variable data contains the data we get from serverside
					{
					if(result){ //Update the info related to the remote server						
						myconsolelog(result);
						var placeholder = $("#"+actual_plot);
						$.plot(placeholder,result,options);
						
						
					}
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
					$('#tabs').tabs({ active: 0 });
					$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
					
				}
			});		
}
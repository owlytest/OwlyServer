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
	//function to add console only if debug is enabled
	function myconsolelog(display_info) {
		if (debug_console){
			console.log(display_info);
		}
	}

	//function used for automatic with ajustment
	function resizeInput() {
		$(this).attr('size', $(this).val().length);
	}

	//This function will update the infor related to remote server based in name and ip of the remote server
	function update_remserver_info(in_nameServer,in_ipserver){		
		$.ajax({ // ajax call when a selectable is done in the list.
			type:'GET',
			url: 'getSelectedRemoteServer.jsp', // JQuery loads from jsp file
			data: "nameserver="+in_nameServer+"&ipserver="+in_ipserver,
			dataType: 'json', // Choosing a JSON datatype
			success: function(result) // Variable data contains the data we get from serverside
					{
					if(result){ //Update the info related to the remote server
						$(' #txtRemoteServerName').val(result.Name);
						$(' #txtStatsIpAddress').val(result.NodeIPAddress);
						$(' #txRemoteServerType').val(result.SrvType);
						$(' #txtRemoteClientPort').val(result.ClientPort);
						myconsolelog(result.ServerStatus);
						if (result.ServerStatus){
							$('#radioResponseStatus-1').prop("checked",true);
							}
						else{
							$('#radioResponseStatus-2').prop("checked",true);
						}
						if (result.Enabled){
							$('#radioRemoteStatus-1').prop("checked",true);					
						}
						else{
							$('#radioRemoteStatus-2').prop("checked",true);
						}								
						
						var total = result.ListTypeOfStats.length;
						$( "#listStat" ).empty();
						for (var i=0;i<total;i++)
						{
							$( "#listStat" ).append("<li>"+result.ListTypeOfStats[i]+"</li>");									
							
						}
						//Once info is received show this part of the page
						$(' #remoteServer').show();
					}
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
					$('#tabs').tabs({ active: 0 });
					$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
					
				}
			});
	
	
		}
		// Update info related to stat server
		function update_stat_server_info(){
			$.ajax({ // ajax call starts
				url: 'getServerStatsDetails.jsp', // JQuery loads from jsp file			
				dataType: 'json', // Choosing a JSON datatype
				success: function(data) // Variable data contains the data we get from serverside
				{
					$('#txtStatsDatabase').val(data.StatsDatabase);
					$('#txtStatsDatabaseType').val(data.StatsDatabaseType);
					$('#txtStatsIpAddress').val(data.StatsIpAddress);
					$('#txStatsPort').val(data.StatsPort);
					$('#txtName').val(data.Name);
					$('#txtStatsCollect').val(data.StatsCollect);
					$('#txSavedDays').val(data.SavedDays);
					if (data.Enabled){
						$('#radioStatus-1').prop("checked",true);
						}
					else{
						$('#radioStatus-2').prop("checked",true);
					}
					if (data.Monitoring){
						$('#radioMonitor-1').prop("checked",true);					
					}
					else{
						$('#radioMonitor-2').prop("checked",true);
					}
					
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$.alert(xhr.status,"ERROR");
					$.alert(thrownError,"ERROR");
				}
			});
		}
		
		//Update info related to remote servers
		function update_remote_servers_info(typeOfUpdate){
			// Execute an ajax to get data related to remote servers.
			$.ajax({ // ajax call starts
					url: 'getRemoteServers.jsp', // JQuery loads from jsp file			
						dataType: 'json', // Choosing a JSON datatype
						success: function(data) // Variable data contains the data we get from serverside
						{
							if(data){
								// Drop all elements before fill
								$( "#selectable" ).empty();
								// Drop all info for selection of server
								$( "#server_selector_body" ).empty();
								var total = data.length;	
								myconsolelog(data);
								var index_element=0;
								for (var i=0;i<total;i++)
								{
									//Add elements of selectable dinamycally
									$( "#selectable" ).append("<li class=\"ui-widget-content\" id=\"selct-val-"+i+"\">"+data[i].Name+" ( "+data[i].NodeIPAddress+" )</li>");
									if ((data[i].ServerStatus)&&(data[i].Enabled)){										
										$( "#server_selector_body" ).append("<tr><td><input type=\"checkbox\" name=\"sel_server\" id=\"sel_server_"+index_element+"\"></td><td id=\"tdname\">"+data[i].Name+"</td><td id=\"tdip\">"+data[i].NodeIPAddress+"</td><td>"+data[i].ServerStatus+"</td><td>"+data[i].Enabled+"</td></tr>");
										// Bind a callback when a checkbox is selected in online monitoring									
										$("#sel_server_"+index_element).bind('click', function() {
											//Check if online is selected or offline in order to continue
											if ( type_of_analyze == "online" ){											
												//Check if minutes has been selected and is correct
												var minutes_to_check=$('#txtAnalyzeMinutes').val();
												if (!minutes_to_check){
													$.alert("Minutes to monitor value is not present","ERROR");
													$( "#analyze_metrics_stat").empty();
													$("#analyze_server_type_stat :radio").prop("checked",false);
													$("#analyze_server_type_stat :radio").button("refresh");
													$(this).prop("checked",false);
													
													
												}else
												{	
													//Check if format of minutes is correct with regular expresions
													var numericReg = /^\d*[0-9]{1,3}?$/;
													 if(!numericReg.test(minutes_to_check)) {									 	 
														  $.alert("Minutes should be an integer value", "ERROR");
														  $(this).prop("checked",false);
													 }
													 else{
														 if ( (minutes_to_check > 0) && (minutes_to_check < 1441)){
															//Get metrics for this period of time														 
															var nb_of_childs=$('#server_selector_body').children().length;
															var nb_of_selected_childs=0;
															var val_selected = new Array();
															for (var j=0;j<nb_of_childs;j++){
																if ($("#sel_server_"+j).prop('checked')){
																	//myconsolelog(nb_of_selected_childs);
																	val_selected[nb_of_selected_childs]="#sel_server_"+j;
																	nb_of_selected_childs=nb_of_selected_childs+1;
																	}
															}
															if (nb_of_selected_childs==0){												
																clean_selection_options();
															
															}else
															{
																if (nb_of_selected_childs==1){																	
																	add_selection_options_online("one",val_selected,minutes_to_check);
																}
																else{
																																	
																	add_selection_options_online("multi",val_selected,minutes_to_check);
																	
																}
															}
														 }
														 else
														 {
															  $.alert("Online monitoring can only be executed for one day period (0 - 1440 minutes) ", "ERROR");
														  }
														 
													}
												}
											}
											else{ // We are in offline analysis
												//Check inputs for dates and check format with regular expressions.
												var start_date=$('#txtStartDate').val();
												var end_date=$('#txtEndDate').val();
												myconsolelog("start_date : " + start_date + " and end_date : " + end_date);
												var dateReg = /^(19|20)\d\d[-/.](0[1-9]|1[012])[-/.](0[1-9]|[12][0-9]|3[01])\s([01]?[0-9]|2[0-3]):[0-5][0-9]$/;
												if(!dateReg.test(start_date)) {
													$.alert("Start Date needs to be in formant yyyy-mm-dd hh:mm ", "ERROR");
												}
												else{
													if(!dateReg.test(end_date)) {
													$.alert("End Date needs to be in formant yyyy-mm-dd hh:mm ", "ERROR");
													}
													else{
														 var nb_of_childs=$('#server_selector_body').children().length;
														var nb_of_selected_childs=0;
														var val_selected = new Array();
														for (var j=0;j<nb_of_childs;j++){
															if ($("#sel_server_"+j).prop('checked')){
																//myconsolelog(nb_of_selected_childs);
																val_selected[nb_of_selected_childs]="#sel_server_"+j;
																nb_of_selected_childs=nb_of_selected_childs+1;
																}
														}
														if (nb_of_selected_childs==0){												
															clean_selection_options();
															
														}else
														{
															if (nb_of_selected_childs==1){
																
																add_selection_options_offline("one",val_selected,start_date,end_date);
															}
															else{
																add_selection_options_offline("multi",val_selected,start_date,end_date);
															}
														}														
													}
				
												}
											}
										});
									index_element=index_element+1;
								}
								}
								//Only update the server if we are in the tab of remote server
								if (typeOfUpdate == 'remsrv'){
									//Select first option
									$( "#selct-val-0" ).addClass("ui-selected");
									var value_selected=$("#selct-val-0").text();
									var statServer=value_selected.split(" ( ");
									nameServer=statServer[0];
									var ipserver=(statServer[1].split(" )")[0]);
									update_remserver_info(nameServer,ipserver);
									//Don´t show till all info is received
								}
								$( "#analyze_server" ).show();
							}
						},
						error: function (xhr, ajaxOptions, thrownError) {
							$.alert(xhr.status,"ERROR");
							$.alert(thrownError,"ERROR");
						}
					});
		
		
		}
		
		//Parse the array of servers in order to create the list of options to send inyo the HTTP request
		function parse_for_request(in_namesrv,in_ipsrv){
			//The parameters received in the function are arrays lets process in order to send then in the request as a parameters	
			var nameserver="";
			for (i=0;i<in_namesrv.length-1;i++){
				nameserver=nameserver+"nameserver="+in_namesrv[i]+"&";
			}
			nameserver=nameserver+"nameserver="+in_namesrv[in_namesrv.length-1]; //Avoid andversand in last value
			
			var ipserver="";
			for (i=0;i<in_ipsrv.length-1;i++){
				ipserver=ipserver+"ipserver="+in_ipsrv[i]+"&";
			}
			ipserver=ipserver+"ipserver="+in_ipsrv[in_ipsrv.length-1]; //Avoid andversand in last value
			
			parse_obj=new Object();
			
			parse_obj.nameserver=nameserver;
			parse_obj.ipserver=ipserver;
			
			return(parse_obj)
		}
		
		
		//Get type of stats in database for a specific server filtered by name and IP
		function get_type_stats_in_server(type_analysis,type_multi,in_namesrv,in_ipsrv,minutes_to_check,start_date,end_date){

			//We parse the array of servers in order to have the reqyest to send in HTTP
			var res_obj = parse_for_request(in_namesrv,in_ipsrv);
			
			var nameserver=res_obj.nameserver;
			var ipserver=res_obj.ipserver;
	
			
			if ( type_analysis == "online") { //We are doing an online analysys
				var parameters_to_send = nameserver+"&"+ipserver+"&minutes_to_check="+minutes_to_check
				myconsolelog(parameters_to_send);
			}else
			{
				
								//Get the offset in te browser
				var offset_browser = new Date().getTimezoneOffset();	
				myconsolelog("Offset is : " + offset_browser);

				var parameters_to_send = nameserver+"&"+ipserver+"&start_date="+start_date+"&end_date="+end_date+"&offset_browser="+offset_browser
				myconsolelog(parameters_to_send);
				
			}
			
	
			if ( type_analysis == "online") { //We are doing an online analysys
			
			$.ajax({ 
				type:'GET',
				url: 'getTypeStatsRemoteServer.jsp', // JQuery loads from jsp file
				data: parameters_to_send, // This has been generated in top of this function
				dataType: 'json', // Choosing a JSON datatype
				success: function(result) // Variable data contains the data we get from serverside
						{
						if(result){ //Update the info related to the remote server
							myconsolelog(result);
							var total = result.length;
							if ( total  == 0 ) { //Result is empty, no mectrics obtained in this period of time
								$.alert("No metriis obtained in last period of time, check if monitoring is configured now","ERROR");
								$("#server_selector_body tr td input").prop("checked",false);
								$("#analyze_server_stat").empty();
							}
							else{
								if ( type_multi == "one" ){ // We are doing only one server analyze
									$.alert("One Server selected","ALERT");
								}
								for (var i=0;i<total;i++)
								{
									$("#analyze_server_type_stat").append("<input type=\"radio\" id=\"type_stat_"+i+"\" name=\"radio_type_stat\"><label for=\"type_stat_"+i+"\">"+result[i]+"</label>");
									$("#type_stat_"+i).button();								
									$("#type_stat_"+i).bind('click', function() {
										var type_stat_selected = ($("#analyze_server_type_stat :radio:checked + label").text());
										myconsolelog(type_stat_selected);
										get_type_metrics_in_server(type_analysis,in_namesrv,in_ipsrv,type_stat_selected,minutes_to_check,start_date,end_date);
										
									});
									
								}
								$( "#analyze_server_stat").show();
							}
						}
					},
					error: function (xhr, ajaxOptions, thrownError) {
						$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
						$('#tabs').tabs({ active: 0 });
						$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
						
					}
				});
			}else{
				$.ajax({ 
				type:'GET',
				url: 'getTypeStatsRemoteServerbyDate.jsp', // JQuery loads from jsp file
				data: parameters_to_send, // This has been generated in top of this function
				dataType: 'json', // Choosing a JSON datatype
				success: function(result) // Variable data contains the data we get from serverside
						{
						if(result){ //Update the info related to the remote server
							myconsolelog(result);
							var total = result.length;
							if ( total  == 0 ) { //Result is empty, no mectrics obtained in this period of time
								$.alert("No metriis obtained in this period of time","ERROR");
								$("#server_selector_body tr td input").prop("checked",false);
								$("#analyze_server_stat").empty();

							}
							else{
								if ( type_multi == "one" ){ // We are doing only one server analyze
									$.alert("One Server selected","ALERT");
								}
								for (var i=0;i<total;i++)
								{
									$("#analyze_server_type_stat").append("<input type=\"radio\" id=\"type_stat_"+i+"\" name=\"radio_type_stat\"><label for=\"type_stat_"+i+"\">"+result[i]+"</label>");
									$("#type_stat_"+i).button();								
									$("#type_stat_"+i).bind('click', function() {
										var type_stat_selected = ($("#analyze_server_type_stat :radio:checked + label").text());
										myconsolelog(type_stat_selected);
										get_type_metrics_in_server(type_analysis,in_namesrv,in_ipsrv,type_stat_selected,minutes_to_check,start_date,end_date);
										
									});
									
								}
								$( "#analyze_server_stat").show();
							
							}
						}
					},
					error: function (xhr, ajaxOptions, thrownError) {
						$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
						$('#tabs').tabs({ active: 0 });
						$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
						
					}
				});				
				

				
			}
			
			
		
		}
		
		//Get type of metrcis from stats 
		function get_type_metrics_in_server(type_analysis,in_namesrv,in_ipsrv,type_selected,minutes_to_check,start_date,end_date){
			
		//We parse the array of servers in order to have the reqyest to send in HTTP
		var res_obj = parse_for_request(in_namesrv,in_ipsrv);
			
		var nameserver=res_obj.nameserver;
		var ipserver=res_obj.ipserver;
	
			
		if ( type_analysis == "online") { //We are doing an online analysys
			var parameters_to_send = nameserver+"&"+ipserver+"&minutes_to_check="+minutes_to_check+"&type_stat="+type_selected
			myconsolelog(parameters_to_send);
		}else
		{
				
								//Get the offset in te browser
			var offset_browser = new Date().getTimezoneOffset();	
			myconsolelog("Offset is : " + offset_browser);

			var parameters_to_send = nameserver+"&"+ipserver+"&start_date="+start_date+"&end_date="+end_date+"&offset_browser="+offset_browser+"&type_stat="+type_selected
			myconsolelog(parameters_to_send);
				
		}			
			
			
		if ( type_analysis == "online") { //We are doing an online analysys
			$.ajax({ 
				type:'GET',
				url: 'getTypeMetricfromStat.jsp', // JQuery loads from jsp file
				data: parameters_to_send,
				dataType: 'json', // Choosing a JSON datatype
				success: function(result) // Variable data contains the data we get from serverside
						{
						if(result){ //Update the info related to the remote server
							//myconsolelog(result);
							$( "#analyze_metrics_stat").empty();
							$( "#analyze_metrics_stat").append("<br>");
							$( "#analyze_metrics_stat").append("Type of Metrics to analyze : ");
							$( "#analyze_metrics_stat").append("<br>");
							$( "#analyze_metrics_stat").append("<br>");
							$( "#analyze_metrics_stat").append("<div id=\"analyze_server_type_metric\"></div>");
							var total = result.length;									 
							for (var i=0;i<total;i++)
							{
								$("#analyze_server_type_metric").append("<input type=\"radio\" id=\"type_metric_"+i+"\" name=\"radio_type_metric\"><label for=\"type_metric_"+i+"\">"+result[i]+"</label>");
								$("#type_metric_"+i).button();								
								$("#type_metric_"+i).bind('click', function() {
									var type_metric_selected = ($("#analyze_server_type_metric :radio:checked + label").text());
									myconsolelog(type_metric_selected);
									myconsolelog("Type of analysis : " + type_of_analyze);
									// This part is to analyze the input values in the form
	
									 var minutes_to_check=$('#txtAnalyzeMinutes').val();
									 get_metrics_online(in_namesrv,in_ipsrv,type_selected,type_metric_selected,minutes_to_check);
									
								});
									
							}
						$( "#analyze_metrics_stat").show();						
						}
					},
					error: function (xhr, ajaxOptions, thrownError) {
						$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
						$('#tabs').tabs({ active: 0 });
						$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
						
					}
				});
			}else{ // We are in offline analysis
				
				//Get the offset in te browser
				var offset_browser = new Date().getTimezoneOffset();	
				myconsolelog("Offset is : " + offset_browser);


				$.ajax({ 
					type:'GET',
					url: 'getTypeMetricfromStatbyDate.jsp', // JQuery loads from jsp file
					data: parameters_to_send,
					dataType: 'json', // Choosing a JSON datatype
					success: function(result) // Variable data contains the data we get from serverside
							{
							if(result){ //Update the info related to the remote server
								//myconsolelog(result);
								$( "#analyze_metrics_stat").empty();
								$( "#analyze_metrics_stat").append("<br>");
								$( "#analyze_metrics_stat").append("Type of Metrics to analyze : ");
								$( "#analyze_metrics_stat").append("<br>");
								$( "#analyze_metrics_stat").append("<br>");
								$( "#analyze_metrics_stat").append("<div id=\"analyze_server_type_metric\"></div>");
								var total = result.length;									 
								for (var i=0;i<total;i++)
								{
									$("#analyze_server_type_metric").append("<input type=\"radio\" id=\"type_metric_"+i+"\" name=\"radio_type_metric\"><label for=\"type_metric_"+i+"\">"+result[i]+"</label>");
									$("#type_metric_"+i).button();								
									$("#type_metric_"+i).bind('click', function() {
										var type_metric_selected = ($("#analyze_server_type_metric :radio:checked + label").text());
										myconsolelog(type_metric_selected);
										myconsolelog("Type of analysis : " + type_of_analyze);

										// This part if for analyzing offlie monitoring
										get_metrics_offline(in_namesrv,in_ipsrv,type_selected,type_metric_selected,start_date,end_date);
									});
										
								}
							$( "#analyze_metrics_stat").show();						
							}
						},
						error: function (xhr, ajaxOptions, thrownError) {
							$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
							$('#tabs').tabs({ active: 0 });
							$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
							
						}
					});
			
			}
		
			
		
		}		
		
		//Create tabs, and flot container to drow inside the plot
		function create_plot(type_analysis,in_namesrv,in_ipsrv,type_stat,type_metric,minutes_to_chk,start_date,end_date){
			
			var num_tabs_now = $("div#tabs ul li.ui-state-default").length; //Number of tabs in the system
			var actual_tab = num_tabs_now - num_tabs_original +1; //Number of actual tab droping the original value of tabs
			if ( num_of_plots > actual_tab - 1 ){ // In case we are not the 1st tab created we will create the tab based in the previous tab number created.
				actual_tab= num_of_plots+ 1;
			}
			myconsolelog(actual_tab);
			myconsolelog($("div#tabs ul li.ui-state-default"));
			var tabs = $( "#tabs" ).tabs(), //Our jquery UI tabs
				id = "tabs-monitor-"+actual_tab //id for the new tab to create
			if ( type_analysis == "online" ){
				var label="Online_monitor_"+actual_tab // label for the tab created
			}
			else{
				var label="Offline_Analysis_"+actual_tab // label for the tab created
			}
			var	li="<li><a href=\"#"+id+"\">"+label+"</a></li>" // HTML for the tab to create 
			var tabs = $( "#tabs" ).tabs();
			tabs.find( ".ui-tabs-nav" ).append( li ); //Creation of the tabe
			tabs.append( "<div id='" + id + "'></div>" ); // Add the div for this tab

			var tittle_id="tittle_plot-"+actual_tab;
			$("#"+id).append("<div id=\""+tittle_id+"\" style=\"text-align:center;font-weight:bold;font-size:1.3em;\"></div>"); //create the container
			//Create the button to close the tabs
			$("#"+id).append("<button id=\""+type_analysis+"_but_"+actual_tab+"\">Close Statistic</button>");
			$( "#"+type_analysis+"_but_"+actual_tab).css({"position":"absolute","right":"20px"});
			//This is a UI juqery button
			$( "#"+type_analysis+"_but_"+actual_tab).button();
			//Add logic to buton for droooping all elmentes related to this tab and plot.
			$( "#"+type_analysis+"_but_"+actual_tab).click(function() {
			var this_button=$(this).attr('id'), // The id of my button
				this_tab_nb=this_button.replace(""+type_analysis+"_but_",""), // Get the number of this button
				this_tab="tabs-monitor-"+this_tab_nb, //Get the id of the tab
				this_plot_nb=this_tab_nb-1, // Get the number of the plot created.
				tabs = $( "#tabs" ).tabs();
				for (i=0;i<open_plots.length;i++){ // Check the Array with all tabs created that is in memory
					if (open_plots[i].actual_plot == "ChartDiv-"+this_plot_nb){ // Drop from the array info related to this plot
						myconsolelog("We need to drop object = "+ open_plots[i].actual_plot);
						open_plots.splice(i, 1);
						//myconsolelog("After dropping = "+ open_plots);
						//num_of_plots=num_of_plots-1;
						//myconsolelog($( "#tabs" ));
						//myconsolelog($( "#"+this_tab));
						$( "#tabs" ).find(  "#"+this_tab).remove(); // Drop the tab from gereric UI tab
						$( "#tabs" ).children("ul").children("li[aria-controls=\""+this_tab+"\"]").remove(); // Drop the label in the upper tab.
						tabs.tabs( "refresh" ); // Refresh the UI tab
						$('#tabs').tabs({ active: 1 });
					}
				}
										
			});

			tabs.tabs( "refresh" );
								
			//Let´s create the graph now								
			var actual_plot="ChartDiv-"+num_of_plots; //NUmber of plots we ha created 
			var actual_Containner="ContainerDiv-"+num_of_plots; // Id of the container of FLot pluguin.
			
			myconsolelog("A new plot needs to be created, actual_plot = " + actual_plot);
								
			with_tabs=($( "#tabs" ).width()-100); //get the with of the tabs
			height_tabs=($( "#tabs" ).height()); // get the height of the tabs
			myconsolelog("with_tabs = " + with_tabs+",height_tabs = " + height_tabs);								
								
			$("#"+id).append("<div id=\""+actual_Containner+"\"class=\"plot-container\"></div>"); //create the container
			//add css wtih actual size from tabs
			var css_size={"width":with_tabs,"height":height_tabs}
			$("#"+actual_Containner).css(css_size);
								
			with_container=($("#"+actual_Containner).width()); //get the with of the container
			myconsolelog("with_container = " + with_container);
			height_container=($("#"+actual_Containner).height()); // get the height of the container
								
			css_size={"width":with_container,"height":height_container}
			$("#"+actual_Containner).append("<div id=\""+actual_plot+"\" class=\"plot_monitor\"></div>"); // Create the canvas for the plot
			$("#"+actual_plot).css(css_size); //Chnage the size based in the container --> this is due to a bug in Flot plugion.
			num_of_plots=num_of_plots+1; // a new plot is added.
			if ( type_analysis == "online" ){
				display_online_chart(actual_plot,tittle_id,in_namesrv,in_ipsrv,type_stat,type_metric,minutes_to_chk,number_returned_metrics); // Display now the plot with this function.
			}
			else{
				display_offline_chart(actual_plot,tittle_id,in_namesrv,in_ipsrv,type_stat,type_metric,start_date,end_date,number_returned_metrics); // Display now the plot with this function.
			}
			//Create a new object with info related to this plot
			fplot_obj=new Object();
			fplot_obj.tittle_id=tittle_id;
			fplot_obj.actual_plot=actual_plot;
			fplot_obj.actual_container=actual_Containner;
			fplot_obj.namesrv=in_namesrv;
			fplot_obj.ipsrv=in_ipsrv;
			fplot_obj.typestat=type_stat;
			fplot_obj.typemetric=type_metric;
			if ( type_analysis == "online" ){
				fplot_obj.minutestochk=minutes_to_chk;
				fplot_obj.typeplot="online";
			}
			else{
				fplot_obj.minutestochk=0;
				fplot_obj.typeplot="offline";
			}
			//Add this object to my global variable of online plots
			open_plots.push(fplot_obj);
		}
		
		
		
		//Get metrics table for one server and type of stat for online analysis type. 
		function get_metrics_online(in_namesrv,in_ipsrv,type_stat,type_metric,minutes_to_check){
			
		//We parse the array of servers in order to have the reqyest to send in HTTP
		var res_obj = parse_for_request(in_namesrv,in_ipsrv);
			
		var nameserver=res_obj.nameserver;
		var ipserver=res_obj.ipserver;
	
		var parameters_to_send = nameserver+"&"+ipserver+"&minutes_to_check="+minutes_to_check+"&type_stat="+type_stat+"&type_metric="+type_metric
		myconsolelog(parameters_to_send);

		var plot_monitor = {
			"width": "500",
			"height": "500",
			"font-size": "14px",
			"line-height": "1.2em"
		}
		
		var plot_container = {		
			"box-sizing": "border-box",
			"width": "850px",
			"height": "450px",
			"padding": "20px 15px 15px 15px",
			"margin": "15px auto 30px auto",
			"border": "1px solid #ddd",
			"background": "#fff",
			"background": "linear-gradient(#f6f6f6 0, #fff 50px)",
			"background": "-o-linear-gradient(#f6f6f6 0, #fff 50px)",
			"background": "-ms-linear-gradient(#f6f6f6 0, #fff 50px)",
			"background": "-moz-linear-gradient(#f6f6f6 0, #fff 50px)",
			"background": "-webkit-linear-gradient(#f6f6f6 0, #fff 50px)",
			"box-shadow": "0 3px 10px rgba(0,0,0,0.15)",
			"-o-box-shadow": "0 3px 10px rgba(0,0,0,0.1)",
			"-ms-box-shadow": "0 3px 10px rgba(0,0,0,0.1)",
			"-moz-box-shadow": "0 3px 10px rgba(0,0,0,0.1)",
			"-webkit-box-shadow": "0 3px 10px rgba(0,0,0,0.1)"
		}
		
		
		$.ajax({ 
			type:'GET',
			url: 'getMetricServerAvailable.jsp', // JQuery loads from jsp file
			data: parameters_to_send,
			dataType: 'json', // Choosing a JSON datatype
			success: function(result) // Variable data contains the data we get from serverside
					{
					if(result){ //Update the info related to the remote server						
						myconsolelog(result);
						if(result.isMetrics=='OK'){
								myconsolelog("OK --> We have metric for this options");
								//Create the tabs and plots for flot
								create_plot("online",in_namesrv,in_ipsrv,type_stat,type_metric,minutes_to_check,0,0);
								
								
						}else
						{
							$.alert("No data available for this options","WARNING")
						}
							

						
						
						
						
					}
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
					$('#tabs').tabs({ active: 0 });
					$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
					
				}
			});
		
		}		


		//Get metrics table for one server and type of stat for offline analysis type. 
		function get_metrics_offline(in_namesrv,in_ipsrv,type_stat,type_metric,start_date,end_date){
			
		//We parse the array of servers in order to have the reqyest to send in HTTP
		var res_obj = parse_for_request(in_namesrv,in_ipsrv);
			
		var nameserver=res_obj.nameserver;
		var ipserver=res_obj.ipserver;
	
		//Get the offset in te browser
		var offset_browser = new Date().getTimezoneOffset();	
		myconsolelog("Offset is : " + offset_browser);

		var parameters_to_send = nameserver+"&"+ipserver+"&start_date="+start_date+"&end_date="+end_date+"&offset_browser="+offset_browser+"&type_stat="+type_stat+"&type_metric="+type_metric
		myconsolelog(parameters_to_send);
				
		var plot_monitor = {
			"width": "500",
			"height": "500",
			"font-size": "14px",
			"line-height": "1.2em"
		}
		
		var plot_container = {		
			"box-sizing": "border-box",
			"width": "850px",
			"height": "450px",
			"padding": "20px 15px 15px 15px",
			"margin": "15px auto 30px auto",
			"border": "1px solid #ddd",
			"background": "#fff",
			"background": "linear-gradient(#f6f6f6 0, #fff 50px)",
			"background": "-o-linear-gradient(#f6f6f6 0, #fff 50px)",
			"background": "-ms-linear-gradient(#f6f6f6 0, #fff 50px)",
			"background": "-moz-linear-gradient(#f6f6f6 0, #fff 50px)",
			"background": "-webkit-linear-gradient(#f6f6f6 0, #fff 50px)",
			"box-shadow": "0 3px 10px rgba(0,0,0,0.15)",
			"-o-box-shadow": "0 3px 10px rgba(0,0,0,0.1)",
			"-ms-box-shadow": "0 3px 10px rgba(0,0,0,0.1)",
			"-moz-box-shadow": "0 3px 10px rgba(0,0,0,0.1)",
			"-webkit-box-shadow": "0 3px 10px rgba(0,0,0,0.1)"
		}
		
		//Get the offset in te browser
		var offset_browser = new Date().getTimezoneOffset();	
		myconsolelog("Offset is : " + offset_browser);
		
		
		$.ajax({ 
			type:'GET',
			url: 'getMetricServerAvailablebyDate.jsp', // JQuery loads from jsp file
			data: parameters_to_send,
			dataType: 'json', // Choosing a JSON datatype
			success: function(result) // Variable data contains the data we get from serverside
					{
					if(result){ //Update the info related to the remote server						
						myconsolelog(result);
						if(result.isMetrics=='OK'){
								myconsolelog("OK --> We have metric for this options");
								//Create the tabs and plots for flot
								create_plot("offline",in_namesrv,in_ipsrv,type_stat,type_metric,0,start_date,end_date);
								
								
								
						}else
						{
							$.alert("No data available for this options","WARNING")
						}
					}
				},
				error: function (xhr, ajaxOptions, thrownError) {
					$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
					$('#tabs').tabs({ active: 0 });
					$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
					
				}
			});
		
		}		

		
		
		
		//When all servers are unselected from the list, we will drop all oprions in the page
		function clean_selection_options (){
			
				$.alert("Nothing selected","ERROR");
				$( "#analyze_server_stat").empty();
				$( "#analyze_metrics_stat").empty();
				
				
		}
		
		//When one server is selected we will give options related to only this server
		function add_selection_options_online(type_multi,values,minutes_to_check){
			if ( type_multi == "multi"){
				$.alert("Multi Server selected","ALERT");
				$( "#analyze_server_stat").empty();
				$( "#analyze_metrics_stat").empty();
			}
				//alert(values);
			$( "#analyze_server_stat").empty();
			$( "#analyze_server_stat").append("<br>");
			$( "#analyze_server_stat").append("Type of Statistics : ");
			$( "#analyze_server_stat").append("<br>");
			$( "#analyze_server_stat").append("<br>");
			$( "#analyze_server_stat").append("<div id=\"analyze_server_type_stat\"></div>");
			$( "#analyze_metrics_stat").empty();
				
			//Array with info of names and Ips of the server
			var server_ip = new Array(); 
			var server_name = new Array();
			//Add values o the array
			for(i=0;i<values.length;i++){
					//myconsolelog($("input"+values[i]).parent().parent().find("#tdname").text());
					var sel_name=$("input"+values[i]).parent().parent().find("#tdname").text();
					var sel_ip=$("input"+values[i]).parent().parent().find("#tdip").text();
					server_name.push(sel_name);
					server_ip.push(sel_ip);
				
			}
			get_type_stats_in_server("online",type_multi,server_name,server_ip,minutes_to_check,0,0);
		}

		
		//When one server is selected we will give options related to only this server
		function add_selection_options_offline(type_multi,values,start_date,end_date){
			if ( type_multi == "multi"){
				$.alert("Multi Server selected","ALERT");
				$( "#analyze_server_stat").empty();
				$( "#analyze_metrics_stat").empty();
			}
			
				//alert(values);
				$( "#analyze_server_stat").empty();
				$( "#analyze_server_stat").append("<br>");
				$( "#analyze_server_stat").append("Type of Statistics : ");
				$( "#analyze_server_stat").append("<br>");
				$( "#analyze_server_stat").append("<br>");
				$( "#analyze_server_stat").append("<div id=\"analyze_server_type_stat\"></div>");
				$( "#analyze_metrics_stat").empty();
				
				//Array with info of names and Ips of the server
				var server_ip = new Array(); 
				var server_name = new Array();

				//Add values to the array
				for(i=0;i<values.length;i++){
					//myconsolelog($("input"+values[i]).parent().parent().find("#tdname").text());
					var sel_name=$("input"+values[i]).parent().parent().find("#tdname").text();
					var sel_ip=$("input"+values[i]).parent().parent().find("#tdip").text();
					server_name.push(sel_name);
					server_ip.push(sel_ip);
				}
				get_type_stats_in_server("offline",type_multi,server_name,server_ip,0,start_date,end_date);
		}
		
		// This function formats the integer in 2 disigts
		function pad(d) {
			return (d < 10) ? '0' + d.toString() : d.toString();
		}
		
		//Funtion to get the actual date in the system ( and day offest can be added ).
		function get_date_and_format (previous_days){
			var msecPerDay = 24 * 60 * 60 * 1000;
			var currentTime = new Date();
			//myconsolelog("currentTime = " + currentTime);
			
			var modDate = new Date(currentTime.getTime() + (previous_days*msecPerDay));//subtract a day's worth of ms
			
			//myconsolelog("modDate = " + modDate);
			
			var actualHour=modDate.getHours();
			var actualMinutes=modDate.getMinutes();
			var actualYear=modDate.getFullYear();
			var actualMonth=modDate.getMonth()+1;
			var actualDay=modDate.getDate();
			var formatDAte=actualYear+"-"+pad(actualMonth)+"-"+pad(actualDay)+" "+pad(actualHour)+":"+pad(actualMinutes);
			//myconsolelog("get_date_and_format = " + formatDAte);
			
			return(formatDAte);
			
			
		}


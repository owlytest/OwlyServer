<!-- 
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
*/ -->

<!doctype html>
<html lang="us">
<head>
	<meta charset="utf-8">
	<title>The Owly User Inferface</title>
	<%  
	String typeofshow = request.getParameter("typeofshow");
	%>
	
	<%
	response.addCookie(new Cookie("JSESSIONID", session.getId()));
	%>

	<link href="css/sunny/jquery-ui-1.10.3.custom.css" rel="stylesheet">	
	<link href="css/flot/flot.css" rel="stylesheet">
	<script src="js/jquery-1.9.1.js"></script>
	<script src="js/jquery-ui-1.10.3.custom.js"></script>
	<script src="js/jquery.flot.js"></script>
	<script src="js/jquery.flot.time.js"></script>
	<script src="js/jquery.flot.resize.js"></script>
	
	
	
	<script src="incliude_functions.js"></script>
	<script src="monitor_online.js"></script>
	<script src="analysis_offline.js"></script>
	
    
	
	<style>
		#feedback { font-size: 1.0em; }
		#selectable .ui-selecting { background: #FECA40; }
		#selectable .ui-selected { background: #F39814; color: white; }
		#selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
		#selectable li { margin: 3px; padding: 0.4em; font-size: 1.0em; height: 18px; }
		.server_selector { border-collapse: collapse;  margin: 20px; text-align: left;  width: 480px;}
		.server_selector th { background: none repeat scroll 0 0 #B9C9FE; color: #003399; font-size: 13px; font-weight: normal; padding: 8px;}
		.server_selector td { background: none repeat scroll 0 0 #E8EDFF; border-top: 1px solid #FFFFFF; color: #666699; padding: 8px;}	
		
	</style>
	<!-- following are definitions for the dialog boc from jquery UI -->
	<style>
		.ui-dialog-title {
			float:none !important;
			display: block;
			text-align: center;
		}
		
		.ui-dialog-content {
			text-align: center;
		}
	</style>
		
		
	</style>	
	<script>
	//Global variables
		var num_tabs_original; // This varaible is used to show the number of tabs created when the UI is launched at the beginning
		var num_of_plots = 0; // This variable is used to know the number of plots created in the system.
		var open_plots = new Array(); // This array contains the online plots ongoing, which is used for upating the values.
		var debug_console=true; // This variable is used to add console.log funcionality or disable it.
		var type_of_analyze="online"; // This variable is used to know if we are doing an online analysos or offline, becouse the behaviour of the system is different.
		var number_returned_metrics=180; // Ths values is generated for limiting the number of metrcis answered by the backend, and limit so the network traffic. 
		

	// This function is ised for configuring the aler mgs box using the dialog UI jquery		
		$.extend({ alert: function (message, title) {
		  $("<div id=\"dialog\"></div>").dialog( {
		    buttons: { "Ok": function () { $(this).dialog("close"); } },
		    close: function (event, ui) { $(this).remove(); },
		    resizable: false,
		    title: title,
		    modal: true
		  }).text(message);
		  $("#dialog").css({"padding-top": "25px"});
		}
		});		
		
	//Jquery function
		$(function() {
			//Don´t show this element till all info is received
			$( "#analyze_server" ).hide();
			// Configuration related to the tabs of the jquery ui
			$('#tabs').tabs({ active: <%= typeofshow %> });
			$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
			//Original number of tags in the system
			num_tabs_original = $("div#tabs ul li.ui-state-default").length;
			update_stat_server_info();
			// Ajax send for collection data related to server stadistics.
			// If tab2 is selected, enable the tab3
			$('#tabs').tabs({ 
				activate: function (event, ui) { 
					var active = $("#tabs").tabs("option", "active");
					var activated=$("#tabs ul>li a").eq(active).attr('href');
					//myconsolelog(activated);
					//myconsolelog($('#tabsonline').attr('href'));
					if (activated == '#tabs-statsrv'){
						//refresh the info of stats server
						update_stat_server_info();
					}
					
					if (activated == '#tabs-remsrv'){
						//refresh the info of stats server
						//Don´t show info till is not received from backend
						$(' #remoteServer').hide();
						update_remote_servers_info("remsrv");
					}
					if (activated == '#tabs-metrics'){
						//refresh the info of stats server
						update_remote_servers_info("metrics");
						//Buttons for statistics are hide till are selected
						$( "#analyze_server_stat").empty();
						$( "#analyze_metrics_stat").empty();
					}
				} 
			});
			//Make the selectable list for the jquery UI in the remothe statistics tab
			//Configuration for the selectable list
			$( "#selectable" ).selectable();
			$( "#selectable" ).selectable({ 
				selected: function(event, ui) { 
					//myconsolelog(event);			 	
					//myconsolelog(ui.selected);
					var value_selected=$("li.ui-widget-content.ui-selectee.ui-selected").text()
					var statServer=value_selected.split(" ( ");
					nameServer=statServer[0];
					var ipserver=(statServer[1].split(" )")[0]);
					update_remserver_info(nameServer,ipserver);
				}
			});
			// Button for main page when access is click
			$( "#main_button" ).button();
			$("#main_button").click(function() {
				$( '#tabs' ).tabs("enable", 1 );
				$( '#tabs' ).tabs("enable", 2 );
				$( '#tabs' ).tabs("enable", 3 );
				$('#tabs').tabs({ active: 1});
				
				
			});
			// Button for updating the stadistics server properties
			$( "#update_button" ).button();
			$("#update_button").click(function() {
				$.alert("update the server","ALERT");
				var srvDatabase=$('#txtStatsDatabase').val();
				var srvType=$('#txtStatsDatabaseType').val();
				var srvIP=$('#txtStatsIpAddress').val();
				var srvPort=$('#txStatsPort').val();
				var srvName=$('#txtName').val();
				var srvStatsCollect=$('#txtStatsCollect').val();
				var radioStatus=$('#radioStatus-1:checked').val();
				var radioMonitor=$('#radioMonitor-1:checked').val();
				var srvDays=$('#txSavedDays').val();
				
				if (radioStatus){
					var Status=true;
				}
				else{
					var Status=false;
				}
				
				if (radioMonitor){
					var Monitor=true;
				}
				else{
					var Monitor=false;
				}
				//myconsolelog(Status);
				//myconsolelog(Monitor);
				$.ajax({ // ajax call when a selectable is done in the list.
						type:'GET',
						url: 'updateStatServer.jsp', // JQuery loads from jsp file
						data: "srvStatsCollect="+srvStatsCollect+"&srvDays="+srvDays+"&srvDatabase="+srvDatabase+"&srvType="+srvType+"&srvIP="+srvIP+"&srvPort="+srvPort+"&srvName="+srvName+"&Status="+Status+"&Monitor="+Monitor,
						dataType: 'json', // Choosing a JSON datatype
						success: function(result) // Variable data contains the data we get from serverside
							{
								if(result){
								//myconsolelog("OK");
								}
							},
						error: function (xhr, ajaxOptions, thrownError) {
							$.alert("response " + xhr.status +" : "  +thrownError,"ERROR");
							$('#tabs').tabs({ active: 0 });
							$( '#tabs' ).tabs({ disabled: [ 1,2,3 ] });
								
						}						
							
					});
				
			});
		$( "#accordion" ).accordion();
		$('#accordion >div').css('height', "100%");
		
		// This function is used to dynamic add the content inside the accordion part, it can be for offline stattistics or for online depending in the input value.
		function add_content_accordion(accordion){
			if (accordion == "accordion_online" ){
				type_of_analyze="online";
				$( "#"+accordion).empty();
				$( "#"+accordion).append("<div id=\"analyze_server\"></div>");
				$( "#analyze_server").append("Server to analyze :");		
				$( "#analyze_server").append("<table summary=\"Monitored servers\" id=\"server_selector\" class=\"server_selector\"></table>");
				$( "#server_selector").append("<thead><tr><th class=\"server_select_cl\" scope=\"col\">Selected</th><th class=\"server_name_cl\" scope=\"col\">Server Name</th><th class=\"server_ip_cl\" scope=\"col\">Server Ip address</th><th class=\"server_status_cl\" scope=\"col\">Status</th><th class=\"server_availability_cl\" scope=\"col\">Availability</th></tr></thead>");
				$( "#server_selector").append("<tbody id=\"server_selector_body\"></tbody>");
				$( "#analyze_server").append("<div id=\"analyze_server_options\"><br>Minutes to analyze : <input size=\"0\" type=\"text\" name=\"txtAnalyzeMinutes\" id=\"txtAnalyzeMinutes\" ></div>");
				$( "#txtAnalyzeMinutes")// event handler
					.keyup(resizeInput)
					// resize on page load
					.each(resizeInput);
				$( "#analyze_server").append("<div id=\"analyze_server_stat\"><div id=\"analyze_server_type_stat\"></div></div>");
				$( "#analyze_server").append("<div id=\"analyze_metrics_stat\"><div id=\"analyze_server_type_metrics\"></div>");
				}
			else{ // This is accordion offline
				
				type_of_analyze="offline";
				$( "#"+accordion).empty();
				$( "#"+accordion).append("<div id=\"analyze_server\"></div>");
				$( "#analyze_server").append("Server to analyze :");		
				$( "#analyze_server").append("<table summary=\"Monitored servers\" id=\"server_selector\" class=\"server_selector\"></table>");
				$( "#server_selector").append("<thead><tr><th class=\"server_select_cl\" scope=\"col\">Selected</th><th class=\"server_name_cl\" scope=\"col\">Server Name</th><th class=\"server_ip_cl\" scope=\"col\">Server Ip address</th><th class=\"server_status_cl\" scope=\"col\">Status</th><th class=\"server_availability_cl\" scope=\"col\">Availability</th></tr></thead>");
				$( "#server_selector").append("<tbody id=\"server_selector_body\"></tbody>");
				$( "#analyze_server").append("<div id=\"analyze_server_options\"><br>Start Date to Analyze : <input size=\"0\" type=\"text\" name=\"txtStartDate\" id=\"txtStartDate\" value=\""+get_date_and_format(-1)+"\">(Format YYYY-MM-DD HH:MM)</div>");
				$( "#txtStartDate")// event handler
					.keyup(resizeInput)
					// resize on page load
					.each(resizeInput);
				$( "#analyze_server").append("<div id=\"analyze_server_options\"><br>End Date  to Analyze : <input size=\"0\" type=\"text\" name=\"txtEndDate\" id=\"txtEndDate\" value=\""+get_date_and_format(0)+"\" style=\"margin-left: 4px;\"> (Format YYYY-MM-DD HH:MM) </div>");
				$( "#txtEndDate")// event handler
					.keyup(resizeInput)
					// resize on page load
					.each(resizeInput);
					
				$( "#analyze_server").append("<div id=\"analyze_server_stat\"><div id=\"analyze_server_type_stat\"></div></div>");
				$( "#analyze_server").append("<div id=\"analyze_metrics_stat\"><div id=\"analyze_server_type_metrics\"></div>");
			}

		}
		
		//When the accordion tabs is changed from one value to the other, we will regenerated the contents of the accoirdion.
		$( "#accordion" ).accordion({
			activate: function (event, ui) {
				accordion_selector=$('#accordion').accordion('option', 'active');
					if ( accordion_selector == 0){
						$( "#accordion_online").empty();
						$( "#accordion_offline").empty();
						add_content_accordion("accordion_online"); // Add contents for the accordion part
						update_remote_servers_info("metrics");
						$( "#analyze_server_stat").empty();
						$( "#analyze_metrics_stat").empty();
						
						
					}
					else{
						$( "#accordion_online").empty();
						$( "#accordion_offline").empty();
						add_content_accordion("accordion_offline"); // Add contents for the accordion part
						update_remote_servers_info("metrics");
						$( "#analyze_server_stat").empty();
						$( "#analyze_metrics_stat").empty();

						
					}
				}
			});
		
		//If we click in a selected server process the following funtion
		$("#sel_server_0").change(function() {
			$.alert("sel_server selected","ALERT");
		});
		
		//input text will automatically autoadjust
		$('input[type="text"]')
			// event handler
			.keyup(resizeInput)
			// resize on page load
			.each(resizeInput);
			
		$( "#analyze_server_type_stat" ).buttonset();
		
		// This function is used for updating the online plots that are stablished in the ui
		function update() {
			myconsolelog(open_plots.length);
			if (open_plots.length > 0 ){
					
		
					//plot.setData([getRandomData()]);
		
					// Since the axes don't change, we don't need to call plot.setupGrid()
		
					//plot.draw();
					myconsolelog(open_plots);
					for (i=0;i<open_plots.length;i++){
						if ( open_plots[i].typeplot == "online" ){ //Only if is an online plot we are going to replot every 20000 secongs
						myconsolelog("call display_online_chart with parameters = "+ open_plots[i].actual_plot+","+
							open_plots[i].tittle_id+","+
							open_plots[i].actual_container+","+
							open_plots[i].namesrv+","+open_plots[i].ipsrv+","+open_plots[i].typestat+","+
							open_plots[i].typemetric+","+open_plots[i].minutestochk);
						display_online_chart(open_plots[i].actual_plot,open_plots[i].tittle_id,open_plots[i].namesrv,open_plots[i].ipsrv,open_plots[i].typestat,open_plots[i].typemetric,open_plots[i].minutestochk,number_returned_metrics);
						}
						
					}
			}
			setTimeout(update, 20000);			
			
		}

		update();
		
		
		// Following funtions are used to update the plot on its  window when this is resized
		var rtime = new Date(1, 1, 2000, 12,00,00);
		var timeout = false;
		var delta = 200;
		$(window).resize(function() {
		    rtime = new Date();
		    if (timeout === false) {
			timeout = true;
			setTimeout(resizeend, delta);
		    }
		});
		
		function resizeend() {
		    if (new Date() - rtime < delta) {
			setTimeout(resizeend, delta);
		    } else {
			timeout = false;
			myconsolelog('Resizing, check if I am in a plot window');
			actual_tab= $("div#tabs ul li.ui-state-active").attr('aria-controls');
			myconsolelog(actual_tab);
			myconsolelog(actual_tab.search("tabs-monitor"));
			if (actual_tab.search("tabs-monitor") >= 0){
				var number_tab=actual_tab.replace("tabs-monitor-",""); 
				myconsolelog("We are in a monitoring window : number "+ number_tab);
				var plot_number=number_tab-1;
				var container_id="ContainerDiv-"+plot_number;
				var plot_id="ChartDiv-"+plot_number;
				
				with_tabs=($( "#tabs" ).width()-100); //get the with of the tabs
				myconsolelog("with_tabs = " + with_tabs);
				var css_size={"width":with_tabs}
				$("#"+container_id).css(css_size);
				
				with_container=($("#"+container_id).width()); //get the with of the container
				myconsolelog("with_container = " + with_container);				
				css_size={"width":with_container}
				$("#"+plot_id).css(css_size);
				
			}

		    }               
		}
	
		
	});
	</script>
	
	<style>
	body{
		font: 62.5% "Trebuchet MS", sans-serif;
		margin: 50px;
	}
	</style>
	
</head>
<body >

<!-- Tabs Section of the Jquery UI-->
<div id="tabs">
	<ul>
		<li><a href="#tabs-main">Main Owly Page</a></li>
		<li><a href="#tabs-metrics">Analyze Statistics</a></li>
		<li><a href="#tabs-remsrv">Remote Server Conf.</a></li>
		<li><a href="#tabs-statsrv">StatServer Configuration</a></li>
		
	</ul>
	<div id="tabs-main">
	In this page we will write some description about the used Owly Application
	<br>
	<button id="main_button">Access</button>
	</div>
	
	<!-- Part related to the stadistics Server -->
	<div id="tabs-statsrv">
		<table style="text-align: left; width: 790px; height: 108px;"
	 border="0" cellpadding="5" cellspacing="2">
	  <tbody>
	    <tr>
	      <td>Database &nbsp;Characteristics</td>
	      <td>Database Name </td>
	      <td><input readonly="readonly" type="text" id="txtStatsDatabase"
	 name="txtStatsDatabase"  size="40"></td>
	    </tr>
	    <tr>
	      <td></td>
	      <td>Database Type</td>
	      <td><input readonly="readonly" type="text" 
	 name="txtStatsDatabaseType" id="txtStatsDatabaseType" size="40"></td>
	    </tr>
	    <tr>
	      <td></td>
	      <td>IP address</td>
	      <td><input readonly="readonly" type="text" 
	 name="txtStatsIpAddress" id="txtStatsIpAddress" size="40"></td>
	    </tr>
	    <tr>
	      <td></td>
	      <td>Database Port</td>
	      <td><input readonly="readonly" type="text"  name="txStatsPort" id="txStatsPort"
	 size="20"></td>
	    </tr>
	    <tr>
	      <td>Collection&nbsp;</td>
	      <td>Campaing Name</td>
	      <td><input readonly="readonly" type="text"  name="txtName" id="txtName"
	 size="40"></td>
	    </tr>
	    <tr>
	      <td></td>
	      <td>Collection Name</td>
	      <td><input readonly="readonly" type="text"  id="txtStatsCollect"
	 name="txtStatsCollect" size="40"></td>
	    </tr>
	    <tr>
	      <td>Configuration </td>
	      <td>Server Status </td>
	      <td>Enabled<input name="radioStatus" id="radioStatus-1"
	 value="Enabled" type="radio"> &nbsp; Disabled<input
	 name="radioStatus"  id="radioStatus-2" value="Disabled" type="radio">
	      </td>
	    </tr>
	    <tr>
	      <td></td>
	      <td>Monitoring Status</td>
	      <td>Enabled<input name="radioMonitor" id="radioMonitor-1"
	 value="Enabled" type="radio"> &nbsp; Disabled<input
	 name="radioMonitor"  id="radioMonitor-2" value="Disabled" type="radio">
	      </td>
	    </tr>
	    <tr>
	      <td></td>
	      <td>Saved Days</td>
	      <td><input name="txSavedDays" type="text"  id="txSavedDays"
	 size="20"></td>
	    </tr>
	  </tbody>
	</table>	
	<button id="update_button">Update configuration values</button>
	</div>
	
	
	<!-- Part related to the remote servers-->
	<div id="tabs-remsrv" >
		<table style="text-align: left; width: 80%; height: 80%;" border="0" cellpadding="2" cellspacing="2">
			<tr>
				<td style="height: 100%; width: 30%; vertical-align: top;">
					<ol id="selectable">
						<!-- This element will be added dinamically -->
						<!-- <li class="ui-widget-content">Item 1</li> -->
					</ol>
				</td>
				<td style="height: 100%; width: 70%; vertical-align: top;">
					<div id="remoteServer">
					<table style="text-align: left; width: 100%; height: 253px;"
					 border="0" cellpadding="5" cellspacing="2">
					  <tbody>
					    <tr>
					      <td style="height: 100%; width: 20%;"></td>
					      <td style="height: 100%; width: 30%;">Server Name </td>
					      <td style="height: 100%; width: 50%;"><input type="text"
					 readonly="readonly" id="txtRemoteServerName"
					 name="txtRemoteServerName" size="40" /></td>
					    </tr>
					    <tr>
					      <td></td>
					      <td>IP address</td>
					      <td><input readonly="readonly" type="text"
					 name="txtStatsIpAddress" id="txtStatsIpAddress" size="40" /></td>
					    </tr>
					    <tr>
					      <td></td>
					      <td>Server Type</td>
					      <td><input readonly="readonly" type="text"
					 name="txRemoteServerType" id="txRemoteServerType" size="20" /></td>
					    </tr>
					    <tr>
					      <td></td>
					      <td>Client Port</td>
					      <td><input readonly="readonly"  type="text"
					 name="txtRemoteClientPort" id="txtRemoteClientPort" size="40" /></td>
					    </tr>
					    <tr>
					      <td></td>
					      <td>Response Status</td>
					      <td>Enabled<input disabled='disabled'  name="radioResponseStatus"
					 id="radioResponseStatus-1" value="Enabled" type="radio" />
					&nbsp; Disabled<input disabled='disabled'  name="radioResponseStatus"
					 id="radioResponseStatus-2" value="Disabled" type="radio" />
					      </td>
					    </tr>
					    <tr>
					      <td></td>
					      <td>Server Availability </td>
					      <td>Enabled<input disabled='disabled'  name="radioRemoteStatus"
					 id="radioRemoteStatus-1" value="Enabled" type="radio" />
					&nbsp; Disabled<input disabled='disabled'  name="radioRemoteStatus"
					 id="radioRemoteStatus-2" value="Disabled" type="radio" />
					      </td>
					    </tr>
					    <tr>
					      <td></td>
					      <td style="vertical-align: top;">Statistics to process</td>
					      <td>
					      <ul id="listStat">
							<!-- this are element to add dynmically <li>Stat1</li>-->							
						</ul>
					      </td>
					    </tr>
					  </tbody>
					</table>
					</div>
				</td>
			</tr>
		</table>
			
	
	
	</div>
	<!-- Part related to collection of stadistics-->
	<div id="tabs-metrics" >
			<!-- Accordion -->
			<div id="accordion" >
				<h3>Online Analysis</h3>
				<div id="accordion_online">
					<div id="analyze_server">
						Server to analyze :					
						<table summary="Monitored servers" id="server_selector" class="server_selector">
							<thead>
								<tr>
									<th class="server_select_cl" scope="col">Selected</th>
									<th class="server_name_cl" scope="col">Server Name</th>
									<th class="server_ip_cl" scope="col">Server Ip address</th>
									<th class="server_status_cl" scope="col">Status</th>
									<th class="server_availability_cl" scope="col">Availability</th>
								</tr>
							</thead>
							<tbody id="server_selector_body">
								<!-- example of element<tr>
									<td>x</td>
									<td>Microsoft</td>
									<td>20.3</td>
									<td>30.5</td>
									<td>23.5</td>
								</tr>-->
							</tbody>
						</table>
						<div id="analyze_server_options">
							<br>
							Minutes to analyze : <input size="40" type="text" name="txtAnalyzeMinutes" id="txtAnalyzeMinutes" >
						</div>
							<div id='analyze_server_stat'>
								<div id="analyze_server_type_stat">
								</div>
							</div>
					
							<div id='analyze_metrics_stat'>
								<div id="analyze_server_type_metrics">
								</div>
							</div>
						</div>
				</div>
				<h3>Offline Analysis</h3>
				<div id="accordion_offline" >

				</div>
			</div>
	</div>
</div>
</body>
</html>
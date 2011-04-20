<%@ page errorPage="error.jsp" %>
<%@ taglib uri="av-tag" prefix="av" %>
<av:check_parameters/>
<HTML>
<HEAD>
	<TITLE> 
		ArchiveViewer JSP
	</TITLE>
</HEAD>
<BODY BGCOLOR="white">
<form method="POST" action="<av:encode_url url="processPVConfigurationForPlot"/>" >
<table border="1" cellpadding="5px" width="100%">
	<tr>
		<%@ include file="graphs_plot_configurator.jsp" %>
		<%@ include file="time_axes_plot_configurator.jsp" %>
		<%@ include file="range_axes_plot_configurator.jsp" %>
	</tr>
	<tr>
		<td></td>
		<td>
			<div style="position:relative;margin-top:20px;text-align:center">
			New Time Axis Label	
				<input name="new_time_axis" type="text" size="20"/>
				<input type="submit" value="Add"/>
			</div>
		</td>
		<td>
			<div style="position:relative;margin-top:20px; text-align:center">	
				New Range Axis Label
				<input name="new_range_axis" type="text" size="20"/>
				<input type="submit" value="Add"/>
			</div>
		</td>
	</tr>
	<tr>
		<td>
			Plot Title
			<input 
				name="plot_title" 
				type="text" 
				size="20"
				value="<av:print_plot_title/>"
			/>
		</td>
		<%@ include file="plot_legend_configurator.jsp" %>
		<td align="right" valign="bottom">
			<table border="0">
				<tr>
					<td>
						Width (px)
						<input 
							name="width" 
							type="text" 
							size="5"
							value="<av:print_request_parameter parameter="width"/>"
						/> 
					</td>
					<td>
						Height (px)
						<input 
							name="height" 
							type="text" 
							size="5"
							value="<av:print_request_parameter parameter="height"/>"
						/> 
					</td>
					<td>
						<input type="submit" value="Submit"/>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
</BODY>
</HTML>
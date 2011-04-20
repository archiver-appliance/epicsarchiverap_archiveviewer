<%@ page errorPage="error.jsp" %>
<%@ taglib uri="av-tag" prefix="av" %>
<av:check_parameters/>
<td valign="top">
	<av:loop set="graphs">
	<div style="position:relative;margin-top:20px">
		<div>
			<b>
				<av:print_graph_parameter index="<%= counter.intValue() %>" parameter="name"/>
			</b>
			<br/>
			<b>
				<av:print_graph_parameter index="<%= counter.intValue() %>" parameter="directory"/>
			</b>
		</div>
		<table border="0">
			<tr>
				<td>Time Axis</td>
				<td align="right">
					<select name="time_axis" size="1">
						<av:print_graph_parameter index="<%= counter.intValue() %>" parameter="time_axis"/> 
					</select>
				</td>
			</tr>
			<tr>
				<td>Range Axis</td>
				<td align="right">
					<select name="range_axis" size="1">
						<av:print_graph_parameter index="<%= counter.intValue() %>" parameter="range_axis"/>
					</select>
				</td>
			</tr>
			<tr>
				
				<td><a href="color_chart.html" target="_blank">Color</a></td>
				<td align="center">
					<div 
						style="background-color:<av:print_graph_parameter index="<%= counter.intValue() %>" parameter="color"/>"
					>
						<input 
							name="color" 
							type="text" 
							size="7"
							value="<av:print_graph_parameter index="<%= counter.intValue() %>" parameter="color"/>"
						/>
					</div>
						
				</td>
			</tr>
			<tr>
				<td>Type</td>
				<td align="right">
					<select name="draw_type" size="1">
						<av:print_graph_parameter index="<%= counter.intValue() %>" parameter="draw_type"/>
					</select>
				</td>
			</tr>
		</table>
	</div>
 	</av:loop>
</td>
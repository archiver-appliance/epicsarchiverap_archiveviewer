<%@ page errorPage="error.jsp" %>
<%@ taglib uri="av-tag" prefix="av" %>
<av:check_parameters/>
<td valign="top">
	<av:loop set="time_axes">
	<div style="position:relative;margin-top:20px">
		<b>
			<av:print_time_axis_parameter index="<%= counter.intValue() %>" parameter="name"/>
		</b>
	</div>
	<table border="0">
		<tr>
			<td>
			Start Time
			</td>
			<td>
			<input 
				name="start_time" 
				type="text" 
				size="30"
				value="
				<av:print_time_axis_parameter index="<%= counter.intValue() %>" parameter="starttime"/>
			"/>
			</td>
		</tr>
		<tr>
			<td>
			End Time
			</td>
			<td>
			<input 
				name="end_time" 
				type="text" 
				size="30"
				value="
				<av:print_time_axis_parameter index="<%= counter.intValue() %>" parameter="endtime"/>
			"/>
			</td>
		</tr>
		<tr>
			<td>
			Location
			</td>
			<td>
			<select name="time_axis_location" size="1">
				<av:print_time_axis_parameter index="<%= counter.intValue() %>" parameter="location"/>				
			</select>
			</td>
		</tr>
		
	</table>
	</av:loop>
</td>		
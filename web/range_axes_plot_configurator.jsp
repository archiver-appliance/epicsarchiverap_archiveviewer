<%@ page errorPage="error.jsp" %>
<%@ taglib uri="av-tag" prefix="av" %>
<av:check_parameters/>
<td valign="top">
	<av:loop set="range_axes">
	<div style="position:relative;margin-top:20px">
		<b>
			<av:print_range_axis_parameter index="<%= counter.intValue() %>" parameter="name"/>
		</b>
	</div>
	<table border="0">
		<tr>
			<td>
			Min
			</td>
			<td>
			<input 
				name="min" 
				type="text" 
				size="30"
				value="
				<av:print_range_axis_parameter index="<%= counter.intValue() %>" parameter="min"/>
			"/>
			</td>
		</tr>
		<tr>
			<td>
			Max
			</td>
			<td>
			<input 
				name="max" 
				type="text" 
				size="30"
				value="
				<av:print_range_axis_parameter index="<%= counter.intValue() %>" parameter="max"/>
			"/>
			</td>
		</tr>
		<tr>
			<td>
			Type
			</td>
			<td>
			<select name="axis_type" size="1">
				<av:print_range_axis_parameter index="<%= counter.intValue() %>" parameter="type"/>				
			</select>
			</td>
		</tr>
		<tr>
			<td>
			Location
			</td>
			<td>
			<select name="range_axis_location" size="1">
				<av:print_range_axis_parameter index="<%= counter.intValue() %>" parameter="location"/>				
			</select>
			</td>
		</tr>
		
	</table>
	</av:loop>
</td>		
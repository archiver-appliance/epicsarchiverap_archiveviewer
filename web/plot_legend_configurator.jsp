<%@ page errorPage="error.jsp" %>
<%@ taglib uri="av-tag" prefix="av" %>
<av:check_parameters/>
<td>
	Legend<br/>
	<input 
		type="checkbox" 
		name="legend" 
		value="show_ave_name" 
		<av:print_legend_parameter parameter="show_ave_name"/>
		>AVE name
	</input>
	<input 
		type="checkbox" 
		name="legend" 
		value="show_archive_name" 
		<av:print_legend_parameter parameter="show_archive_name"/>
		>Archive name
	</input>	
	<input 
		type="checkbox" 
		name="legend" 
		value="show_range" 
		<av:print_legend_parameter parameter="show_range"/>
		>Range
	</input>
	<input 
		type="checkbox" 
		name="legend" 
		value="show_units" 
		<av:print_legend_parameter parameter="show_units"/>
		>Units
	</input>
</td>
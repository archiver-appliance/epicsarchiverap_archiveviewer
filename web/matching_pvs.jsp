<%@ page errorPage="error.jsp" %>
<%@ taglib uri="av-tag" prefix="av" %>
<av:check_parameters/>
<HTML>
<HEAD><TITLE> 
	ArchiveViewer JSP
</TITLE></HEAD>


<BODY BGCOLOR="white">
<center>
	<font size="2">
		Select PVs by checking boxes next to their names
	</font>
</center>
<form method="POST" action="<av:encode_url url="processPVSelection"/>">
<table border="1" border-width="1">
	<tr bgcolor="#FFFF00">
		<th>
			Archive Entry
		</th>
		<th>
			Archive Directory
		</th>
		<th>
			Archiving Start Time
		</th>
		<th>
			Archiving End Time
		</th>
	</tr>
<av:loop set="matching_pvs">
	<tr>
		<td>
			<input 
				type="checkbox" 
				name="pv_index" 
				value="<%= counter.intValue() %>"
			>
			<av:print_matching_pv_parameter index="<%= counter.intValue() %>" parameter="name"/>
		</td>
		<td>
			<av:print_matching_pv_parameter index="<%= counter.intValue() %>" parameter="directory"/>
		</td>
		<td>
			<av:print_matching_pv_parameter index="<%= counter.intValue() %>" parameter="starttime"/>
		</td>
		<td>
			<av:print_matching_pv_parameter index="<%= counter.intValue() %>" parameter="endtime"/>
		</td>
	</tr>
</av:loop>
	<tr>
	 <td></td>
	 <td></td>
	 <td align="center">
	 	<input type="radio" name="configuration_mode" value="plot" checked="checked"> Configure for Plot<br>
		<input type="radio" name="configuration_mode" value="export"> Configure for Export<br>
	 </td>
	 <td align="right">
	 	<input type="submit" value="Go"/>
	 </td>
	</tr>
</table>
 
</form>

</BODY>
</HTML>
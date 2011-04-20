<%@ page errorPage="error.jsp" %>
<%@ taglib uri="av-tag" prefix="av" %>
<av:check_parameters/>
<HTML>
<HEAD>
	<TITLE> 
		ArchiveViewer Test JSP
	</TITLE>
</HEAD>
<center>
	<font size="2">
		Select an archive directory from which you want to export data; then proceed with export options.<br>
		Hint: You can edit and/or add your own PV names to the list (one name per line)
	</font>
</center>
<body>
Selected PVs<br>
<form method="POST" action="<av:encode_url url="export"/>">
<textarea name="pv_names" cols="50" rows="10">
<av:loop set="selected_pvs">
	<av:print_selected_pv_parameter index="<%= counter.intValue() %>" parameter="name"/>
</av:loop>
</textarea>
<hr>
 
	Please, select an archive directory to export data from:<br/>
	<select name="pv_directory" size="1">
		<av:print_ads>
			<av:print_selected_pv_parameter index="0" parameter="directory"/>
		</av:print_ads>
	</select>
	<table border="0">
	<tr>
		<td>
			Start time
			<br>
			<input name="start_time" type="text" size="30" value="-1d"/>
		</td>
		<td>
			End time
			<br>
			<input name="end_time" type="text" size="30" value="now"/>
		</td>
	</tr>
	<tr>
		<td>
			Number of values
			<br>
			<input name="nr_values" type="text" size="10" value="1000"/>
		</td>
		<td>
			Choose a retrieval method
			<br>
			<select name="retrieval_method" size="1">
				<av:print_methods purpose="export"/>
			</select>
		</td>
	</tr>
	<tr>
		<td>
			<input type="checkbox" name="export_status" value="status">Export Status<br>
		</td>
		<td>
			Custom Timestamp Format<br>
			<font size="-2">(MM/dd/yyyy HH:mm:ss.SSS)</font>:
			<br>
			<input name="ts_format" type="text" size="30"/>
		</td>
	</tr>
	<tr>
		<td>
			Export as...<br>
			<select name="exporter_id" size="1">
				<av:print_exporter_ids/>
			</select>
		</td>
		<td align="right" valign="bottom">
			<input type="submit" value="Export"/>
		</td>
	</tr>
	</table>
</form>
</BODY>
</HTML>
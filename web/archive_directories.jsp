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
<center>
	<font size="2">
		Select one (or more) archive directories and enter a search string,
		according to the rules common for the Unix shell (*, ? may be used)
	</font>
</center>
<form method="POST" action="<av:encode_url url="search"/>">
<table border="0">
	<tr>
		<td>
			<select name="archive_dirs" size="10" multiple="multiple">
				<av:print_ads/>
			</select>
		 </td>
		 <td valign="middle">
		 	Search string (glob pattern):<br>
		 	<input name="glob_expression" type="text" size="30"/>
		 </td>
		 <td align="right" valign="middle">
		 	<input type="submit" value="Search"/>
		 </td>
	</tr>
</table>
</form>

</BODY>
</HTML>





<%@page isErrorPage="true" %>
<%@ taglib uri="av-tag" prefix="av" %>
<av:check_parameters/>
<html>
<head>
<title>ArchiveViewer JSP Error Page</title>
</head>
<body style="color:#FF0000">
<h3>
An error has occured during processing of your request. Please, go back and try again.
</h3>
<hr/>
<av:print_full_error/>
</body> 
</html>
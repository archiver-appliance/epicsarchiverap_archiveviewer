<%@ page errorPage="error.jsp" %>
<av:check_parameters/>
<html>
<head>
<meta http-equiv="expires" content="0"/>
</head>
<body>
<img src="<%= "images/" + request.getSession().getId() + ".png" %>"/>
</body>
</html>
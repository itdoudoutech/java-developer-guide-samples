<head>
    <jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf"/>
    <title>My Home Page</title>
</head>
<body style="text-align: center">
<br/>
<form action="/register/submit" method="post">
    username:&nbsp;&nbsp;<input type="text" name="username" required="required" placeholder="input username"/> <br/>
    password:&nbsp;&nbsp;<input type="password" name="password" required="required" placeholder="input password"/><br/>
    phone:&nbsp;&nbsp;<input type="text" name="phone" required="required" placeholder="input phone"/> <br/><br/>
    <input type="submit" value="submit">
</form>
</body>
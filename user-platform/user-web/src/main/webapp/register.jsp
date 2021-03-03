<head>
    <jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf"/>
    <title>My Home Page</title>
</head>
<body style="text-align: center">
<form action="/register/submit" method="post">
    username:&nbsp;&nbsp;<input type="text" name="username" placeholder="input username"/> <br/>
    password:&nbsp;&nbsp;<input type="password" name="password" placeholder="input password"/><br/><br/>
    <input type="submit" value="submit">
</form>
</body>
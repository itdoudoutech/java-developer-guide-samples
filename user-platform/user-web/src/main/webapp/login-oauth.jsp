<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
	<style>
		.container-lg{
			margin-top: 20px;
		}
	</style>
</head>
<body>
	<div class="container-lg">
		Login With GitHub: <a href="https://github.com/login/oauth/authorize?client_id=${client_id}">click here</a>
	</div>
</body>
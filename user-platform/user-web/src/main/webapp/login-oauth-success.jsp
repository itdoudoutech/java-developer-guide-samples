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
		<h2>Welcome ${name}!</h2>
		<img src="${avatar_url}">
		<p>
			If you see this page,it means that you have login with GitHub successfully. Congratulations to you!
		</p>
	</div>
</body>
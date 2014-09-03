<%@ taglib prefix="jwr" uri="http://jawr.net/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<jwr:style src="/resources/theme/common/css/all.css" />
<link rel="shortcut icon" href="<jwr:imagePath src="/resources/img/favicon.ico" />" type="image/x-icon" />


</head>

<body>
	<div style="height: 20px"> 
		<jwr:img src="/resources/img/cog.png" />
		This HTML image use a generated path which force the caching for the
		browser.
		This is a test <a href="<c:url value='/'/>">link</a>
	</div>
</body>

</html>
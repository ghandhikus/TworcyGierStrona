<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="/resources/js/jquery-2.1.4.min.js" var="jquery" />
<spring:url value="/resources/js/jquery.color-2.1.2.min.js" var="jqueryColor" />
<spring:url value="/resources/jquery-ui/jquery-ui.min.js" var="jqueryUI" />
<spring:url value="/resources/jquery-ui/jquery-ui.min.css" var="jqueryUIcss" />
<spring:url value="/resources/jquery-ui/jquery-ui.structure.min.css" var="jqueryUIStructurecss" />
<spring:url value="/resources/jquery-ui/jquery-ui.theme.min.css" var="jqueryUIThemecss" />
<!-- JQUERY - minified JavaScript -->
<script src="${jquery}"></script>
<!-- JQUERY - minified color -->
<script src="${jqueryColor}"></script>
<!-- JQUERY - minified UI and it's CSS -->
<script src="${jqueryUI}"></script>
<link href="${jqueryUIcss}" rel="stylesheet" type="text/css">
<link href="${jqueryUIStructurecss}" rel="stylesheet" type="text/css">
<link href="${jqueryUIThemecss}" rel="stylesheet" type="text/css">
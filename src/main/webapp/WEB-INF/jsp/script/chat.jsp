<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="/chat/ajax" var="chatAjax" />
<spring:url value="/resources/js/js_storage.js" var="jsStorage" />
<spring:url value="/resources/js/chat.js" var="jsChat" />
<spring:url value="/resources/css/chat.css" var="cssChat" />

<link rel="stylesheet" type="text/css" href="${cssChat}">
<script src="${jsStorage}"></script>
<script type="text/javascript"> var ChatSettings = ChatSettings || { url : "${chatAjax}", } </script>
<script src="${jsChat}"></script>

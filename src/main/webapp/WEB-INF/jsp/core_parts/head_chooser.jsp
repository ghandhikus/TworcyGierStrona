<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />

<c:choose>
<%-- Maren --%>
	<c:when test="${nav.onPageSpecific(currentPage, 'maren')}">
		<link rel="stylesheet" href="<spring:url value="/resources/maren/in_game.css" />" />
	</c:when>
</c:choose>
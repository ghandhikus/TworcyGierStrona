<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- Current Page Variable --%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />

<c:choose>
<%-- ### Accounts --%>
	<%-- Register --%>
    <c:when test="${nav.onPage(currentPage, 'register')}">
		<jsp:include page="content/register.jsp" />
    </c:when>
	<%-- Login --%>
    <c:when test="${nav.onPage(currentPage, 'login')}">
		<jsp:include page="content/login.jsp" />
    </c:when>
    
<%-- ### Profile --%>
	<%-- Account Profile --%>
    <c:when test="${nav.onPageSpecific(currentPage, 'profile')}">
		<jsp:include page="content/profile/profile_single.jsp" />
    </c:when>
	<%-- Specific --%>
    <c:when test="${nav.onPage(currentPage, 'profileSpecific')}">
		<jsp:include page="content/profile/profile_single.jsp" />
    </c:when>
	<%-- Edit --%>
    <c:when test="${nav.onPage(currentPage, 'profileEdit')}">
		<jsp:include page="content/profile/profile_edit.jsp" />
    </c:when>


<%-- ### News --%>
	<%-- Specific --%>
    <c:when test="${nav.onPage(currentPage, 'specificNews')}">
		<jsp:include page="content/news/news_single.jsp" />
    </c:when>
	<%-- Edit --%>
    <c:when test="${nav.onPage(currentPage, 'editNews')}">
		<jsp:include page="content/news/news_edit.jsp" />
    </c:when>
	<%-- New --%>
    <c:when test="${nav.onPage(currentPage, 'newNews')}">
		<jsp:include page="content/news/news_new.jsp" />
    </c:when>
	<%-- List --%>
    <c:when test="${nav.onPage(currentPage, 'listNews') || (newsList!=null && gameList==null)}">
		<jsp:include page="content/news/news_list.jsp" />
    </c:when>
    
<%-- ### Games --%>
	<%-- Specific --%>
    <c:when test="${nav.onPage(currentPage, 'specificGame')}">
		<jsp:include page="content/game/game_single.jsp" />
    </c:when>
	<%-- Edit --%>
    <c:when test="${nav.onPage(currentPage, 'editGame')}">
		<jsp:include page="content/game/game_edit.jsp" />
    </c:when>
	<%-- New --%>
    <c:when test="${nav.onPage(currentPage, 'newGame')}">
		<jsp:include page="content/game/game_new.jsp" />
    </c:when>
	<%-- List --%>
    <c:when test="${nav.onPage(currentPage, 'listGames') || (newsList==null && gameList!=null)}">
		<jsp:include page="content/game/game_list.jsp" />
    </c:when>

<%-- ### Other --%>

	<%-- Maren Game --%>
	<c:when test="${nav.onPage(currentPage, 'maren')}">
		<jsp:include page="content/maren.jsp" />
	</c:when>
	
	<%-- Default --%>
    <c:otherwise>
		<jsp:include page="content/main_page.jsp" />
	</c:otherwise>
</c:choose>

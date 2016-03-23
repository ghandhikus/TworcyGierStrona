<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />


<%-- General Navigation --%>
<li <c:if test="${nav.onPageSpecific(currentPage, 'home')}"> class="active"</c:if>>
	<a href="${nav.get('home')}"><i class="fa fa-fw fa-dashboard"></i> Strona główna</a>
</li>

<c:choose>
	<%-- Unlogged Navigation --%>
	<c:when test="${account==null}">
		<li<c:if test="${nav.onPage(currentPage, 'login')}"> class="active"</c:if>>
			<a href="${nav.get('login')}"><i class="fa fa-fw fa-power-off"></i> Zaloguj się</a>
		</li>
		<li<c:if test="${nav.onPage(currentPage, 'register')}"> class="active"</c:if>>
			<a href="${nav.get('login')}"><i class="fa fa-fw fa-power-off"></i> Zarejestruj się</a>
		</li>
	</c:when>
	
	<%-- Logged Navigation --%>
	<c:when test="${account!=null}">
		<li<c:if test="${nav.onPage(currentPage, 'panel')}"> class="active"</c:if>>
			<a href="${nav.get('panel')}"><i class="fa fa-fw fa-file"></i> Panel zarządzania</a>
		</li>
		<li<c:if test="${nav.onPageSpecific(currentPage, 'profile')}"> class="active"</c:if>>
			<a href="${nav.get('profile')}"><i class="fa fa-fw fa-file"></i> Profil konta</a>
		</li>
		<li<c:if test="${nav.onPageSpecific(currentPage, 'maren')}"> class="active"</c:if>>
			<a href="${nav.get('maren')}"><i class="fa fa-fw fa-desktop"></i> Maren Game</a>
		</li>
	</c:when>
</c:choose>

<%-- Misc. --%>
<li<c:if test="${nav.onPage(currentPage, 'news')}"> class="active"</c:if>>
	<a href="${nav.get('listNews')}"><i class="fa fa-fw fa-list"></i> Artykuły</a>
</li>
<li<c:if test="${nav.onPage(currentPage, 'games')}"> class="active"</c:if>>
	<a href="${nav.get('games')}"><i class="fa fa-fw fa-list"></i> Gry</a>
</li>

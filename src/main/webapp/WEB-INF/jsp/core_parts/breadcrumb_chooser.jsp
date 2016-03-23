<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />


<%-- Site Main --%>
<c:choose>
	<%-- Only on main site --%>
	<c:when test="${nav.onPageSpecific(currentPage, 'home')}">
		<li class="active"><i class="fa fa-dashboard"></i> Strona główna</li>
	</c:when>
	<%-- On every other site --%>
	<c:otherwise>
		<li><i class="fa fa-dashboard"></i> <a href="${nav.get('home')}">Strona główna</a></li>
	</c:otherwise>
</c:choose>
<%-- ### Profile --%>
<%-- Account Profile --%>
<c:if test="${nav.onPageSpecific(currentPage, 'profile')}">
	<li><i class="fa fa-list"></i> Profile</li>
	<li class="active"><i class="fa fa-file"></i> Twój profil</li>
</c:if>
<%-- Specific Profile --%>
<c:if test="${nav.onPage(currentPage, 'profileSpecific')}">
	<li><i class="fa fa-list"></i> Profile</li>
	<li class="active"><i class="fa fa-file"></i> Profil konta</li>
</c:if>
<%-- Edit Profile --%>
<c:if test="${nav.onPage(currentPage, 'profileEdit')}">
	<li><i class="fa fa-list"></i> Profile</li>
	<li class="active"><i class="fa fa-file"></i> Edycja profilu</li>
</c:if>

<%-- ### Accounts --%>
<%-- Register --%>
<c:if test="${nav.onPage(currentPage, 'register')}">
	<li><i class="fa fa-list"></i> Konta</li>
	<li class="active"><i class="fa fa-file"></i> Rejestracja</li>
</c:if>

<%-- Login --%>
<c:if test="${nav.onPage(currentPage, 'login')}">
	<li><i class="fa fa-list"></i> Konta</li>
	<li class="active"><i class="fa fa-file"></i> Logowanie</li>
</c:if>

<%-- ### General --%>

<%-- Control Panel --%>
<c:if test="${nav.onPage(currentPage, 'panel')}">
	<li class="active"><i class="fa fa-file"></i> Panel zarządzania</li>
</c:if>

<%-- Maren Game --%>
<c:if test="${nav.onPage(currentPage, 'maren')}">
	<li class="active"><i class="fa fa-desktop"></i> Maren Game</li>
</c:if>

<%-- News --%>
<c:choose>
	<%-- /news/ --%>
	<c:when test="${nav.onPageSpecific(currentPage, 'news')}">
		<li class="active"><i class="fa fa-list"></i> Artykuły</li>
	</c:when>
	<%-- /news/** --%>
	<c:when test="${nav.onPage(currentPage, 'news')}">
		<li><i class="fa fa-list"></i> <a href="${nav.get('news')}">Artykuły</a></li>
		
		<%-- Specific --%>
		<c:if test="${nav.onPage(currentPage, 'specificNews')}"><li class="active"><i class="fa fa-list"></i> ${news.title}</li></c:if>
		
		<%-- Listing --%>
		<c:if test="${nav.onPage(currentPage, 'listNews')}"><li class="active"><i class="fa fa-list"></i> Lista artykułów</li></c:if>
		
		<%-- Adding --%>
		<c:if test="${nav.onPage(currentPage, 'newNews')}"><li class="active"><i class="fa fa-edit"></i> Dodawanie artykułu</li></c:if>
		
		<%-- Edit --%>
		<c:if test="${nav.onPage(currentPage, 'editNews')}"><li class="active"><i class="fa fa-edit"></i> Edytowanie artykułu</li></c:if>
	</c:when>
</c:choose>

<%-- Games --%>
<c:choose>
	<%-- /game/ --%>
	<c:when test="${nav.onPageSpecific(currentPage, 'game')}">
		<li class="active"><i class="fa fa-list"></i> Gry</li>
	</c:when>
	<%-- /game/** --%>
	<c:when test="${nav.onPage(currentPage, 'game')}">
		<li><i class="fa fa-list"></i> <a href="${nav.get('game')}">Gry</a></li>
		
		<%-- Specific --%>
		<c:if test="${nav.onPage(currentPage, 'specificGame')}"><li class="active"><i class="fa fa-list"></i> ${game.title}</li></c:if>
		
		<%-- Listing --%>
		<c:if test="${nav.onPage(currentPage, 'listGames')}"><li class="active"><i class="fa fa-list"></i> Lista gier</li></c:if>
		
		<%-- Adding --%>
		<c:if test="${nav.onPage(currentPage, 'newGame')}"><li class="active"><i class="fa fa-edit"></i> Dodawanie gry</li></c:if>
		
		<%-- Edit --%>
		<c:if test="${nav.onPage(currentPage, 'editGame')}"><li class="active"><i class="fa fa-edit"></i> Edytowanie gry</li></c:if>
	</c:when>
</c:choose>


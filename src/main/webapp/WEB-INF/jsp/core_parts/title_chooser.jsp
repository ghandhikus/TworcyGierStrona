<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />
<c:choose>
	<c:when test="${nav.onPage(currentPage, 'register')}">Twórcy | Rejestracja</c:when>
	<c:when test="${nav.onPage(currentPage, 'login')}">Twórcy | Logowanie</c:when>
	<c:when test="${nav.onPage(currentPage, 'profile')}">Twórcy | Profile</c:when>

	<c:when test="${nav.onPage(currentPage, 'newsSpecific')}">Twórcy | Przeglądanie artykułu</c:when>
	<c:when test="${nav.onPage(currentPage, 'newNews')}">Twórcy | Tworzenie artykułów</c:when>
	<c:when test="${nav.onPage(currentPage, 'newsList')}">Twórcy | Lista artykułów</c:when>
	<c:when test="${nav.onPage(currentPage, 'newsEdit')}">Twórcy | Edytowanie artykułu</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'gameSpecific')}">Twórcy | Przeglądanie gry</c:when>
	<c:when test="${nav.onPage(currentPage, 'editGame')}">Twórcy | Edytowanie gry</c:when>
	<c:when test="${nav.onPage(currentPage, 'newGame')}">Twórcy | Dodawanie gry</c:when>
	<c:when test="${nav.onPage(currentPage, 'listGames')}">Twórcy | Lista gier</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'maren')}">Twórcy | Maren</c:when>
	
	<c:otherwise>Twórcy Gier | Hajsownicy Gimpera 3000</c:otherwise>
</c:choose>
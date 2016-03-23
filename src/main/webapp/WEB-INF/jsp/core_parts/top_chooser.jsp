<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />

<c:choose>
<%-- ### General --%>
	<c:when test="${nav.onPageSpecific(currentPage, 'home')}">
		Strona Główna
		<small>Twórcy Gier | Hajsownicy Gimpera 3000</small>
	</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'panel')}">
        Panel zarządzania
        <small>Krótki opis panelu</small>
	</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'maren')}">
        Maren
        <small>Subheading</small>
	</c:when>
<%-- ### Profiles --%>
	<c:when test="${nav.onPage(currentPage, 'profile')}">
        Profile
        <small>Krótki opis profili</small>
	</c:when>
	
<%-- ### Accounts --%>
	<c:when test="${nav.onPageSpecific(currentPage, 'register')}">
		Rejestracja
		<small>Twórcy Gier | Hajsownicy Gimpera 3000</small>
	</c:when>
	
	<c:when test="${nav.onPageSpecific(currentPage, 'login')}">
		Logowanie
		<small>Twórcy Gier | Hajsownicy Gimpera 3000</small>
	</c:when>
	
	<c:when test="${nav.onPageSpecific(currentPage, 'profile')}">
		Profil
		<small>Twórcy Gier | Hajsownicy Gimpera 3000</small>
	</c:when>
	
<%-- ### News --%>
	<c:when test="${nav.onPage(currentPage, 'newsSpecific')}">
		<c:if test="${news!=null}">
			${news.title}&nbsp;
			<%-- Edit Button on specific --%>
			<c:if test="${nav.onPage(currentPage, 'newsSpecific') == true && account.getAccess()>0}">
				<a href="${nav.get('editNews')}/${news.newsID}" class="btn btn-default fa fa-edit"
					style="font-size: 15px;"> Edytuj</a>
			</c:if>
		</c:if>
		<c:if test="${news==null}">Artykuł nie istnieje</c:if>
	</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'newNews')}">
        Dodawanie artykułu
        <small>Krótki opis</small>
	</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'listNews')}">
        Lista artykułów
        <small>Krótki opis</small>
	</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'editNews')}">
        Edycja artykułu
        <small>Krótki opis</small>
	</c:when>
	
<%-- ### Games --%>
	<c:when test="${nav.onPage(currentPage, 'specificGame')}">
		<c:if test="${game!=null}">
			${game.title}&nbsp;
			<%-- Edit Button on specific --%>
			<c:if test="${nav.onPage(currentPage, 'gameSpecific') == true && account.getAccess()>0}">
				<a href="${nav.get('editGame')}/${game.gameId}" class="btn btn-default fa fa-edit"
					style="font-size: 15px;"> Edytuj</a>
			</c:if>
		</c:if>
		<c:if test="${game==null}">Gra nie istnieje</c:if>
	</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'newGame')}">
        Dodawanie gry
        <small>Krótki opis</small>
	</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'gamesList')}">
        Lista gier
        <small>Krótki opis</small>
	</c:when>
	
	<c:when test="${nav.onPage(currentPage, 'editGame')}">
        Edycja gry
        <small>Krótki opis</small>
	</c:when>
	
<%-- ### Other --%>
	<%-- Maren Game --%>
	<c:when test="${nav.onPage(currentPage, 'maren')}">
        Maren
        <small>Gra</small>
	</c:when>
	
	<c:otherwise>404<small> (debug:{currentPage:"${currentPage}")</small></c:otherwise>
</c:choose>
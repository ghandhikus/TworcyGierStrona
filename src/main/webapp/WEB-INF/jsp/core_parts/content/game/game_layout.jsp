<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />
<spring:url value="/resources/galleria/" var="galleria" />


<c:if test="${game!=null}">
	<c:if test="${nav.onPage(currentPage, 'gameSpecific') == false}">
		<%-- Top Line --%>
		<hr>
		<%-- Title --%>
		<h1>
			<a href="${nav.get('gameSpecific')}${game.gameId}"><c:out value="${game.title}" /></a>
			<%-- Edit Button --%>
			<c:if test="${account.getAccess()>0}">
				<a href="${nav.get('editGame')}/${game.gameId}" class="btn btn-default fa fa-edit"
					style="font-size: 15px;"> Edytuj</a>
			</c:if>
		</h1>
	</c:if>
	
	<%-- Images --%>
	<c:if test="${game.images!=null || game.images.size()>0}">
		<c:if test="${gameShortDesc==true}">
			<%-- Single miniature photo --%>
			<img src="<spring:url value="/resources/upload/games/${game.gameId}/${game.images.get(0)}"/>" alt="Can't load the image"/>
		</c:if>
		<c:if test="${gameShortDesc!=true}">
			<%-- Galleria --%>
			<div class="game galleria">
				<c:forEach var="img" items="${game.images}">
					<img src="<spring:url value="/resources/upload/games/${game.gameId}/${img}"/>" alt="Can't load the image"/>
				</c:forEach>
			</div>
			
			<script>
				$(function(){
				    Galleria.loadTheme('${galleria}/themes/classic/galleria.classic.min.js');
				    Galleria.run('.galleria');
				});
			</script>
		</c:if>
	</c:if>
	
	<%-- Description --%>
	<c:choose>
		<c:when test="${gameShortDesc==true}">
			<%-- No need to split if description is short --%>
			<c:if test="${game.description.length()>512}">
				<p>
					<p class="game description"><c:out value="${htmlUtils.split(bbcode.parse(game.description),512)}" escapeXml="false" />...</p>
					<a href="${nav.get('gameSpecific')}${game.gameId}"><i>Czytaj więcej</i></a>
				</p>
			</c:if>
			<c:if test="${game.description.length()<=512}">
				<p><c:out value="${bbcode.parse(game.description)}" escapeXml="false" /></p>
			</c:if>
		</c:when>
		<c:otherwise>
			<p><c:out value="${bbcode.parse(game.description)}" escapeXml="false" /></p>
		</c:otherwise>
	</c:choose>
	<%-- Author --%>
	<p style="color:#aaa;display:inline-block;">
		dodał <a href="${nav.get('profile')}${game.authorId}">${gameService.getAuthorName(game.authorId)}</a>
	</p>
	<%-- Time --%>
	<c:choose>
		<c:when test="${game.getDateUpdated() != null}">
			<p style="color:#aaa;float:right;">zaktualizowano ${game.getDateAdded().toString("yyyy/MM/dd HH:mm 'UTC'ZZ")}</p>
		</c:when>
		<c:when test="${game.getDateAdded() != null}">
			<p style="color:#aaa;float:right;">dodane ${game.getDateAdded().toString("yyyy/MM/dd HH:mm 'UTC'ZZ")}</p>
		</c:when>
	</c:choose>
</c:if>

<c:if test="${game==null}">
	<h2 style="color:red;">Gra jest pusta</h2>
</c:if>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:if test="${media!=null}">
	<c:choose>
		<%-- Youtube --%>
		<c:when test='${media.startsWith("yt:")}'>
			<%-- Var: yt --%>
			<c:set var="yt" value='${media.replace("yt:","")}' />
			<%-- Galleria display --%>
			<c:if test="${galleria==true}">
				<a href="http://www.youtube.com/watch?v=${yt}"><img src="http://img.youtube.com/vi/${yt}/hqdefault.jpg" /></a>
			</c:if>
			<%-- Image display --%>
			<c:if test="${youtubeImg==true}">
				<img src="http://img.youtube.com/vi/${yt}/hqdefault.jpg" />
			</c:if>
			<%-- Embedded --%>
			<c:if test="${(galleria==null || galleria==false) && (youtubeImg==null || youtubeImg==false)}">
				<iframe style="max-width:560px; max-height:315px;" src="https://www.youtube.com/embed/${yt}" frameborder="0" allowfullscreen></iframe>
			</c:if>
		</c:when>
		
		<%-- IMG Default --%>
		<c:otherwise>
			<img src="<spring:url value="/resources/upload/games/${game.gameId}/${media}"/>"/>
		</c:otherwise>
	</c:choose>
</c:if>
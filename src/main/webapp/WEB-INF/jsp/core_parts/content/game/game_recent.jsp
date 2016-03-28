<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="row">
	<div class="col-lg-12">
	
		<c:if test="${gameList!=null && gameList.size()>0}">
			<c:set var="game" value="${gameList.get(0)}" scope="request" />
		</c:if>
		
		<c:if test="${game!=null}">
			<%-- Get one most recent and display it --%>
			<c:set var="game" value="${gameService.getRecentGames(1, 0).get(0)}" scope="request" />
		</c:if>

		<c:set var="gameShortDesc" value="${true}" scope="request" />
		<jsp:include page="game_layout.jsp"/>
	</div>
</div>
<!-- /.row -->
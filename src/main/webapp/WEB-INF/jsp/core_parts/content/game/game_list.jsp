<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="row">
	<div class="col-lg-12">
		<%-- Adding --%>
		<c:if test="${account != null}">
			<a href="${nav.get('newGame')}" class="btn btn-default">Dodaj grę</a>
			<br />
		</c:if>

		<%-- Nothing in list --%>
		<c:if test="${gameList == null || gameList.size() == 0}">
			<h4 style="color: red;">Nie znaleziono gier</h4>
			<br />
		</c:if>

		<%-- List --%>
		<c:if test="${gameList != null}">
			<c:forEach var="n" items="${gameList}">
				<c:set var="game" value="${n}" scope="request" />
				<c:set var="gameShortDesc" value="${true}" scope="request" />
				<jsp:include page="game_layout.jsp"/>
			</c:forEach>
		</c:if>
		<%-- /List --%>

	</div>
</div>
<!-- /.row -->
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<div class="col-lg-12">
		<%-- Adding news --%>
		<c:if test="${account.getAccess() > 0}">
			<a href="${nav.get('newNews')}" class="btn btn-default">Dodaj artykuł</a>
			<br />
		</c:if>
		
		<%-- Catching recent news if they were not set --%>
		<c:if test="${newsList == null}">
			<c:set var="newsList" value="${newsService.getRecentNews(10,0)}"/>
		</c:if>

		<%-- No News --%>
		<c:if test="${newsList == null}">
			<h4 style="color:red;">Nie znaleziono artykułów</h4>
			<br />
		</c:if>

		<%-- List --%>
		<c:if test="${newsList != null}">
			<c:forEach var="n" items="${newsList}">
				<c:set var="news" value="${n}" scope="request" />
				<c:set var="newsShortDesc" value="${true}" scope="request" />
				<jsp:include page="news_layout.jsp"/>
			</c:forEach>
		</c:if>
		<%-- /List --%>

	</div>
</div>
<!-- /.row -->
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="row">
	<div class="col-lg-12">
		<c:if test="${newsList!=null && newsList.size()>0}">
			<c:set var="news" value="${newsList.get(0)}" scope="request" />
		</c:if>
		
		<c:if test="${news!=null}">
			<%-- Get one most recent and display it --%>
			<c:set var="news" value="${newsService.getRecentNews(1, 0).get(0)}" scope="request" />
		</c:if>
		
		<c:set var="newsShortDesc" value="${true}" scope="request" />
		<jsp:include page="news_layout.jsp"/>
	</div>
</div>
<!-- /.row -->
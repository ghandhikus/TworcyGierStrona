<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="row">
	<div class="col-lg-12">
		<%-- Display --%>
		<c:if test="${news != null}">
			<c:set var="news" value="${news}" scope="request" />
			<c:set var="newsShortDesc" value="false" scope="request" />
			<jsp:include page="news_layout.jsp"/>
		</c:if>
		<%-- /Display --%>

		<c:if test="${news == null}">
			<h2 style="color:red;">Nie znaleziono artykułu</h2>
		</c:if>
	</div>
</div>
<!-- /.row -->
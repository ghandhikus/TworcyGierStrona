<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="row">
	<div class="col-lg-12">
		<%-- Display --%>
		<c:if test="${game != null}">
			<c:set var="game" value="${game}" scope="request" />
			<c:set var="gameShortDesc" value="false" scope="request" />
			<jsp:include page="game_layout.jsp"/>
		</c:if>
		<%-- /Display --%>

		<c:if test="${game == null}">
			<h2 style="color:red;">Game was not found</h2>
		</c:if>
	</div>
</div>
<!-- /.row -->
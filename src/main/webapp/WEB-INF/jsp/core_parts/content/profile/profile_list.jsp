<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="row">
	<div class="col-lg-12">

		<%-- No entries --%>
		<c:if test="${profileList == null}">
			<h4 style="color:red;">Nie znaleziono profilów</h4>
			<br />
		</c:if>

		<%-- List --%>
		<c:if test="${profileList != null}">
			<c:forEach var="n" items="${profileList}">
				<c:set var="profile" value="${n}" scope="request" />
				<c:set var="profileShortDesc" value="${true}" scope="request" />
				<jsp:include page="profile_layout.jsp"/>
			</c:forEach>
		</c:if>
		<%-- /List --%>

	</div>
</div>
<!-- /.row -->
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="row">
	<div class="col-lg-12">
		<%-- Display --%>
		<c:set var="profile" value="${profile}" scope="request" />
		<c:set var="profileShortDesc" value="false" scope="request" />
		<jsp:include page="profile_layout.jsp"/>
		<%-- /Display --%>
	</div>
</div>
<!-- /.row -->
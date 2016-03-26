<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />

<div class="row">
	<div class="col-lg-12">
		<c:choose>
			<c:when test="${error!=null}">Wystąpił błąd: ${error}</c:when>
			<c:when test="${news==null}">Nie można wczytać newsu.</c:when>
			<c:otherwise>
				<form:form modelAttribute="news" action="${nav.get('newsEdit')}/${news.newsId}?${_csrf.parameterName}=${_csrf.token}" method="post">
					<b>Tytuł</b>
					<br />
					<input type="text" name="title" style="color: black; width: 100%;" value="${news.title}">
					<br />
					<b>Zawartość</b>
					<br />
					<textarea name="content" style="color: black; width: 100%; min-height: 300px;">${news.content}</textarea>
					<br />
					<br />
		
					<input type="submit" class="btn btn-default" value="Wyślij edycję" />
				</form:form>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<!-- /.row -->
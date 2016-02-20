<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />

<div class="row">
	<div class="col-lg-12">

		<c:if test="${errors != null}">
			<h4 style="color: red;">${errors}</h4>
			<br />
		</c:if>

		<form:form modelAttribute="game" action="${nav.get('newGame')}" method="post">
			<b>Tytuł</b>
			<br />
			<input type="text" name="title" style="color: black; width: 100%;" value="${game.title}" />
			<br />
			<b>Opis</b>
			<br />
			<textarea name="description" style="color: black; width: 100%; min-height: 300px;">${game.description}</textarea>
			<br />
			<b>Zdjęcia</b>
			<br />
			<input name="image" type="file" size="50" multiple/>
			<br />
			<br />

			<input type="submit" class="btn btn-default" value="Dodaj Grę" />
		</form:form>
	</div>
</div>
<!-- /.row -->
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main left">
	<h2>Najnowsza gra</h2>
	<jsp:include page="game/game_recent.jsp" />
</div>
<div class="main right">
	<h2>Najnowszy news</h2>
	<jsp:include page="news/news_recent.jsp" />
</div>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../../script/maren/game_script.jsp" />
<img style="max-height:128px" alt="logo not found :C" src="<spring:url value="/resources/maren/logo.png" />">
<br style="clear:both;" />
<canvas id="game" width="${width}" height="${height+75}"></canvas>
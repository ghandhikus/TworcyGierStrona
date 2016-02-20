<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<h3 style="color:#FBB;">${error}</h3>
<form:form action="${nav.get('register')}" method="post">
<b>Nazwa : </b><br /><input type="text" name="name" style="color:black;" /><br />
<b>Hasło : </b><br /><input type="password" name="password" style="color:black;" /><br />
<br />
<input type="submit" class="btn btn-default" value="Register" />
</form:form>

<c:if test="${newUserCount != null}">
	Od ostatniego restartu serwera, zarejestrowało się ${newUserCount} nowych użytkowników.<br />
</c:if>
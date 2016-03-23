<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<spring:url value="/account/login" var="login" />
<spring:url value="/account/logout" var="logout" />
<c:set var="_get_params" value="${(pageContext.request.queryString!=null)? (pageContext.request.queryString) : null}"/>

<h3 style="color:#FBB;">
	${error}
	<!-- use param.error assuming FormLoginConfigurer#failureUrl contains the query parameter error -->
    <c:if test="${param.error != null}">
        <div>
            Failed to login.
            <c:if test="${SPRING_SECURITY_LAST_EXCEPTION != null}">
              Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}" />
            </c:if>
        </div>
    </c:if>
</h3>
<%-- <form:form action="${login}${(_get_params!=null)?'?':null}${_get_params}" method="post"> --%>
<form:form action="${login}?${_csrf.parameterName}=${_csrf.token}${(_get_params!=null)?'&':null}${_get_params}" method="post">

<b>&nbsp;Konto</b>
<div class="input-group margin-bottom-sm">
  <span class="input-group-addon"><i class="fa fa-user fa-fw"></i></span>
  <input class="form-control" type="text" name="name" placeholder="Nazwa konta">
</div>
<b>&nbsp;Hasło</b>
<div class="input-group">
  <span class="input-group-addon"><i class="fa fa-key fa-fw"></i></span>
  <input class="form-control" type="password" name="password" placeholder="Hasło">
</div>
<br />
<!-- if using RememberMeConfigurer make sure remember-me matches RememberMeConfigurer#rememberMeParameter -->
<p>
<label for="remember-me">Zapamiętać logowanie?</label>
 <input type="checkbox" id="remember-me" name="remember-me"/>
<br />
<input type="submit" class="btn btn-default" value="Login" />
</form:form>
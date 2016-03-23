<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%-- ### Urls --%>
<%-- CSS --%>
<spring:url value="/resources/css/sb-admin.css" var="sbAdmin" />
<spring:url value="/resources/galleria/galleria-1.4.2.min.js" var="jsGalleria" />

<%-- Font --%>
<spring:url value="/resources/css/font-awesome.css" var="fontAwesome" />
<spring:url value="https://fonts.googleapis.com/css?family=PT+Sans:400,700" var="ptSans" />

<%-- Images --%>
<spring:url value="/resources/img/logo.png" var="logo" />

<%-- Pages --%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />


<%-- ### Document --%>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title><jsp:include page="core_parts/title_chooser.jsp" /></title>

<jsp:include page="include/jquery.jsp" />
<jsp:include page="include/bootstrap.jsp" />
<jsp:include page="script/chat.jsp" />

<!-- Custom CSS -->
<link href="${sbAdmin}" rel="stylesheet" type="text/css">

<!-- Custom Fonts -->
<link href="${fontAwesome}" rel="stylesheet" type="text/css">
<link href="${ptSans}" rel="stylesheet" type="text/css">

<!-- Galleria -->
<script src="${jsGalleria}"></script>

<jsp:include page="core_parts/head_chooser.jsp" />

</head>

<body>
	<div id="wrapper">

		<!-- Navigation -->
		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-ex1-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				
				<c:if test="${param.marenLogo != null}">
					<a class="navbar-brand" href="${home}"><img class="logo" alt="logo not found :C" src="${param.marenLogo}"></a>
				</c:if>
				
				<c:if test="${param.marenLogo == null}">
					<a class="navbar-brand" href="${home}"><img class="logo" alt="logo not found :C" src="${logo}"></a>
				</c:if>
			</div>
			<!-- Top Bar Items -->
			<ul class="nav navbar-right top-nav">
				<!-- Logged Account Menu -->
				<c:if test="${account!=null}">
					<jsp:include page="core_parts/top_bar/logged.jsp" />
				</c:if>
				
				<!-- Logged Out Person Menu -->
				<c:if test="${account==null}">
					<jsp:include page="core_parts/top_bar/unlogged.jsp" />
				</c:if>
			</ul>
			<!-- Sidebar Menu Items - These collapse to the responsive navigation menu on small screens -->
			<div class="collapse navbar-collapse navbar-ex1-collapse">
				<ul class="nav navbar-nav side-nav">
					<jsp:include page="core_parts/navigation_chooser.jsp" />
				</ul>
			</div>
			<!-- /.navbar-collapse -->
		</nav>

		<div id="page-wrapper">
			<div class="container-fluid">

				<!-- Page Heading -->
				<div class="row">
					<div class="col-lg-12">

						<%-- Page title --%>
						<h1 class="page-header">
							<jsp:include page="core_parts/top_chooser.jsp" />
						</h1>

						<%-- Breadcrumb --%>
						<ol class="breadcrumb">
							<jsp:include page="core_parts/breadcrumb_chooser.jsp" />
						</ol>
						<%-- /Breadcrumb --%>
					</div>
				</div>
				<!-- /.row -->


				<%-- #Page Contents --%>
				<jsp:include page="core_parts/content_chooser.jsp" />
				<%-- /Page Contents --%>

			</div>
			<!-- /.container-fluid -->
		</div>
		<!-- /#page-wrapper -->

	</div>
	<!-- /#wrapper -->
	<jsp:include page="script/check_logout.jsp" />
</body>

</html>
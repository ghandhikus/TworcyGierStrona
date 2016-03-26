<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}" var="currentPage" />

<c:if test="${news!=null}">	
	<%-- Title (doesn't show if is inside the specific news page) --%>
	<c:if test="${nav.onPage(currentPage, 'newsSpecific') == false}">
		<%-- Top Line --%>
		<hr>
		
		<h1 class="news title">
			<a href="${nav.get('newsSpecific')}${news.newsId}"><c:out value="${news.title}" /></a>
			<%-- Edit Button --%>
			<c:if test="${account.getAccess()>0 || account.id == news.newsId}">
				<a href="${nav.get('editNews')}/${news.newsId}" class="btn btn-default fa fa-edit"
					style="font-size: 15px;"> Edytuj</a>
			</c:if>
		</h1>
	</c:if>
	
	<%-- Edit Button on specific --%>
	<c:if test="${nav.onPage(currentPage, 'newsSpecific') == true && account.getAccess()>0}">
		<a href="${nav.get('editNews')}/${news.newsId}" class="btn btn-default fa fa-edit"
			style="font-size: 15px;"> Edytuj</a>
	</c:if>
	
	<%-- Content --%>
	<c:choose>
		<c:when test="${newsShortDesc==true}">
			<%-- No need to split if description is short --%>
			<c:if test="${news.content.length()>512}">
				<p>
					<span class="news content"><c:out value="${htmlUtils.split(bbcode.parse(news.content),512)}" escapeXml="false" />...</span>
					<a href="${nav.get('newsSpecific')}${news.newsId}"><i>Czytaj więcej</i></a>
				</p>
			</c:if>
			<c:if test="${news.content.length()<=512}">
				<p><c:out value="${bbcode.parse(news.content)}" escapeXml="false" /></p>
			</c:if>
		</c:when>
		<c:otherwise>
			<p class="news content"><c:out value="${bbcode.parse(news.content)}" escapeXml="false" /> </p>
		</c:otherwise>
	</c:choose>
	
	<%-- Author --%>
	<p class="news author" style="color:#aaa;display:inline-block;">
		wstawił <a href="${nav.get('profileSpecific')}${news.authorId}">${newsService.getAuthorName(news.authorId)}</a>
	</p>
	
	<%-- Time --%>
	<p class="news time" style="color:#aaa;float:right;">${news.getDate().toString("yyyy/MM/dd HH:mm 'UTC'ZZ")}</p>
	<br />
</c:if>

<c:if test="${news==null}">
	<h2 style="color:red;">Newsy są puste</h2>
</c:if>
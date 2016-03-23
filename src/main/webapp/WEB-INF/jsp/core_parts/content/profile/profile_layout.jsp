<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}"
	var="currentPage" />

<c:if test="${profile!=null}">
	<%-- Edit Button --%>
	<c:if
		test="${nav.onPage(currentPage, 'profile') == true && (account.getAccess()>0 || profile.accountId==account.id)}">
		<a href="${nav.get('profileEdit')}/${profile.accountId}"
			class="btn btn-default fa fa-edit" style="font-size: 15px;">
			Edytuj</a>
	</c:if>
	<%-- Remove Button --%>
	<c:if test="${account.getAccess()>0 || profile.accountId==account.id}">
		<div id="profile-remove-btn" class="btn btn-default fa fa-edit" style="font-size: 15px;"> Usuń</div>
		<div id="profile-remove" style="z-index:1000000000000;">Czy chcesz usunąć opis profilu?</div>
		<%-- JS --%>
		<script>
		$(function() {
			$("#profile-remove").fadeOut(0);
			  $("#profile-remove-btn").click(function() {
			    $("#profile-remove").dialog({
			      resizable: false,
			      height: 140,
			      modal: true,
			      buttons: {
			        "Usuń": function() {
			          window.location.replace("${nav.get('profileRemove')}${account.id}");
			          $(this).dialog("close");
			        },
			        "Anuluj": function() {
			          $(this).dialog("close");
			        }
			      }
			    });
			  });
			});
		</script>
	</c:if>

	<%-- Description --%>
	<c:choose>
		<c:when test="${profileShortDesc==true}">
			<%-- No need to split if description is short --%>
			<c:if test="${profile.description.length()>512}">
				<p>
				<span class="profile description">
					<c:out
						value="${htmlUtils.split(bbcode.parse(profile.description),512)}"
						escapeXml="false" />
					...
				</span>
				<a href="${nav.get('profileSpecific')}${profile.accountId}"><i>Czytaj
						więcej</i></a>
				</p>
			</c:if>
			<c:if test="${profile.description.length()<=512}">
				<p>
					<c:out value="${bbcode.parse(profile.description)}"
						escapeXml="false" />
				</p>
			</c:if>
		</c:when>
		<c:otherwise>
			<p class="profile description">
				<c:out value="${bbcode.parse(profile.description)}"
					escapeXml="false" />
			</p>
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="${profile==null}">
	<c:if test="${nav.onPageSpecific(currentPage, 'profile')}">
		<a href="${nav.get('profileEdit')}/${account.id}"
			class="btn btn-default fa fa-edit" style="font-size: 15px;">
			Dodaj</a>
	</c:if>
	<h2 style="color: red;">Profil jest pusty</h2>
</c:if>
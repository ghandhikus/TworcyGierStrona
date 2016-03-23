<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<spring:url value="/account/login_ajax" var="checkerAjax" />
<spring:url value="/" var="mainSite" />

<%-- Adds the script only if user is logged in --%>
<c:if test="${account!=null}">
<script type="text/javascript">
/** @file logout.js */

// Allow splice for string
String.prototype.splice = String.prototype.splice
		|| function(index, count, add) {
			return this.slice(0, index) + (add || "")
					+ this.slice(index + count);
		}

var LogoutChecker = LogoutChecker || {
	net : function() {
		var startTime = Date.now();
		
		LogoutChecker._messagesCache = [];
		
		// Calls ajax to retrieve data from the server
		$.ajax({
			url : "${checkerAjax}",
			dataType : 'json',
			method : 'POST',
			timeout : 2000, //2 second timeout
			error : function(jqXHR, status, errorThrown) { //the status returned will be "timeout" 
				console.error("ERROR check_logout.jsp : "+errorThrown);
				setTimeout(LogoutChecker.net, 250);
			},
			success : function(data) {
				// Redirect to main site when logged out.
				if (data[0] == "notLogged") 
					window.location.assign("${mainSite}"); // set the link
				else if(data != "")
					console.log("Unknown json in check_logout.jsp : "+data);
				
				setTimeout(LogoutChecker.net, 250);
			}
		});
	},
}

// Start connection if ready.
//$(LogoutChecker.net);
</script>
</c:if>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<spring:url value="${requestScope['javax.servlet.forward.request_uri']}"
	var="currentPage" />
<spring:url value="/resources/js/jquery.sceditor.bbcode.min.js"
	var="wysiwygEditorJS" />
<spring:url value="/resources/css/themes/monocons.min.css"
	var="wysiwygEditorCSS" />
<spring:url value="/resources/languages/pl.js"
	var="wysiwygEditorLocale" />
<spring:url value="/resources/img/"
	var="wysiwygEditorEmoticons" />

<div class="row">
	<div class="col-lg-12">
		<c:if test="${error!=null}">Wystąpił błąd: ${error}</c:if>
	<%-- ### Form --%>
		<form:form modelAttribute="game"
			action="${nav.get('gameEdit')}/${game.gameId}?${_csrf.parameterName}=${_csrf.token}" method="post" enctype="multipart/form-data">
		<%-- ### Title --%>
			<h3><i class="fa fa-fw fa-header"></i> Tytuł</h3>
			<br />
			<input type="text" name="title" style="color: black; width: 100%;"
				value="${(gameTitle!=null)? gameTitle : game.title}"/>
			<br />
		<%-- ### Description --%>
			<h3><i class="fa fa-fw fa-file-text"></i> Opis</h3>
			<br />
			<script type="text/javascript" src="${wysiwygEditorJS}"></script>
			<script type="text/javascript" src="${wysiwygEditorLocale}"></script>
			<script>
				$("head link[rel='stylesheet']").last().after('<link rel="stylesheet" href="${wysiwygEditorCSS}" type="text/css" media="all" />');
				$(function() {
					// Set up the editor
				    $(".game.edit.description").sceditor({
				        plugins: "bbcode",
						style: "${wysiwygEditorCSS}",
						locale: "pl-PL",
						emoticonsRoot: "${wysiwygEditorEmoticons}",
						autoUpdate: true,
						width: "98%",
						height: 350
				    });
				    
					// Form submit
				    $('.game.edit.submit').on("click",function() {
				    	var instance = $('.game.edit.description').sceditor('instance');
					    var bbcode = instance.toBBCode(instance.val());
					    $('textarea.game.edit.description').val(bbcode);
					    console.log("bbcode: "+bbcode);
					    $('.game.edit.submit').submit();
				    });
				});
			</script>
			<%-- <textarea class="game edit description" name="description" style="color: black; width:100%; min-height: 300px; max-height:800px;">${(gameDesc!=null)? gameDesc : game.description}</textarea> --%>
			<input type="hidden" class="game edit description" name="description" value="${(gameDesc!=null)? gameDesc : game.description}"/>
			<br />
			<hr/>
		<%-- ### Media --%>
			<h3><i class="fa fa-fw fa-camera-retro"></i> Media</h3>
			<br />
			<%-- Youtube --%>
			<h4><img src="<c:url value="/resources/img/youtube.png"/>" /> Dodaj filmik youtube</h4>
			<br />
			<input type="text" name="youtube" style="color: black;" size=48 placeholder="https://www.youtube.com/watch?v=LmWQyhzt2ho" />
			<br />
			<br />
			<h4><i class="fa fa-fw fa-image"></i> Upload zdjęć</h4>
			<br />
			<input name="image" type="file" size="50" multiple />
			<br />
			<br />

		<%-- ### Image Sorting --%>
			<%-- Hidden JSON image data holder --%>
			<input id="game_media_sort" name="mediaJSON" type="hidden"
				value='${json.stringArrayToJSON(game.media)}'>
				
			<%-- Image sorting script --%>
			<script>
				$(function() {
					var _parseHolder = ".game.edit.media_sorter";
					var _parseElements = _parseHolder+" img, "+_parseHolder+" iframe";
					var _parseOutput = "#game_media_sort";
					var _youtubePrefixRegex = /^http:\/\/img.youtube.com\/vi\//;
					var _youtubeSuffixRegex = /\/hqdefault\.jpg$/;
					
					// Makes parent div sort possible
					$(_parseHolder).sortable({
						revert : false,
					});
					
					// Sets up elements for sorting
					$(_parseElements).draggable(
							{
								connectToSortable : _parseHolder,
								helper : "original",
								revert : "invalid",
								// No delay before firing stop method
								revertDuration : 0,
								scrollSpeed : 0,
								// Is called upon stopping of the movement
								stop : function(event, ui) {
									// List of images
									var list = [];
									// For each image
									$(_parseElements).each(
											function() {
												var str = $(this).attr("src");
												var name = "";
												
												// Checks supported types
												if(str.startsWith("http://img.youtube.com/vi/"))// Youtube check
													name = str.replace(_youtubePrefixRegex, 'yt:')
															  .replace(_youtubeSuffixRegex, '');
												else // Image
													name = str.substring(str
														.lastIndexOf("/") + 1,
														str.length);
												
												// Add the result to the list
												if (name !== null && name !== "null")
													// Add the image to the list
													list.push(name);
											});
									
									// Remove duplicates (eventual bugfix)
									var uniqueNames = [];
									$.each(list, function(i, el) {
										if ($.inArray(el, uniqueNames) === -1)
											uniqueNames.push(el);
									});
									
									// Set the hidden input data
									$(_parseOutput).val(JSON.stringify(list));
								},
							});
					// Disables mouse selection of the images on dragging
					$(_parseElements).disableSelection();
				});

			</script>
			
			<%-- Images for sorting --%>
			<div class="game edit media_sorter">
				<c:forEach var="n" items="${game.media}">
					<c:set var="media" value="${n}" scope="request" />
					<c:set var="youtubeImg" value="${true}" scope="request" />
					<jsp:include page="game_media.jsp"/>
				</c:forEach>
			</div>
		<%-- /Image Sorting --%>

			<br />
			<br />
			
			<%-- Form submit --%>
			<input type="submit" class="game edit submit btn btn-default" value="Wyślij edycję" />
			
			<br />
			<br />
			<br />
		</form:form>
	<%-- /Form --%>
	</div>
</div>
<!-- /.row -->
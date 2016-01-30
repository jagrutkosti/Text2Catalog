<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!doctype html>
<html lang="en">
<head>
<title>Results</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="resources/jquery.flipster.min.js"></script>
<script type="text/javascript" src="resources/bootstrap-tagsinput.js"></script>
<link rel="stylesheet" href="resources/bootstrap-tagsinput.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
	integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7"
	crossorigin="anonymous">
<link rel="stylesheet" href="resources/jquery.flipster.min.css">
<link rel="stylesheet" href="resources/results.css">
<link rel="stylesheet" href="http://css-spinners.com/css/spinner/whirly.css" type="text/css">
<script type="text/javascript">
	function filepath() {
		$('#loading').show();
		document.forms["imageOfBook"].submit();
	}
	
	$('#scrollbox3').enscroll({
	    showOnHover: true,
	    verticalTrackClass: 'track3',
	    verticalHandleClass: 'handle3'
	});
</script>
</head>
<body>
	
	<div class="form-group">
		<div class="input-group">
			<div class="input-group-addon">
				<form id="imageOfBook" name="imageOfBook" action="getBooks" method="post"
					enctype="multipart/form-data">
				<label for="fileUpload"><img
					src="resources/camera-black.png"> </label> 
				<input type="file"
					onchange="filepath()" id="fileUpload" name="fileName"
					accept="image/*" capture="camera">
				</form>
			</div>


			<span class="input-group-addon" id="basic-addon1">#</span>
			<input type="text" class="form-control" placeholder="Add Keywords..."
				aria-describedby="basic-addon1" style="height: 50px;"> 
			<span class="input-group-btn">
				<button type="button" class="btn btn-primary" style="height: 50px;">Add</button>
			</span>

		</div>
	</div>
	
	<div>
		<div id="coverflow">
			<ul class="flip-items">
				<c:forEach items="${dataFromServer.booksResult}" var="book">					
					<c:choose>
						<c:when test="${book.coverId == 1}">
							<li>
								<div class="boxed">"<c:out value="${book.name}" />"<br>
         						-<c:out value="${book.author}" /></div>
         						<pre style="max-width:100px;word-wrap:break-word;">${book.associatedKeywords}</pre>         						
         					</li>       
						</c:when>
						<c:otherwise>
							<li>
								&nbsp;&nbsp;<div class="boxed">
									<img src="http://covers.openlibrary.org/b/id/${book.coverId}-M.jpg" style="width: auto; height: auto;max-width: 100px;max-height: 120px">
								</div>
								<pre style="max-width:100px;word-wrap:break-word;">${book.associatedKeywords}</pre>									
							</li>													
						</c:otherwise>
					</c:choose>						
         		</c:forEach>				
			</ul>
		</div>
		
		

		<script>
			$(document).ready(function() {
				setTimeout(function() {
					var coverflow = $("#coverflow").flipster({
						style: 'flat',
						start : 0,
						touch : true,
						scrollwheel: true
					});
				}, 500);
			});
		</script>

	</div>
	<div id="scrollbox3">
		<select multiple data-role="tagsinput">
			<c:forEach items="${dataFromServer.keywords}" var="keyword">
				<option value="${keyword}"><c:out value="${keyword}" /></option>
			</c:forEach>
		</select>
	</div>
	<br>
	<center><div class="whirly-loader" id="loading" style="display:none; position:relative;" >Loading...</div></center>
	<div class="imgborder">
		<img src="data:image/jpg;base64,${dataFromServer.base64Img}"
			style="width: 100%" class="img-rounded" />
	</div>

</body>
</html>

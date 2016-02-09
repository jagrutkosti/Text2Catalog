<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!doctype html>
<html lang="en">
<head>
<title>Results</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script type="text/javascript" src="resources/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="resources/jquery-ui.min.js"></script>
<script type="text/javascript" src="resources/jquery.ui.touch-punch.min.js"></script>
<script type="text/javascript" src="resources/bootstrap.min.js"></script>
<link rel="stylesheet" href="resources/jquery-ui.min.css">
<link rel="stylesheet" href="resources/bootstrap.min.css">
<link rel="stylesheet" href="resources/coverflow.css">
<link rel="stylesheet" href="resources/results.css">
<script type="text/javascript">
	function filepath() {
		$('#loading').show();
		document.forms["imageOfBook"].submit();
	};
</script>
</head>
<body>

	<!-- <div class="form-group"> -->
	<div class="input-group">
		<div class="input-group-addon">
			<form id="imageOfBook" name="imageOfBook" action="getBooks"
				method="post" enctype="multipart/form-data">
				<label for="fileUpload"><img
					src="resources/camera-black.png"> </label> <input type="file"
					onchange="filepath()" id="fileUpload" name="fileName"
					accept="image/*" capture="camera">
			</form>
		</div>


		<span class="input-group-addon" id="basic-addon1">#</span>
		<div class="input-group-addon">
			<form id="addKeyword" name="addKeyword" action="getKeywordBooks"
				method="post">
				<input type="text" placeholder="Add Keywords..."
					aria-describedby="basic-addon1" style="height: 50px;"
					name="keyword" id="keyword">
				<!-- <span class="input-group-btn"> -->
				<input type="submit" class="btn btn-primary" style="height: 50px;"
					value="Add" />
				<!-- </span> -->
			</form>
		</div>
	</div>
	<!-- </div> -->
	<!-- <div id="master"> -->
		<div id="container">
			<!-- <ul class="flip-items" id="flip-items"></ul> -->
		</div>
	<!-- </div> -->

	<center><div class="spinner" id="loading" style="display: none; position: relative;"></div>	</center>
	<hr>
	
	<span class="label label-primary">#Keywords</span>
	<!-- <div id="scrollbox3"> -->
		<textarea class="tagarea" id="tagsarea"></textarea>
	<!-- </div> -->
	<br>
	<hr>
	
	<span class="label label-primary">Your Image</span>
	<div class="imgborder">
		<img id="img" src="" style="width: 100%" class="img-rounded" />
	</div>

	<div id="modal-content" class="modal fade" tabindex="-1" role="dialog">
	    <div class="modal-dialog">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">×</button>
	                <p><b>Book Information</b></p>
	            </div>
	            <div class="modal-body">
	            	<p>Some Book Information</p>
	            	<p>And its location.</p>
	                <p>
	                    <label for="txtname">Email:</label><input type="text" id="txtname" />
	                </p>
	            </div>
	            <div class="modal-footer"> 
	                <a href="#" class="btn" data-dismiss="modal">Close</a>
	                 <a href="#" class="btn btn-primary">Send Location</a>
	            </div>
	        </div>
	    </div>
	</div>

	<script>
		var dataFromServer = ${dataFromServer};
	</script>
	<script type="text/javascript" src="resources/coverflow.js"></script>	
	<script type="text/javascript" src="resources/results.js"></script>
</body>
</html>

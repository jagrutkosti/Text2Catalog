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
	<div id="master">
		<div id="coverflow">
			<ul class="flip-items" id="flip-items"></ul>
		</div>
	</div>

	<center><div class="spinner" id="loading" style="display: none; position: relative;"></div>	</center>
	<hr>
	<span class="label label-primary">#Keywords</span>
	<div id="scrollbox3">
		<select id="tags" multiple data-role="tagsinput"></select>
	</div>

	<br>

	<hr>
	<span class="label label-primary">Your Image</span>
	<div class="imgborder">
		<img id="img" src="" style="width: 100%" class="img-rounded" />
	</div>
	
	<script>
	var dataFromServer;
	function showAllData(dataFromServer){
		$('#coverflow').remove();
		$('#master').append($('<div id="coverflow"><ul class="flip-items" id="flip-items"></ul></div>'));
		$('#tags').tagsinput('removeAll');
		$.each(dataFromServer.booksResult, function(index, book){
			var html = '';
			if(book.coverId == 1){
				html = '<li><div class="boxed">"'+book.name+'"<br>';
				html += '-'+book.author+'</div>';
				html += '<pre style="width:100px;height:40px;word-wrap:break-word;">'+book.associatedKeywords+'</pre></li>';
				$('#flip-items').append($(html));
			}else{
				html = '<li><div class="boxed"><img src="http://covers.openlibrary.org/b/id/';
				html += book.coverId+'-M.jpg" width="100" height="120"></div>';
				html += '<pre style="width:100px;height:40px;word-wrap:break-word;">'+book.associatedKeywords+'</pre></li>';
				$('#flip-items').append($(html));
			}
		});
		
		document.getElementById('img').setAttribute('src','data:image/jpg;base64,'+dataFromServer.base64Img);
		
		
		$.each(dataFromServer.keywords, function(index, keyword){			
			$('#tags').tagsinput('add',keyword);
		});
	}
	
	$(document).ready(function() {
		dataFromServer = ${dataFromServer};
		console.log(dataFromServer);
		showAllData(dataFromServer)
		$("#coverflow").flipster({
			style: 'flat',
			start : 0,
			touch : true,
			scrollwheel: true
		});
	});
	
	$("#addKeyword").submit(function(event){
		event.preventDefault();
		$('#loading').show();
		var $form = $(this), url = $form.attr('action');
		var newKeyword = $('#keyword').val().toLowerCase();
		var tmp = dataFromServer.keywords.join('~').toLowerCase();
		var lcArray = tmp.split('~')
		if(lcArray.indexOf(newKeyword) > -1){
			alert('Keyword already exists!');
			$('#keyword').val('');
		}else{
			$.ajax({
				url: url,
				contentType : 'application/json; charset=utf-8',
			    dataType : 'json',
				data: JSON.stringify({keyword:newKeyword, dataFromClient : dataFromServer}),
				type: "POST",
				success: function(data){
					console.log(data);
					dataFromServer = data;
					$('#keyword').val('');
					showAllData(dataFromServer);
					$("#coverflow").flipster({
						style: 'flat',
						start : 0,
						touch : true,
						scrollwheel: true
					});
					$('#loading').hide();
				},
				error: function(xhr,status,error){
					console.log(xhr.responseText);
				}
			});
		}		
		return false;
	});
	
	$('select').on('itemRemoved', function(event) {
		$('#loading').show();
		$.ajax({
			url: 'deleteBooks',
			contentType : 'application/json; charset=utf-8',
		    dataType : 'json',
			data:JSON.stringify({keyword:event.item, dataFromClient : dataFromServer}),
			type: "POST",
			success: function(data){
				console.log(data);	
				dataFromServer = data;
				showAllData(dataFromServer);
				$("#coverflow").flipster({
					style: 'flat',
					start : 0,
					touch : true,
					scrollwheel: true
				});
				$('#loading').hide();
			},
			error: function(xhr,status,error){
				console.log(xhr.responseText);
			}
		});
	});
	</script>
</body>
</html>

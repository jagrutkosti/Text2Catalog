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
		var $form = $(this), url = $form.attr('action');
		$.ajax({
			url: url,
			contentType : 'application/json; charset=utf-8',
		    dataType : 'json',
			data: JSON.stringify({keyword:$('#keyword').val(), dataFromClient : dataFromServer}),
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
			},
			error: function(xhr,status,error){
				console.log(xhr.responseText);
			}
		});
		return false;
	});
	
	$('select').on('itemRemoved', function(event) {
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
			},
			error: function(xhr,status,error){
				console.log(xhr.responseText);
			}
		});
	});
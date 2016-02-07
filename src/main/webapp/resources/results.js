function showAllData(dataFromServer){
	$('#coverflow').remove();
	$('#master').append($('<div id="coverflow"><ul class="flip-items" id="flip-items"></ul></div>'));
	
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
	
	$('.tag-box').remove();
	$('#tagsarea').val('');
	$.each(dataFromServer.keywords, function(index, keyword){
		$('#tagsarea').val($('#tagsarea').val()+keyword+'~');
	});
	var str = $('#tagsarea').val();
	str = str.slice(0,-1);
	$('#tagsarea').val(str);
	console.log($('#tagsarea').val());
	
	var backSpace;
	var close = '<a class="close">x</a>'; 
	var PreTags = $('.tagarea').val().trim().split("~");

	$('.tagarea').after('<ul class="tag-box"></ul>');

	for (i=0 ; i < PreTags.length; i++ ){
	  $('.tag-box').append('<li class="tags">'+PreTags[i]+close+'</li>');
	}
	
	//Delete tag
	$(".tag-box").on("click", ".close", function()  {
		var tempstr = $(this).parent().text();
		tempstr = tempstr.slice(0,-1);
		$(this).parent().remove();
		setTimeout(removeTag(tempstr),1);
	});	
	
	//Sorting
	$(function() {
		var start,stop;
		$(".tag-box").sortable({
			items: "li",
			containment: "parent",
			scrollSpeed: 200,
			stop : function(event,ui){
				stop = ui.item.index();
				console.log(stop);
				reorderArray(start,stop);
			},
			start : function(event,ui){
				start = ui.item.index();
				console.log(start);
			}
		});	
		$( ".tag-box" ).disableSelection();
	});
};

$(document).ready(function() {
	console.log(dataFromServer);
	showAllData(dataFromServer);
	$("#coverflow").flipster({	
		style: 'flat',
		start : 0,
		touch : true,
		scrollwheel: true		
	});		
});


$(document).on('submit','#addKeyword', function(event){
	event.preventDefault();
	$('#loading').show();		
	var $form = $(this), url = $form.attr('action');
	var newKeyword = $('#keyword').val().toLowerCase();
	var tmp = dataFromServer.keywords.join('~').toLowerCase();
	var lcArray = tmp.split('~')
	if(lcArray.indexOf(newKeyword) > -1){
		alert('Keyword already exists!');
		$('#keyword').val('');
		$('#loading').hide();
	}else{
		$.ajax({
			url: url,
			contentType : 'application/json; charset=utf-8',
		    dataType : 'json',
		    async : true,
		    cache : false,
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

/*$('#tags').on('itemRemoved', function(event) {*/
function removeTag(tag){
	$('#loading').show();
	$.ajax({
		url: 'deleteBooks',
		contentType : 'application/json; charset=utf-8',
	    dataType : 'json',
	    async : true,
	    cache : false,
		data:JSON.stringify({keyword:tag, dataFromClient : dataFromServer}),
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
};

function reorderArray(start,stop){
	var booksList = dataFromServer.booksResult;
	var keywords = dataFromServer.keywords;
	console.log(booksList.length);
	var draggedItem = booksList[start];
	var draggedKeyword = keywords[start];
	
	keywords.splice(start,1);
	keywords.splice(stop,0,draggedKeyword);
	dataFromServer.keywords = keywords;
	
	var temp = [];
	for (var i=0; i < booksList.length; i++){
		if(booksList[i].associatedKeywords.toLowerCase() == draggedKeyword.toLowerCase()){
			temp.push(booksList[i]);
		}
	};
	booksList = booksList.filter(function(element,i){
		return element.associatedKeywords.toLowerCase() !== draggedKeyword.toLowerCase()
	});
	
	if(stop > 0)
		stop -= 1;
	
	var prevKeyword = keywords[stop];
	var index;
	for (var i=0; i < booksList.length; i++){
		if(booksList[i].associatedKeywords.toLowerCase() == prevKeyword.toLowerCase()){
			index = i;
			break;
		}
	};
	for (var i=index; i < booksList.length; i++){
		if(booksList[i].associatedKeywords.toLowerCase() !== prevKeyword.toLowerCase()){
			index = i;
			break;
		}
	};
	booksList.splice.apply(booksList,[index,0].concat(temp));
	dataFromServer.booksResult = booksList;
	
	console.log(temp.length);
	showAllData(dataFromServer);
	$("#coverflow").flipster({	
		style: 'flat',
		start : 0,
		touch : true,
		scrollwheel: true		
	});	
}

var playlist = [];

//Method to arrange content on the page on each activity
function showAllData(dataFromServer){
	coverflow('container').remove();
	coverflow('container').setup({
		playlist : playlist,
		coverwidth: 100,
		coverheight: 120,
		fixedsize: true,
		width : $(window).width(),
		reflectionopacity : 0,
		reflectionratio : 0
	}).on('click', function(){
		$('#modal-content').modal({
	        show: true
	    });
	});
	
	document.getElementById('img').setAttribute('src','data:image/jpg;base64,'+dataFromServer.base64Img);
	
	$('.tag-box').find("*").addBack().off();
	$('.tag-box').remove();
	$('#tagsarea').val('');
	$.each(dataFromServer.keywords, function(index, keyword){
		$('#tagsarea').val($('#tagsarea').val()+keyword+'~');
	});
	var str = $('#tagsarea').val();
	str = str.slice(0,-1);
	$('#tagsarea').val(str);
	
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
		removeTag(tempstr);
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
				reorderArray(start,stop);
			},
			start : function(event,ui){
				start = ui.item.index();
			}
		});	
		$( ".tag-box" ).disableSelection();
	});
};

$(document).ready(function() {
	constructJson(dataFromServer);
	showAllData(dataFromServer);
});

//New keyword submit event
$(function(){
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
					dataFromServer = data;
					constructJson(dataFromServer);
					$('#keyword').val('');
					showAllData(dataFromServer);	
					$('#loading').hide();
				},
				error: function(xhr,status,error){
					console.log(xhr.responseText);
				}
			});
		}		
		return false;
	});
});

//remove tag call
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
			dataFromServer = data;
			constructJson(dataFromServer);
			showAllData(dataFromServer);		
			$('#loading').hide();
		},
		error: function(xhr,status,error){
			console.log(xhr.responseText);
		}
	});
};

//Called when keywords are rearranged
function reorderArray(start,stop){
	var booksList = dataFromServer.booksResult;
	var keywords = dataFromServer.keywords;
	
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
	
	constructJson(dataFromServer);
	showAllData(dataFromServer);
}

//formatting the received data to suit the coverflow.js needs
function constructJson(dataFromServer){
	playlist = [];
	$.each(dataFromServer.booksResult, function(index, book){
		var object = {};
		var bookname = book.name;
		if(bookname.length > 100){
			bookname = bookname.substr(0,100);
			bookname += '...';
		}
		
		object.title = "'"+ bookname + "'-By " + book.author;
		object.description = "#"+book.associatedKeywords;
		if(book.coverId !== 1){
			object.image = "http://covers.openlibrary.org/b/id/" + book.coverId+ "-M.jpg";
		}else{
			object.image = "http://dummyimage.com/100x120/dbd8db/000000.png&text=No+Image";
		}
		playlist.push(object);
	});
}

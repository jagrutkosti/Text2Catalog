<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!doctype html>
<html lang="en">
<head>
<title>Home</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" 
integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
<link rel="stylesheet" href="resources/home.css" type="text/css">

<script type="text/javascript">
function filepath(){
	$('#loading').show();
	document.forms["imageOfBook"].submit();
}
</script>

</head>
<body>
		<br><br>
		<form id="imageOfBook" name="imageOfBook" action="getBooks" method="post"
			enctype="multipart/form-data">

			<input type="file" onchange="filepath()" id="fileUpload" name="fileName" accept="image/*" capture="camera"><br> 
			<label for="fileUpload"> <img src="resources/camera.png" class="img-responsive img-circle" alt="Responsive image" />
			</label><br>	
			
		</form>
		<br><br>
		<center><div class="spinner" id="loading" style="display:none;"></div></center>
		<blockquote>
  			<p class="text-center"><strong>Step 1:</strong> Click Photo!</p>
		</blockquote>
		<blockquote>
  			<p class="text-center"><strong>Step 2:</strong> Get Books & Locations!</p>
		</blockquote>
</body>
</html>

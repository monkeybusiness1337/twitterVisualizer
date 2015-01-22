/**
 * MapManager v.1.0
 */


geoTweets = [] ; 
userLocation = null ;
userTopic = null ;
markersAdded = 0 ;
MAX_MARKERS = 10000 ;
currentLocation = null ;
mediaType = null ;

$( document ).ready(function() {
	
	$.post( "TweetServlet?isLoggedIn", function( data ) {
		if(data == true){
			$('#openLoginPanel').hide() ;
			$('#logoutButton').show() ;
			console.log("loggedIn") ;
		} else{
			$('#logoutButton').hide() ;
			$('#openLoginPanel').show() ;
			console.log("not loggedIn") ;
		}
	});
	
	$('ul.dropdown-menu li a').click(function(){
		console.log("activating mediatweets in menu") ;
		$('#filterTweetsButton').addClass('active') ;
	}) ;
	
	$('#tweetFoundButton').click(function(){
		if(!$('#statsContainer').hasClass('opened')){
			$('#statsContainer').slideDown().addClass('opened') ;
		} else{
			$('#statsContainer').slideUp().removeClass('opened') ;
		}
	}) ;
	
	$('.videos').click(function(){
		mediaType = 'video' ;
		$('#selectedMediaType').html(': <span class="glyphicon glyphicon-film" style="color: orange"></span>') ;
		$('#videoSelected').show() ;
		$('#filterTweetsButton').addClass('active') ;
		$('#searchField').css('width', '360px') ;
	}) ;
	
	$('.photos').click(function(){
		mediaType = 'photo' ;
		$('#selectedMediaType').html(': <span class="glyphicon glyphicon-camera" style="color: orange"></span>') ;
		$('#photoSelected').show() ;
		$('#filterTweetsButton').addClass('active') ;
		$('#searchField').css('width', '360px') ;
	}) ;
	
	$('.weblinks').click(function(){
		mediaType = 'weblink' ;
		$('#selectedMediaType').html(': <span class="glyphicon glyphicon-globe" style="color: orange"></span>') ;
		$('#linkSelected').show() ;
		$('#filterTweetsButton').addClass('active') ;
		$('#searchField').css('width', '360px') ;
	}) ;
	
	$('.music').click(function(){
		mediaType = 'music' ;
		$('#selectedMediaType').html(': <span class="glyphicon glyphicon-music" style="color: orange"></span>') ;
		$('#musicSelected').show() ;
		$('#filterTweetsButton').addClass('active') ;
		$('#searchField').css('width', '360px') ;
	}) ;
	
	$('#videoSelected').click(function(){
		$('#videoSelected').hide() ;
		$('#selectedMediaType').html('') ;
		mediaType = null ;
		initialize() ;
		$('#filterTweetsButton').removeClass('active') ;
		$('#allTweetsButton').addClass('active') ;
		$('#searchField').css('width', '380px') ;
	}) ;
	
	$('#photoSelected').click(function(){
		$('#photoSelected').hide() ;
		$('#selectedMediaType').html('') ;
		mediaType = null ;
		initialize() ;
		$('#filterTweetsButton').removeClass('active') ;
		$('#allTweetsButton').addClass('active') ;
		$('#searchField').css('width', '380px') ;
	}) ;
	
	$('#linkSelected').click(function(){
		$('#linkSelected').hide() ;
		$('#selectedMediaType').html('') ;
		mediaType = null ;
		initialize() ;
		$('#filterTweetsButton').removeClass('active') ;
		$('#allTweetsButton').addClass('active') ;
		$('#searchField').css('width', '380px') ;
	}) ;
	
	$('#musicSelected').click(function(){
		$('#musicSelected').hide() ;
		$('#selectedMediaType').html('') ;
		mediaType = null ;
		initialize() ;
		$('#filterTweetsButton').removeClass('active') ;
		$('#allTweetsButton').addClass('active') ;
		$('#searchField').css('width', '380px') ;
	}) ;
	
	$('#welcomeMessageContainer').fadeIn("slow") ;
	
	$('#letsStartButton').click(function(){
		$('#welcomeMessageContainer').fadeOut("slow").html('') ;
	}) ;
	
	$('#openLoginPanel').click(function(){
		console.log("asdasdasdasdasdasd") ;
		document.location.href = "http://tomcat01lab.cs.univie.ac.at:31897/TweetServlet/LoginServlet";
	}) ;
	
	$('#logoutButton').click(function(){
		$.post( "TweetServlet?logMeOut", function( data ) {
			$('#openLoginPanel').show() ;
			$('#logoutButton').hide() ;
		});
	}) ;
	
	$('#login, #cancleLogin').click(function(){
		$('#loginPanel').fadeOut("slow") ;
	}) ;
	
	$('#search').click(function(){
		/*var city = $('#searchField').val() ;
		$.getJSON("http://maps.googleapis.com/maps/api/geocode/json?address="+city, function( data ) {
			if($('#searchField').val().charAt(0) == '#'){
				alert("oh u wanna find a topic, thats not implemented yet ;D") ;
			} else{
				initialize() ;
				if(data.results.length > 0){
					var latLng = new google.maps.LatLng(data.results[0].geometry.location.lat, data.results[0].geometry.location.lng); //Makes a latlng
				    map.panTo(latLng); //Make map global
				} else{
					alert("nothing found bro ;D") ;
				}
			}
		});*/
		if($('#searchField').val().charAt(0) == '#'){
			//alert("oh u wanna find a topic, thats not implemented yet ;D") ;
			userTopic = $('#searchField').val() ;
			var userTopicLabel = '<div class="btn-group" style="margin-left:5px">' +
			 '<button class="btn btn-sm btn-primary" style="opacity:0.8">'+userTopic+'</button>' +
	  		 '<button class="btn btn-sm btn-primary" id="deleteUserTopic" style="opacity:0.8"><span class="glyphicon glyphicon-remove"></span></button>' +
			 '</div>' ;
			$('#trendContainer').append(userTopicLabel).fadeIn("slow") ;
			var mapCenter = map.getCenter() ;
			var centerLat = mapCenter.k ;
			var centerLong = mapCenter.D ;
			var center = {'lat': centerLat, 'longi': centerLong} ;
			initialize(center) ;
		} else{
			var city = $('#searchField').val() ;
			$.getJSON("http://maps.googleapis.com/maps/api/geocode/json?address="+city, function( data ) {
				initialize() ;
				if(data.results.length > 0){
					var latLng = new google.maps.LatLng(data.results[0].geometry.location.lat, data.results[0].geometry.location.lng); //Makes a latlng
				    map.panTo(latLng); //Make map global
				} else{
					alert("nothing found bro ;D") ;
				}
			});
		}
		//$('#searchField').val('') ;
	}) ;
	
	$('#searchField').click(function(){
		/*if(!$('#searchField').hasClass("expanded")){
			$("#searchField").animate({'width': '+=100px'}, 500);
			$("#searchField").addClass("expanded") ;
		}*/
	}) ;
	
	$('#searchField').focusout(function(){
		/*$("#searchField").animate({'width': '-=100px'}, 500);
		$("#searchField").removeClass("expanded") ;*/
	}) ;
	
	
	$(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      
	      if($("#searchField").is(":focus")){
	    	  $('#search').click() ;
	      }
	      
	      return false;
	    }
	  });
	
	
	$('#trendContainer').on('click', '#deleteUserTopic', function(){
		console.log("hello") ;
		userTopic = null ;
		$('#trendContainer').fadeOut('slow') ;
		initialize() ;
	}) ;
	
	$('.navbar ul li a').click(function(){
		if(!$(this).hasClass('filter'))
			$('.navbar ul li').removeClass("active") ;
		$(this).parent().addClass("active") ;
		if($(this).attr("class") == "hotTopics"){
			$.getJSON("TweetServlet?getTrends", function( data ) {
				var trends = data.trends ;
				var trendList = '' ;
				for(var i = 0 ; i < trends.length ; i++){
					trendList += '<div class="btn-group" style="margin-left:5px">' +
								 '<button class="btn btn-sm btn-success" style="opacity:0.8">'+trends[i]+'</button>' +
						  		 '<button class="btn btn-sm btn-success" style="opacity:0.8"><span class="glyphicon glyphicon-remove"></span></button>' +
								 '</div>' ;
				}
				$('#trendContainer').html(trendList) ;
			});
			
			$('#trendContainer').fadeIn("slow") ;
			initialize() ;
		} else if($(this).hasClass('mediaTweets')){
			
		} else{
			$('#trendContainer').fadeOut("slow") ;
			initialize() ;
		}
	}) ;
	
	function getLocation() {
		console.log("hello") ;
	    if (navigator.geolocation) {
	        navigator.geolocation.watchPosition(showPosition);
	    } else {
	       console.log("Geolocation is not supported by this browser.");
	    }
	}
	function showPosition(position) {
	    console.log("Latitude: " + position.coords.latitude + 
	    ";Longitude: " + position.coords.longitude); 
	    userLocation = position.coords ;
	}
	
	getLocation() ;
	
});

var style_array = [
                   {
                       "stylers": [
                           {
                               "saturation": -100
                           }
                       ]
                   },
                   {
                       "featureType": "water",
                       "elementType": "geometry.fill",
                       "stylers": [
                           {
                               "color": "#0099dd"
                           }
                       ]
                   },
                   {
                       "elementType": "labels",
                       "stylers": [
                           {
                               "visibility": "off"
                           }
                       ]
                   },
                   {
                       "featureType": "poi.park",
                       "elementType": "geometry.fill",
                       "stylers": [
                           {
                               "color": "#aadd55"
                           }
                       ]
                   },
                   {
                       "featureType": "road.highway",
                       "elementType": "labels",
                       "stylers": [
                           {
                               "visibility": "on"
                           }
                       ]
                   },
                   {
                       "featureType": "road.arterial",
                       "elementType": "labels.text",
                       "stylers": [
                           {
                               "visibility": "on"
                           }
                       ]
                   },
                   {
                       "featureType": "road.local",
                       "elementType": "labels.text",
                       "stylers": [
                           {
                               "visibility": "on"
                           }
                       ]
                   },
                   {}
               ] ;
               
function initialize(center1) {
  markersAdded = 0 ;
  geoTweets = [] ;
  newTweets = [] ;
  statTextTweets = 0 ;
  statPhotoTweets = 0 ;
  statVideoTweets = 0 ;
  statMusicTweets = 0 ;
  statLinkTweets = 0 ;
  
  $('#textTweetStatCount,#linkTweetStatCount,#videoTweetStatCount,#musicTweetStatCount,#photoTweetStatCount').html(0) ;
  
  $('#tweetsLoadedCount').html(0) ;
  $('#tweetsFound').html('Tweets found') ;
  var center = new google.maps.LatLng(48.20732, 16.373792) ;
    
  console.log("center: " + center1) ;
  var locationGiven = false ;
  
  if(center1 == "[object Event]" || typeof(center1) === "undefined"){
	  console.log("yeah") ;
	  locationGiven = false ;
  } else{
	  locationGiven = true ;
  }
  
  if(userLocation != null && !locationGiven){
	  console.log("user enabled tracking, so setting his location..") ;
	  center = new google.maps.LatLng(userLocation.latitude, userLocation.longitude) ;
  } else if(locationGiven){
	  console.log("setting center to given location") ;
	  center = new google.maps.LatLng(center1.lat, center1.longi) ;
  } else{
	  console.log("user diabled tracking, setting center to default location") ;
  }
  
  var mapOptions = {
    zoom: 12,
    center: center,
    styles: style_array,
    disableDefaultUI: true, 
    zoomControl: true, 
    zoomControlOptions: {
        style: google.maps.ZoomControlStyle.SMALL, 
        position: google.maps.ControlPosition.LEFT_TOP
      }
  }
  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
  
  google.maps.event.addListener(map, 'idle', function() {
    console.log("zoom: " + map.getZoom() ) ;
  
    	$('#statsContainer').slideUp() ;
	    // 3 seconds after the center of the map has changed
	    $('#tweetsFound').html("Loading") ;
	    $('#tweetsLoadedCount').html('<span class="glyphicon glyphicon-refresh glyphicon-refresh-animate"></span>') ;
	    
	    var mapCenter = map.getCenter() ;
		var centerLat = mapCenter.k ;
		var centerLong = mapCenter.D ;
		
		/*currentLocation = {} ;
		currentLocation.latitude = centerLat ;
		currentLocation.longitude = centerLong ;
		
		console.log("currentLocation in idle: " + currentLocation.latitude + ";" + currentLocation.longitude) ;
	    */
	    
	    window.setTimeout(function() {
	    	if(markersAdded <= MAX_MARKERS)
	    		reloatTweets() ;
	    }, 3000);
	  });
  reloatTweets() ;  
}

function reloatTweets(filterMediaType){
	var mapCenter = map.getCenter() ;
	var centerLat = mapCenter.k ;
	var centerLong = mapCenter.D ;

	
	var filterMediaType = (mediaType == null) ? '' : '&mediaType='+mediaType ;

	var getJustHotTopics = $('.hotTopics').parent().hasClass('active') ? '&trendTweets=true' : '' ;
	var topic = (userTopic != null) ? "&topic=" + userTopic.substring(1,userTopic.length) : "" ;
	$.getJSON( "TweetServlet?lat="+centerLat+"&long="+centerLong+getJustHotTopics+topic+filterMediaType, function( data ) {

		var tweetResult = data.result ;
		var newTweets = [] ;
		if(geoTweets.length == 0){
			geoTweets = tweetResult ;
			newTweets = geoTweets ;
		} else{
			for(var i = 0 ; i < tweetResult.length ; i++){
				var matchFound = false ;
				for(var j = 0 ; j < geoTweets.length ; j++){
					if(tweetResult[i].hash == geoTweets[j].hash){
						matchFound = true ;
						//console.log(tweetResult[i].hash + "==" + geoTweets[j].hash) ;
					} else{
						//console.log(tweetResult[i].hash + "!=" + geoTweets[j].hash) ;
					}
						
				}
				if(!matchFound){
					geoTweets.push(tweetResult[i]) ;
					newTweets.push(tweetResult[i]) ;
					//console.log("new one: " + tweetResult[i].hash) ;
				} else{
					//console.log("already painted: " + tweetResult[i].hash) ;
				}
			}
		}
		setMarkers(map, newTweets);
	});
}

function filterMarkers(mediaType){
	
}
statTextTweets = 0 ;
statPhotoTweets = 0 ;
statVideoTweets = 0 ;
statMusicTweets = 0 ;
statLinkTweets = 0 ;
function setMarkers(map, geotweet) {
  
  var shape = {
      coords: [1, 1, 1, 20, 18, 20, 18 , 1],
      type: 'poly'
  };
  var i = 0;
  for ( var i = 0 ; i < geotweet.length ; i++ ) {
	  
	  // set the right marker depending on mediatype
	  if(geotweet[i].isVideoTweet){
		  markerClass = "videoMarker" ;
		  statVideoTweets++ ;
		  $('#videoTweetStatCount').html(statVideoTweets) ;
	  }
	  else if(jQuery.isEmptyObject(geotweet[i].media) && !geotweet[i].isLinkTweet && !geotweet[i].isPhotoLinked && !geotweet[i].isVideoTweet){
		  markerClass = "textMarker" ;
		  statTextTweets++ ;
		  $('#textTweetStatCount').html(statTextTweets) ;
	  }else if(jQuery.isEmptyObject(geotweet[i].media) && geotweet[i].isLinkTweet){
		  markerClass = "linkMarker" ;
		  statLinkTweets++ ;
		  $('#linkTweetStatCount').html(statLinkTweets) ;
	  } else if(geotweet[i].media.photo != "undefined" || geotweet[i].isPhotoLinked){
		  markerClass = "photoMarker" ;
		  statPhotoTweets++ ;
		  $('#photoTweetStatCount').html(statPhotoTweets) ;
	  }  else if(geotweet[i].media.audio != "undefinded"){
		  markerClass = "musicMarker" ;
		  statMusicTweets++ ;
		  $('#musicTweetStatCount').html(statMusicTweets) ;
	  }
  
 
	  addMarker(map,geotweet[i], markerClass, shape, i) ;
  }
  if(geotweet.length != 0)
 	 setTimeout(function(){ $('#statsContainer').slideDown() ;}, geotweet.length*100) ;
  
}

function addMarker(map, geotweet, markerClass, shape, i){
	//console.log(markersAdded) ;

	 setTimeout(function() {

		 var beach = location;
	    var myLatLng = new google.maps.LatLng(geotweet.latitude, geotweet.longitude);
	     var shape = {
      coords: [1, 1, 1, 40, 40, 40, 40 , 1],
      type: 'poly'
  };
	    var marker = new MarkerWithLabel({
	        position: myLatLng,
	        map: map,
	        icon: {
	            path: google.maps.SymbolPath.CIRCLE,
	            scale: 0, //tamaasdasdo 0
	          },
	        shape: shape,
	        //title: beach[0],
	        zIndex: i, 
	        labelClass: markerClass
	        //animation: google.maps.Animation.DROP
	    });
	    marker.geotweet = geotweet ;

   var pic = '' ;
   
   if(!jQuery.isEmptyObject(geotweet.media)){
	   pic = '<hr/><img src="' + geotweet.media.photo + '" style="min-width:300px;max-width:300px; max-height:500px;"/>' ;
   } else{
	   pic = '' ;
   }
	  
   
   var tweetWindow = new google.maps.InfoWindow({
	      content: '<div style="min-width:300px;max-width:320px;height:auto;"><b><a href="http://twitter.com/'+geotweet.user+'" target="_blank">'+geotweet.user+'</a>: </b>' + geotweet.content + '<br/>' + pic + '</div>'
	});
	
	
	
   /*google.maps.event.addListener(marker, 'mouseover', function() {
	  
  	tweetWindow.open(map,marker) ;
  	var iw_container = $(".gm-style-iw").parent();
    iw_container.stop().hide();
    iw_container.fadeIn("slow");
    marker.clicked = false ;
  });*/
  
  google.maps.event.addListener(marker, 'click', function() {
  	tweetWindow.open(map,marker) ;
	marker.clicked = true ;
	console.log(marker.geotweet.longitude+";"+marker.geotweet.latitude+","+marker.geotweet.hash) ;
  });
  
   google.maps.event.addListener(marker, 'mouseout', function() {   
   	if(!marker.clicked){
 		tweetWindow.close() ;
 		marker.clicked = false ;
   }
	  
  });
   
      
   var max_count = 0 ;
   
   if(mediaType == null)
   	max_count = statTextTweets + statPhotoTweets + statVideoTweets + statLinkTweets ;
   else if(mediaType == "photo")
   	max_count = statPhotoTweets ;
   else if(mediaType == "video")
   	max_count = statVideoTweets ;
   else if(mediaType == "weblink")
    max_count = statLinkTweets ;
   
   if(markersAdded <= max_count){
	   markersAdded++ ;
   }
	
	$('#tweetsFound').html('Tweets found') ;
	$('#tweetsLoadedCount').html(markersAdded) ;
	  }, i * 100);
}

google.maps.event.addDomListener(window, 'load', initialize);

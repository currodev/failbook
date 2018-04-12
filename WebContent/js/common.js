/**
 * 
 */

$(document).ready(function() {
	
	$("#buttonSearch").click(function(){
		var term = $("#inputSearch").val();
		window.location.href = "/Failbook/search/?q=" + term;
	});
});
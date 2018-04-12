/**
 * 
 */

$(document).ready(function() {
	var url = new URL(window.location.href);
	var id = url.searchParams.get("id");
	if (id === null)
		id = 0;
	getProfile(id);
	getPosts(id);
});

function getProfile(id) {

	var data = {
		id : id,
	};
	var req = sendAPIRequest("User", "GET", data);
	req.done(function(response) {
		console.log(response);
	});
	req.fail(function(response) {
		var message = getMessageFromErrorResponse(response);
		alert(message);
	});

}

function getPosts(id) {

	var data = {
		scope : "profile",
		id : id,
	};
	var req = sendAPIRequest("Post", "GET", data);
	req.done(function(response) {
		console.log(response);
	});
	req.fail(function(response) {
		var message = getMessageFromErrorResponse(response);
		alert(message);
	});

}
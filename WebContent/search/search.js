/**
 * 
 */

$(document).ready(function() {
	var url = new URL(window.location.href);
	var term = url.searchParams.get("q");
	if (term != null)
		getSearchResults(term);
});

function getSearchResults(name) {
	var data = {
		name : name,
	};
	var req = sendAPIRequest("Search", "GET", data);
	req.done(function(response) {
		console.log(response);
	});
	req.fail(function(response) {
		var message = getMessageFromErrorResponse(response);
		alert(message);
	});
}
/**
 * 
 */

$(document).ready(function() {
	getStream();

	$("#your_post").keypress(function(e) {
		if (e.which === 13) {
			postUpdate();
		}
	});
});

function getStream() {

	var data = {
		scope : "home",
	};
	var req = sendAPIRequest("Post", "GET", data);
	req.done(function(response) {
		$(".list-unstyled").empty();
		var stream = response.message;
		for (var i = 0; i < stream.length; i++) {
			printPost(stream[i].name, stream[i].content, stream[i].date);
		}
	});
	req.fail(function(response) {
		var message = getMessageFromErrorResponse(response);
		alert(message);
	});

}

function postUpdate() {

	var content = $("#your_post").val();
	if (content == "")
		return;
	var data = {
		content : content,
	};
	var req = sendAPIRequest("Post", "POST", data);
	req.done(function(response) {
		console.log(response);
		$("#your_post").val('');
		getStream();
	});
	req.fail(function(response) {
		var message = getMessageFromErrorResponse(response);
		alert(message);
	});
}

function printPost(name, content, date) {
	var post = '<li class="media post"><img class="mr-3 rounded-circle" src="" alt="Avatar"> <div class="media-body"> <span class="mt-0 mb-1 name">'
			+ name
			+ '</span> <div class="post_content">'
			+ content
			+ '</div> </div></li>';
	$(".list-unstyled").append(post);
}
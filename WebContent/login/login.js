/**
 * 
 */

$(document).ready(function() {

	$("#buttonLogin").click(function() {
		login();
	});
});

function login() {

	data = {
		email : $("#inputEmail").val(),
		password : $("#inputPassword").val(),
	}

	var req = sendAPIRequest("Auth", "POST", data);
	req.done(function() {
		window.location.pathname = "/Failbook/home/";
	});
	req.fail(function(response) {
		var message = getMessageFromErrorResponse(response);
		$("#signup-result").removeClass("alert-success");
		$("#signup-result").addClass("alert-danger");
		$("#signup-result").text(message);
	});
}
$(document).ready(function() {
    $('#clearRevButton').click(function() {
        $("input:radio[name=likert]:checked")[0].checked = false;
        $("#recomand").val("").empty();
    });
});

$(document).ready(function(){

    $("#submitRevButton").click(function(){

        // Perform your action on click here, like redirecting to a new url
        window.location='assign-reviewers.html';
    });

});

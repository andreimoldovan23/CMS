

window.onload =
    function () {
        getPaper();
        getAvailablePcMembers();

    };


var TOKEN_KEY = "jwtToken"


function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}


function getPaper() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/papers/" + localStorage.getItem("papersId"),
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            localStorage.setItem("papersId", data.id);
            localStorage.setItem("papersName", data.name);

            $("#paper-name").text(data.name);

        },
        error: function (jqXHR) {
            if (jqXHR.status === 403) {
                $("#paperName").text("FORBIDDEN");
            }
        }
    })
}

function getAvailablePcMembers () {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers/" + localStorage.getItem("papersId") +
            "/pcmembersNotAssigned",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            $("#table-body").html("");
            for (var i = 0; i < data.length; i++) {

                $("#table-body").append(
                    "<tr>\n" +
                    "                    <td><a href=\"#\">" + data[i].user.username +"</a></td>\n" +
                    "                    <td id=\"pcmembr-bid\">\n" +
                    "                        <input type=\"checkbox\" name=\"likert\" value=\"" + data[i].user.username + "\">\n" +
                    "                    </td>\n" +
                    "                </tr>"
                );
            }
        },
        error: function (jqXhr) {
            if (jqXhr.status === 401) {
                alert("Unauthorized");
            } else if (jqXhr.status === 403) {
                alert("Forbidden");
            } else {
                alert(jqXhr.responseText);
            }
        }
    })
}


function addReviewer (reviewer) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers/" + localStorage.getItem("papersId") +
            "/reviewers",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "POST",
        data: JSON.stringify(reviewer),
        contentType:'application/json',
        error: function (jqXhr) {
            if (jqXhr.status === 401) {
                alert("Unauthorized");
            } else if (jqXhr.status === 403) {
                alert("Forbidden");
            } else {
                alert(jqXhr.responseText);
            }
        }
    })
}

$("#reviewersForm").submit(
    (function (event) {
        event.preventDefault();

        var $form = $(this);
        var checked =  $('input[name="likert"]:checked');

        for (var i = 0; i < checked.length; i++) {
            console.log(checked[i].value);
            var reviewer = {
                user: {
                    username: checked[i].value
                }
            }
            addReviewer(reviewer);
        }
        popUp();
    })
)


function popUp() {
    var modal = document.getElementById("rev-pop");

    // Get the button that opens the modal
    var btn = document.getElementById("trigg-confirm");

    // Get the <span> element that closes the modal
    var span = document.getElementsByClassName("close")[0];

    modal.style.display = "block";
    span.onclick = function() {
        modal.style.display = "none";
        window.location.replace("../pages/conf-page.html");
    }

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            window.location.replace("../pages/conf-page.html");
        }
    }
}

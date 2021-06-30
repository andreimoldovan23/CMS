
console.log("started");

$("#conf-name").html(localStorage.getItem("conferenceName").toString());
console.log(localStorage.getItem("conferenceName"));
console.log(localStorage.getItem("conferenceLocation"));
console.log(localStorage.getItem("conferenceStartDate"));

$("#conf-loc").html(localStorage.getItem("conferenceLocation").toString());
$("#conf-date").html(
    "<div class='first'>" +
    "<input type=\"date\" id=\"start-date\" name=\"start-date\" value=\"" + localStorage.getItem("conferenceStartDate") + "\"" +
    "readOnly=\"true\">" +
    "</div> <div>" +
    "<input type=\"date\" id=\"end-date\" name=\"end-date\" value=\"" + localStorage.getItem("conferenceEndDate") + "\"" +
    "readOnly=\"true\"></div>");


var TOKEN_KEY = "jwtToken";

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function hasBidded () {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/pcmembers/hasBidded",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            if (data.likeToReview === true) {
                localStorage.setItem("hasBidded", "true");
            }
            else {
                localStorage.setItem("hasBidded", "false");
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



function getPcMembers() {
    if (localStorage.getItem("BIDDING") === "active" && localStorage.getItem("PCMEMBER") === "true" && localStorage.getItem("hasBidded") === "false") {
        $("#buttons").html("<button id=\"bid_button\" class=\"btn\" style=\"margin-bottom: 10px\">Bid</button>");
        $("#bid-pop-up").html("<div id=\"bid-pop\" class=\"ses-pop\">\n" +
            "          <div class=\"ses-pop-content\">\n" +
            "            <div class=\"ses-pop-header\">\n" +
            "              <span class=\"close\">&times;</span>\n" +
            "              <h2>You have bidded</h2>\n" +
            "            </div>\n" +
            "            <div class=\"ses-pop-body\">\n" +
            "              <p>You will be assigned to review papers for this conference</p>\n" +
            "            </div>\n" +
            "\n" +
            "          </div>\n" +
            "        </div>")
    }

    if (localStorage.getItem("REGISTRATION") === "active") {
        if (localStorage.getItem("AUTHOR") !== "true") {

            if (!$('#regAuthor').length) {
                $("#buttons").append("<a id=\"regAuthor\" onclick=\"registerAsAuthor()\" class=\"btn last\"" + "href=\"#\" style='margin-top: 10px'>Register as Author</a>");
            }
        }
        if (localStorage.getItem("LISTENER") !== "true") {
            if (!$('#regListener').length) {
                $("#buttons").append("<a id=\"regListener\" onclick=\"registerAsListener()\" class=\"btn last\"" + "href=\"#\"  style='margin-top: 10px'>Register as Listener</a>");
            }
        }

    }
    $('#bid_button').click(function () {
        bid();
    });

    if (localStorage.getItem("CHAIR") === "true") {
        $.ajax({
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/pcmembers",
            type: "GET",
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            success: function (data) {
                var content = "";
                content = " <div id=\"conf-pop\" class=\"conf-pop\">\n" +
                    "                    <div class=\"conf-pop-content\">\n" +
                    "                        <div class=\"conf-pop-header\">\n" +
                    "                            <span class=\"close\">&times;</span>\n" +
                    "                            <h2>PC Members</h2>\n" +
                    "                        </div>\n" +
                    "                        <div class=\"conf-pop-body\">\n" +
                    "                            <div className=\"\" id=\"session-table\">" +
                    "<table>" +
                    "<tbody>"
                ;
                console.log(data.length);
                for (var i = 0; i < data.length; i++) {
                    console.log(data[i]);
                    content += "<tr>" +
                        "<td>" +
                        data[i].user.username +
                        "</td>" +
                        "</tr>";
                }
                content += "</tbody>" +
                    "                   </table>\n" +
                    "                        </div>\n" +
                    "\n" +
                    "                    </div>\n" +
                    "                </div>"
                $("#pop-up").html(content);
                if (localStorage.getItem("REGISTRATION") === "active" || localStorage.getItem("BIDDING") === "active") {
                    $("#buttons").prepend("<a class=\"btn\" href=\"../pages/pcmembers-confirm.html\" style=\"margin-bottom: 10px\">Add PC Members</a>");
                }
                $("#buttons").prepend("<button class=\"btn\" onclick='showPopUp()' style=\"margin-bottom: 10px\">View PC Members</button>");

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
        });
    }
}

function bid() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" +
            localStorage.getItem("conferenceId") +
            "/pcmembers/bidding",
        type: "PUT",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        data: {
            "available": true
        },
        success: function () {
            localStorage.setItem("hasBidded", "true");
            biddingPopUp();
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
    });
}

function userHasAnyRoles() {
    return localStorage.getItem("AUTHOR") === "true" || localStorage.getItem("LISTENER") === "true" ||
        localStorage.getItem("CHAIR") === "true" || localStorage.getItem("ADMIN") === "true" ||
        localStorage.getItem("PCMEMBER") === "true";
}

function performRegisterAsListenerRequest() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/registerAsListener",
        type: "POST",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            $("#regListener").hide();
            setJwtToken(data.idToken);
            location.reload();
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
    });
}

function registerAsListener() {
    if (!userHasAnyRoles()) {
        localStorage.setItem("payFor", "LISTENER");
        paymentPopUp();
    } else {
        performRegisterAsListenerRequest();
    }
}

function setJwtToken(token) {
    let TOKEN_KEY = "jwtToken";
    localStorage.setItem(TOKEN_KEY, token);
}

function performRegisterAsAuthorRequest() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/registerAsAuthor",
        type: "POST",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            $("#regAuthor").hide();
            setJwtToken(data.idToken);
            location.reload();
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
    });
}

function registerAsAuthor() {
    if (!userHasAnyRoles()) {
        localStorage.setItem("payFor", "AUTHOR");
        paymentPopUp();
    } else {
        performRegisterAsAuthorRequest();
    }
}



function showPopUp () {
    var modal = document.getElementById("conf-pop");

    // Get the <span> element that closes the modal
    var span = document.getElementsByClassName("close")[0];

    modal.style.display = "block";
    span.onclick = function() {
        modal.style.display = "none";
    }

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    }
}


function biddingPopUp () {
    var modal = document.getElementById("bid-pop");

    // Get the <span> element that closes the modal
    var span = document.getElementsByClassName("close")[0];

    modal.style.display = "block";
    span.onclick = function() {
        modal.style.display = "none";
        window.location.reload();
    }

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            window.location.reload();
        }
    }
}

function paymentPopUp() {
    paymentFormDisplay();

    var modal = document.getElementById("payment-pop");

    // Get the <span> element that closes the modal
    var span = document.getElementsByClassName("close")[0];

    modal.style.display = "block";
    span.onclick = function() {
        modal.style.display = "none";
        window.location.reload();
    }

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            window.location.reload();
        }
    }
}

$('#btnClick').on('click',function(){
    if($('#1').css('display')!='none'){
        $('#2').show().siblings('div').hide();
    }
    if($('#2').css('display')!='none'){
        $('#1').show().siblings('div').hide();
    }
    if($('#3').css('display')!='none'){
        $('#1').show().siblings('div').hide();
    }
});

function replace(show, hide1, hide2 ) {
    document.getElementById(hide1).style.display="none";
    document.getElementById(hide2).style.display="none";
    document.getElementById(show).style.display="block";
}

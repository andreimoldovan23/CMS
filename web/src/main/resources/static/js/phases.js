
var result1 = "";

var TOKEN_KEY = "jwtToken";

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}


function startPhase (phaseId) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/phases/" + phaseId + "/start",
        type: "PUT",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function () {
            alert("Success!");
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

function phaseTable() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/phases",
        type: "GET",
        success: function (data) {
            data.sort(function(a, b){console.log(a.deadlineString); console.log(b.deadlineString); return new Date(a.deadlineString.substr(0, 10)) - new Date(b.deadlineString.substr(0, 10))});
            result1 = "";
            result1 +=
                "<div className=\"scrollable details-table\" id=\"phases-table\">\
                    <h1>Phases</h1>\
                    <table>\
                        <thead>\
                        <tr>\
                            <th>Name</th>\
                            <th>Deadline</th>\
                            <th>Status</th>";
                        if (localStorage.getItem("ADMIN") === "true" || localStorage.getItem("CHAIR") === "true") {
                            result1 += "<th>Update status</th>";
                        }

                        result1 += "</tr>\
                        </thead>\
                        <tbody>"
            for (var i = 0; i < data.length; i++) {
                result1 +=
                    "<tr>\
                     <td><a href=\"#\">" + data[i].name + "</a></td>\
                                <td>" + data[i].deadlineString.substr(0, 10) + "</td>\
                                <td>";
                console.log(data[i].isActive);
                if (data[i].isActive === true) {
                    result1 += "active";
                    if (data[i].name === "REGISTRATION") {
                        localStorage.setItem("currentPhase", "REGISTRATION");

                    }
                    if (data[i].name === "SESSION_REGISTRATION") {
                        localStorage.setItem("currentPhase", "SESSION_REGISTRATION");
                    }
                    if (data[i].name === "BIDDING") {
                        localStorage.setItem("currentPhase", "BIDDING");
                    }
                    if (data[i].name === "REVIEWING") {
                        localStorage.setItem("currentPhase", "REVIEWING");
                    }
                    if (data[i].name === "SUBMITTING") {
                        localStorage.setItem("currentPhase", "SUBMITTING");
                    }

                    localStorage.setItem(data[i].name, "active");
                    if (localStorage.getItem("ADMIN") === "true" || localStorage.getItem("CHAIR") === "true") {
                        result1 += "<td></td>";
                    }

                } else {
                    localStorage.setItem(data[i].name, "inactive");
                    result1 += "inactive";
                    console.log(localStorage.getItem("CHAIR"));
                    if (localStorage.getItem("ADMIN") === "true" || localStorage.getItem("CHAIR") === "true") {
                        result1 += "</td>\
                                <td><a id=\"toggle-status-button\" class=\"btn inverse req-conf add-phase-button\" onclick='startPhase("+ data[i].id +")' href=\"#\">Start phase</a></td>";0
                    }

                }
                result1 += "</tr>"
            }
            result1 +=
                "</tbody>\
            </table>"

               var anchorsContent = "<ul><li><a id=\"phasesHyper\" href=\"#\" onClick=\"showPhases()\"> PHASES</a></li>";
               if (localStorage.getItem("SESSION_REGISTRATION") === "active") {
                   anchorsContent += "<li><a id=\"sessionsHyper\" href=\"#\" onClick=\"showSessions()\">SESSIONS</a></li>";
                   if (localStorage.getItem("CHAIR") === "true" || localStorage.getItem("PCMEMBER") === "true" ||
                        localStorage.getItem("AUTHOR") === "true") {
                       anchorsContent +=    "<li><a id=\"papersHyper\" href=\"#\">PAPERS:</a><ul>";
                       anchorsContent += "<li><a id=\"papersHyper\" href=\"#\" onClick=\"showPapersFinal()\">All papers</a></li>";
                   }
               }
               else {
                   if (localStorage.getItem("ADMIN") === "true" || localStorage.getItem("CHAIR") === "true"){
                       if (localStorage.getItem("BIDDING") === "active" || localStorage.getItem("REVIEWING") === "active" || localStorage.getItem("SUBMITTING") === "active") {
                           anchorsContent += "<li><a id=\"sessionsHyper\" href=\"#\" onClick=\"showSessions()\">SESSIONS</a></li>" +
                               "<li><a id=\"roomsHyper\" href=\"#\" onClick=\"showRooms()\">ROOMS</a></li>";
                       }

                       anchorsContent +=    "<li><a id=\"papersHyper\" href=\"#\">PAPERS:</a><ul>";
                       if (localStorage.getItem("AUTHOR") === "true") {
                           anchorsContent += "<li><a id=\"papersHyper\" href=\"#\" onClick=\"showMyPapers()\">My papers</a></li>";
                       }

                       if (localStorage.getItem("REVIEWING") === "active" && localStorage.getItem("PCMEMBER") === "true") {
                           anchorsContent += "<li><a id=\"papersHyper\" href=\"#\" onClick=\"showReviewPapers()\">Review papers</a></li>";
                       }

                       if (localStorage.getItem("CHAIR") === "true" && localStorage.getItem("REVIEWING") === "active") {
                           anchorsContent += "<li><a id=\"papersHyper\" href=\"#\" onClick=\"showAllPapers()\">All papers</a></li>" +
                               "</ul></li>";
                       }

                   }
                   else {
                       if (localStorage.getItem("AUTHOR") === "true") {
                           anchorsContent +=    "<li><a id=\"papersHyper\" href=\"#\">PAPERS:</a><ul>";
                           anchorsContent += "<li><a id=\"papersHyper\" href=\"#\" onClick=\"showMyPapers()\">My papers</a></li>";
                           if (localStorage.getItem("REVIEWING") === "active" && localStorage.getItem("PCMEMBER") === "true") {
                               anchorsContent += "<li><a id=\"papersHyper\" href=\"#\" onClick=\"showReviewPapers()\">Review papers</a></li>";
                           }
                           anchorsContent += "</ul></li>";
                       }
                       else {
                           if (localStorage.getItem("REVIEWING") === "active" && localStorage.getItem("PCMEMBER") === "true") {
                               anchorsContent +=    "<li><a id=\"papersHyper\" href=\"#\">PAPERS:</a><ul>";
                               anchorsContent += "<li><a id=\"papersHyper\" href=\"#\" onClick=\"showReviewPapers()\">Review papers</a></li>";
                               anchorsContent += "</ul></li>";
                           }
                       }

                   }
               }
               anchorsContent += "</ul>";
                    $("#listRight").html(anchorsContent);
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

function showPhases() {
    $("#replace-table").html(result1);
}

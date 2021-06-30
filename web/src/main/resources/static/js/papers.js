var result2 = "";
var TOKEN_KEY = "jwtToken";
var authorList = "";
var keywordsList = "";
var topicsList = "";


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

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function getPapersOfAuthor() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papersOfAuthor",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            console.log(data);
            result2 = "";
            result2 +=
                "<div className=\"scrollable details-table\" id=\"papers-table\">\
                    <table>\
                        <thead>\
                        <tr>\
                            <th>Paper Name</th>\
                            <th>Status</th>";
                        if (localStorage.getItem("SUBMITTING") === "active") {
                            result2 += "<th>Options</th>";
                        }

                        result2 += "</tr>\
                        </thead>\
                        <tbody>\
                        ";
            for (var i = 0; i < data.length; i++) {
                result2 += "<tr><td><a onclick=\"setPapersId(" + data[i].id + ")\"" +
                    " href=\"view-paper.html\">"+ data[i].name +"</a></td>\
                        <td>"+ data[i].status +"</td>";
                        if (localStorage.getItem("SUBMITTING") === "active") {
                            result2 += "<td><a id=\"toggle-status-button\" class=\"btn inverse req-conf add-phase-button\" \
                           onclick='setActionPaper(\"update\"); setPapersId(" + data[i].id + ")' href=\"paper-confirm.html\">Edit</a>\
                                <a id=\"toggle-status-button\" class=\"btn inverse req-conf add-phase-button\" \
                                    onclick=\"deletePaper("+ data[i].id + ")\" href=\"#\">Delete</a></td>";
                        }

                        result2 += "</tr>";
            }

            result2 += "\
                    </tbody>\
                </table>";
            if (localStorage.getItem("SUBMITTING") === "active") {
                result2 += "<a id=\"author-submit\" class=\"btn req-conf request-button\"\
               onclick=\"setActionPaper('add')\" href=\"paper-confirm.html\">Add</a>";
            }

            result2 += "</div>";
            $("#replace-table").html(result2);

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

function setPapersId(id) {
    localStorage.setItem("papersId", id);
    localStorage.setItem("review", "false");

}

function setPaperToReview(id) {
    localStorage.setItem("review", "true");
    localStorage.setItem("papersId", id);
    window.location.replace("../pages/view-paper.html");
}

function setPaperAssign (id) {
    localStorage.setItem("papersId", id);
    window.location.replace("../pages/assign-reviewers.html");
}

function setPaperReviews (id) {
    localStorage.setItem("papersId", id);
    window.location.replace("../pages/view-reviews.html");
}

function getAllPapers() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers",
        headers:  {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            console.log(data);
            result2 = "";
            result2 +=
                "<div className=\"scrollable details-table\" id=\"papers-table\">\
                    <table>\
                        <thead>\
                        <tr>\
                            <th>Paper Name</th>\
                            <th>Status</th>\
                            <th>Options</th>\
                            \<th id=\"pcmembr-bid\" colspan=\"2\"></th>\
                        </tr>\
                        </thead>\
                        <tbody>\
                        ";
            for (var i = 0; i < data.length; i++) {
                var assignPage = "../pages/assign-reviewers.html";
                var reviewPage = "../pages/view-reviews.html";
                result2 += "<tr><td><a class=\"paper-id\" onclick=\"setPapersId(" + data[i].id + ")\"" +
                    " href=\"view-paper.html\">"+ data[i].name +"</a></td>\
                        <td class=\"paper-status\">"+ data[i].status +"</td>" +
                    "<td class=\"button-col\">\n" +
                    "            <a id=\"pcmembr-bid\" class=\"btn inverse req-conf request-button\" onclick=\"setPaperAssign(" + data[i].id + ")\" href=\"#\">Assign Reviewers</a>\n" +
                    "          </td>\n" +
                    "          <td>\n" +
                    "            <a class=\"btn inverse req-conf request-button trigg-acc-confirm\" " +
                    "                 onclick=\"acceptOrDecline("+ data[i].id +", 1)\" href=\"#\">Accept</a>\n" +
                    "            <a class=\"btn inverse req-conf request-button trigg-dcl-confirm\" " +
                    "                 onclick=\"acceptOrDecline("+ data[i].id +", 0)\" href=\"#\">Decline</a>\n" +
                    "          </td></tr>";
            }

            result2 += "\
                    </tbody>\
                </table>\
            </div>\
            <button class=\"btn\" onclick=\"analysePapers()\">Analyze papers</button>";
            $("#replace-table").html(result2);
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

function getPapersFinal() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers",
        headers:  {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            console.log(data);
            result2 = "";
            result2 +=
                "<div className=\"scrollable details-table\" id=\"papers-table\">\
                    <table>\
                        <thead>\
                        <tr>\
                            <th>Paper Name</th>\
                            <th>Status</th>\
                            \<th id=\"pcmembr-bid\" colspan=\"2\"></th>\
                        </tr>\
                        </thead>\
                        <tbody>\
                        ";
            for (var i = 0; i < data.length; i++) {
                result2 += "<tr><td><a class=\"paper-id\" onclick=\"setPapersId(" + data[i].id + ")\"" +
                    " href=\"view-paper.html\">"+ data[i].name +"</a></td>\
                        <td class=\"paper-status\">"+ data[i].status +"</td>" +
                    "<td class=\"button-col\">\n" +
                    "          </td>\n" +
                    "<td class=\"button-col\">\n" +
                    "  <a id=\"pcmembr-bid\" class=\"btn inverse req-conf request-button\" onclick=\"setPaperReviews(" + data[i].id +")\" href=\"#\">View Reviews</a>\n" +
                    "</td>\n" +
                    "</tr>";
            }

            result2 += "\
                    </tbody>\
                </table>\
            </div>";
            $("#replace-table").html(result2);
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


function getPapersToReview() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papersToReview",
        headers:  {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            console.log(data);
            result2 = "";
            result2 +=
                "<div className=\"scrollable details-table\" id=\"papers-table\">\
                    <table>\
                        <thead>\
                        <tr>\
                            <th>Paper Name</th>\
                            <th>Status</th>\
                            <th>Options</th>\
                        </tr>\
                        </thead>\
                        <tbody>\
                        ";
            for (var i = 0; i < data.length; i++) {
                var assignPage = "../pages/assign-reviewers.html";
                var reviewPage = "../pages/view-reviews.html";
                result2 += "<tr><td><a class=\"paper-id\" onclick=\"setPapersId(" + data[i].id + ")\"" +
                    " href=\"view-paper.html\">"+ data[i].name +"</a></td>\
                        <td class=\"paper-status\">"+ data[i].status +"</td>" +
                    "<td class=\"button-col\">\n" +
                    "            <a id=\"pcmembr-bid\" class=\"btn inverse req-conf request-button\" onclick=\"setPaperToReview(" + data[i].id + ")\">Review</a>\n" +
                    "          </td>\n";
            }

            result2 += "\
                    </tbody>\
                </table>\
            </div>\
            ";
            $("#replace-table").html(result2);
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

function deletePaper(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/papers/" + id,
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "DELETE",
        success: function () {
            alert("Paper deleted");
            window.location.reload();
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

function addAuthorToPaper(userObj) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/papers/" + localStorage.getItem("papersId") + "/authors",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "PUT",
        contentType:'application/json',
        data: JSON.stringify(userObj),
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

function getAuthorsOfPaper() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/papers/" + localStorage.getItem("papersId") + "/authors",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            $("#authorList").empty();
            data.forEach(author => {
                var text = "<li style='{text-decoration: none; display: inline; float: left}'><span>" +
                    author.user.username + "--" + author.user.name + "</span></li>   ";
                $("#authorList").append(text);
            })
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

            $("#paperName").text(data.name);

            $("#topicList").empty();
            data.topics.forEach(topic => {
                var text = "<li style='{text-decoration: none; display: inline; float: left}'><span>" + topic + "</span></li>   ";
                $("#topicList").append(text);
            })

            $("#keywordList").empty();
            data.keywords.forEach(keyword => {
                var text = "<li style='{text-decoration: none; display: inline; float: left}'><span>" + keyword + "</span></li>   ";
                $("#keywordList").append(text);
            })

            getAuthorsOfPaper();

            getPaperFile();
            getPresentationFile();
        },
        error: function (jqXHR) {
            if (jqXHR.status === 403) {
                $("#paperName").text("FORBIDDEN");
            }
        }
    })
}

function analysePapers() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/papers",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "PUT",
        success: function () {
            getAllPapers()
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

function acceptOrDecline(id, code) {
    var status = 1;
    if (code === 1)
        status = "accepted";
    else
        status = "declined";

    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/papers/" + id + "/" + status,
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "PUT",
        success: function () {
            window.location.reload();
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

function reviewPaper() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/papers/" + localStorage.getItem("papersId") + "/reviews",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "POST",
        contentType:'application/json',
        data: JSON.stringify({
            suggestion: $("#recomand").val(),
            grade: $(".gradeInput:checked").val()
        }),
        success: function () {
            localStorage.setItem("review", "false");
            $("#clearRevButton").click();
            popUp();
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

function showPapers() {
    $("#replace-table").html(result2);
}

function getPaperFile() {
    $("#paperFile").attr("src", "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId")
        + "/papers/" + localStorage.getItem("papersId") + "/file");
}

function getPresentationFile() {
    $("#presentationFile").attr("src", "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId")
        + "/papers/" + localStorage.getItem("papersId") + "/presentationFile");
}

function setActionPaper(param) {
    localStorage.setItem("actionPap", param);
}

function showMyPapers() {
    getPapersOfAuthor();
}

function showPapersFinal() {
    getPapersFinal();
}

function showAllPapers() {
    getAllPapers();
}

function showReviewPapers() {
    getPapersToReview();
}

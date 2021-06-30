var TOKEN_KEY = "jwtToken";

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

$(document).ready(
    function () {
        getPaper();
        getTable();
    }
);

function getPaper() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/papers/" + localStorage.getItem("papersId"),
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            $("#paper-name").html(data.name);
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

function getTable() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers/" + localStorage.getItem("papersId") + "/reviews",
        headers:  {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            console.log(data);
            let content = "";
            content +=
                "<div className=\"scrollable details-table\" id=\"papers-table\">\
                    <table>\
                        <thead>\
                        <tr>\
                            <th>Reviewer</th>\
                            <th>Grade</th>\
                            <th>Suggestion</th>\
                        </tr>\
                        </thead>\
                        <tbody>\
                        ";
            for (var i = 0; i < data.length; i++) {
                content += "<tr><td><a class=\"paper-id\" href=\"view-paper.html\">"+ data[i].reviewer.username +"</a></td>\
                        <td class=\"paper-status\">"+ data[i].grade +"</td>\
                        <td class=\"paper-status\">"+ data[i].suggestion +"</td>" +
                    "</tr>";
            }

            content += "\
                    </tbody>\
                </table>\
            </div>";

            $("#paper-reviews-table").html(content);
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

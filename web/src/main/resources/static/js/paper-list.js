var TOKEN_KEY = "jwtToken";

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setContentPapers(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers/" + id,
        type: "GET",
        success: function (data) {
            localStorage.setItem("paperId", data.id);
            localStorage.setItem("paperName", data.name);
            window.location.replace("../pages/view-paper.html");
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


function deletePapers(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers/" + id,
        type: "DELETE",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function () {
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

$(document).ready(function(){
    $.ajax({ url: "http://localhost:8080/api/conferences" + localStorage.getItem("conferenceId") + "/papers",
        type: "GET",
        success: function (data) {
            console.log(data);
            var result = "<table class=\"papers-table\">"

            result += "<thead><tr><th>Name</th> <th colspan=\"2\" ></th><th> </th></tr>\n" +
                "            </thead>";

            result += "<tbody>";

            for (let i = 0; i < data.length; i++) {
                result += "<tr>";
                result +=
                    "<td><a href=\"#\" onclick=\"setContentPapers("+ data[i].id +")\">" + data[i].name + "</a></td>" +
                    "<td class=\"button-col pcmembr-bid\"> <a class=\"btn inverse req-conf request-button\" href=\"view-reviews.html\">AssignReviewers</a> </td>" +
                    "<td className=\"button-col pcmembr-bid\"> <a class=\"btn inverse req-conf request-button\" href=\"view-reviews.html\">View Reviewers</a> </td>" +
                    "<td>" +
                    "<a class=\"btn inverse req-conf request-button trigg-acc-confirm\" href=\"#\">Accept</a>" +
                    "<a class=\"btn inverse req-conf request-button trigg-dcl-confirm\" href=\"#\">Decline</a>" +
                    "</td>" +
                    "<td><a id=\"admin-conf-config\" href=\"#\" onclick=\"deletePapers("+ data[i].id + ")\" >&#10006;</a> </td>"
                "</td>";
                result += "</tr>";
            }

            result += "</tbody>";
            result += "</table>";
            $("#papers-table").html(result);
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
});

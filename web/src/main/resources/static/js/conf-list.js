var TOKEN_KEY = "jwtToken";

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function deleteConference(id) {
    $.ajax({

        url: "http://localhost:8080/api/conferences/" + id,
        type: "DELETE",
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

$(document).ready(function(){
    $.ajax({ url: "http://localhost:8080/api/conferences",
        type: "GET",
        success: function (data) {
            console.log(data);
            var result = "<table class=\"conf-table\">"

            result += "<thead><tr><th>Name</th><th>Location</th><th>Start Date</th><th>End Date</th>";
            if (localStorage.getItem("ADMIN") === "true"){
                result += "<th></th>" +
                    "</tr>\n";
            }

            result +=    "            </thead>";

            result += "<tbody>";

            for (let i = 0; i < data.length; i++) {
                result += "<tr>";
                result +=
                    "<td><a href=\"#\" onclick=\"setContent("+ data[i].id +")\">" + data[i].name + "</a></td>" +
                    "<td>" + data[i].city  + "</td>\n" +
                    "<td><input type=\"date\" id=\"start-date" + data[i].id + "\" name=\"start-date\" value=\"" + data[i].startDate.substr(0, 10) + "\"" +
                               "readOnly=\"true\"></td>" +
                    "<td><input type=\"date\" id=\"end-date" + data[i].id + "\" name=\"end-date\" value=\"" + data[i].endDate.substr(0, 10) + "\"" +
                                "readOnly=\"true\"></td>";


                    if (localStorage.getItem("ADMIN") === "true"){
                        result += "<td class=\"button-col\">" + "<a class=\"btn inverse req-conf request-button\" onclick=\"deleteConference("+ data[i].id + ")\"" +
                        "href=\"#\">Delete conference</a>" +
                        "<a class=\"btn inverse req-conf request-button\" onclick=\"editConference("+ data[i].id + ")\"" +
                        "href=\"#\">Edit conference</a>";
                        result += "</td>";
                    }


                result += "</tr>";
            }

            result += "</tbody>"

            result += "</table>";
            if (localStorage.getItem("ADMIN") === "true") {
                result +=
                    "<a class=\"btn inverse req-conf add-conf-button\" href=\"conf-confirm.html\" onClick=\"setAdd()\">Add a new conference</a>";
            }

            $("#conf-table").html(result);


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

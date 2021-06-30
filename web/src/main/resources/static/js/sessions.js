var result = "";

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

sessionIds = [];

function registerAsAttendee(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/sessions/" + id + "/register",
        type: "PUT",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function () {
            alert("Success!");
            $("#attendButton" + id).remove();
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

function deleteSession(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/sessions/" + id,
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

function setAddSession() {
    localStorage.setItem("actionSes", "add");
}

function setAddRooms() {
    localStorage.setItem("actionRoom", "add");
}

function isAttending(sesId) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
            "/sessions/" + sesId + "/isAttending",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {
            if (data.attending === true) {
                localStorage.setItem("#attendButton" + sesId.toString(), "false");
                $("#attendButton" + sesId).remove();
                $("#attendRow" + sesId).html("&check;");
            }
            else {
                localStorage.setItem("#attendButton" + sesId.toString(), "true");
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

function sessionsTable() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/sessions",
        headers:  {
            "Authorization": "Bearer " + getJwtToken()
        },
        type: "GET",
        success: function (data) {

            console.log(data);
            result = "";
            result +=
                "<div className=\"scrollable details-table\" id=\"session-table\">\
                    <h1>Session</h1>\
                    <table>\
                        <thead>\
                        <th colSpan=\"3\">Session</th>\
                        <th colSpan=\"2\">Room</th>";
                        if (localStorage.getItem("SESSION_REGISTRATION") === "active" && localStorage.getItem("LISTENER") === "true") {
                            result +=
                                "<th></th>";
                        }
                        else {
                            if (localStorage.getItem("ADMIN") === "true" || localStorage.getItem("CHAIR") === "true") {
                                result += "<th></th>";
                            }
                        }

                        result += "<tr>\
                            <th>Name</th>\
                            <th>Topic</th>\
                            <th>Chair</th>\
                            <th class=\"room-info\">Name</th>\
                            <th class=\"room-info\">Capacity</th>";
                            if (localStorage.getItem("SESSION_REGISTRATION") === "active")
                            {
                                if (localStorage.getItem("LISTENER") === "true") {
                                    result +=
                                        "<th>Reserve</th>";
                                }

                            }
                            else {
                                if (localStorage.getItem("ADMIN") === "true" || localStorage.getItem("CHAIR") === "true") {
                                    result += "<th class=\"room - add\">Options</th>";
                                }
                            }
                        result += "</tr>\
                        </thead>\
                        <tbody>"
                        for (var i = 0; i < data.length; i++) {
                            result += "<tr>\
                            <td><a href=\"#\">"+ data[i].name +"</a></td>\
                            <td>" + data[i].topic +"</td>";
                                if (data[i].chair != null) {
                                    result +="<td>" + data[i].chair.user.username +"</td>";
                                }
                                else {
                                    result +="<td>" +"</td>";
                                }

                            result += "<td class=\"room-info\">"+ data[i].room.name +"</td>\
                            <td class=\"room-info\">"+ data[i].room.capacity +"</td>";
                            if (localStorage.getItem("SESSION_REGISTRATION") === "active") {
                                sessionIds.push(data[i].id);
                                if (localStorage.getItem("LISTENER") === "true") {
                                    result +=
                                        "<td class=\"button-col\" id=\"attendRow" + data[i].id +"\">";
                                        result +=
                                            "<a id=\"attendButton" + data[i].id +"\" class=\"btn inverse req-conf request-button trigg-confirm\" onclick=\"registerAsAttendee(" + data[i].id + ")\" href=\"#\">Attend</a>";
                                    result += "</td>";
                                }
                            }
                           else {
                               if (localStorage.getItem("ADMIN") === "true" || localStorage.getItem("CHAIR") === "true") {
                                   result += "<td class=\"button-col\">\
                                        <button type=\"button\" class=\"btn inverse req-conf request-button\" onclick=\"deleteSession("+ data[i].id + ")\" href=\"\">Delete</>\
                                        <button type=\"button\" class=\"btn inverse req-conf request-button\" onclick=\"editSession("+ data[i].id + ")\" href=\"#\">Edit</button>\
                                    </td>";
                               }
                            }

                            result += "</tr>";
                        }
                        result += "\
                        </tbody>\
                    </table>";

                    if (localStorage.getItem("SESSION_REGISTRATION") !== "active") {
                        if (localStorage.getItem("ADMIN") === "true" || localStorage.getItem("CHAIR") === "true") {
                            result += "\
                    <div class=\"admin-conf-config\">\
                        <a class=\"btn inverse req-conf add-conf-button\" onclick=\"setAddSession()\" href=\"../pages/session-confirm.html\">Add</a>\
                    </div>";
                        }
                    }
                result += "</div>";
        }
    });

}

function showSessions() {
    $("#replace-table").html(result);
    sessionIds.forEach(isAttending);
}


var result3 = "";

var TOKEN_KEY = "jwtToken";

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setEditRooms(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/rooms/" + id,
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            localStorage.setItem("actionRoom", "edit");
            localStorage.setItem("roomsId", id.toString());
            localStorage.setItem("roomName", data.name);
            localStorage.setItem("roomCapacity", data.capacity);
            window.location.replace("../pages/room-confirm.html");
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


function deleteRooms(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/rooms/" + id,
        type: "DELETE",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
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
    });

}

function setAddRooms() {
    localStorage.setItem("actionRoom", "add");
}

function roomsTable() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/rooms",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            result3 = "";
            result3 +=
                "<div className=\"scrollable details-table\" id=\"session-table\">\
                    <h1>Rooms</h1>\
                    <table>\
                        <thead>\
                            <th class=\"room-info\">Name</th>\
                            <th class=\"room-info\">Capacity</th>\
                            <th class=\"room-info\">Options</th>\
                        </thead>\
                        <tbody>"
            for (var i = 0; i < data.length; i++) {
                result3 += "<tr>\
                            <td class=\"room-info\">"+ data[i].name +"</td>\
                            <td class=\"room-info\">"+ data[i].capacity +"</td>\
                            <td class=\"button-col\">\
                                <a class=\"btn inverse req-conf request-button\" onclick=\"deleteRooms("+ data[i].id + ")\" href=\"#\">Delete</a>\
                                <a class=\"btn inverse req-conf request-button\" onclick=\"setEditRooms("+ data[i].id + ")\" href=\"#\">Edit</a>\
                            </td>\
                            </tr>";
            }
            result3 += "\
                        </tbody>\
                    </table>";

            result3 += "\
                    <div class=\"admin-conf-config\">\
                        <a class=\"btn inverse req-conf add-conf-button\" onclick=\"setAddRooms()\" href=\"../pages/room-confirm.html\">Add</a>\
                    </div>\
                </div>"


        }
    });
}

function showRooms() {
    $("#replace-table").html(result3);
}

var TOKEN_KEY = "jwtToken"

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setContentSession(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId")  + "/sessions/" + id,
        type: "GET",
        success: function (data) {
            localStorage.setItem("sessionId", data.id);
            localStorage.setItem("sessionName", data.name);
            localStorage.setItem("sessionTopic", data.topic);
            localStorage.setItem("sessionRoomName", data.topic.name);
            localStorage.setItem("sessionRoomCapacity", data.topic.capacity);
            localStorage.setItem("sessionChair", data.chair);
            window.location.replace("../pages/session-confirm.html");
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

function editSession(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/sessions/" + id,
        type: "GET",
        headers:  {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            console.log(data);
            localStorage.setItem("actionSes", "edit");
            localStorage.setItem("sessionId", data.id);
            localStorage.setItem("sessionName", data.name);
            localStorage.setItem("sessionTopic", data.topic);
            localStorage.setItem("sessionRoomName", data.room.name);
            localStorage.setItem("sessionRoomCapacity", data.room.capacity);
            if (data.chair != null) {
                localStorage.setItem("sessionChair", data.chair.user.username);
            }

            window.location.replace("../pages/session-confirm.html");
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

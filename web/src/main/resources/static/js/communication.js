function getUserTypeConf(source) {

    $.ajax({
        url: "http://localhost:8080/api/conferences/" +
            localStorage.getItem("conferenceId") +
            "/user/type",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data, jqXHR) {
            const possibilities = ['PCMEMBER', 'CHAIR', 'AUTHOR', 'LISTENER'];
            possibilities.forEach(role => localStorage.setItem(role, "false"));
            const roles = data.roles;
            $("#conf-roles").html("Your roles at this conference: ");
            roles.forEach(role => {localStorage.setItem(role, "true");
                $("#conf-roles").append(role.toString().toLocaleLowerCase() + " ");
            });

            if (source === "conf-list") {
                window.location.replace("../pages/conf-page.html");
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
    });
}

function setContent(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + id,
        type: "GET",
        success: function (data) {
            localStorage.setItem("conferenceId", data.id);
            localStorage.setItem("conferenceName", data.name);
            localStorage.setItem("conferenceLocation", data.city);
            localStorage.setItem("conferenceStartDate", data.startDate.substr(0, 10));
            localStorage.setItem("conferenceEndDate", data.endDate.substr(0, 10));
            getUserTypeConf("conf-list");
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

function editConference(id) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + id,
        type: "GET",
        success: function (data) {
            localStorage.setItem("action", "edit");
            localStorage.setItem("conferenceId", data.id);
            localStorage.setItem("conferenceName", data.name);
            localStorage.setItem("conferenceLocation", data.city);
            localStorage.setItem("conferenceStartDate", data.startDate.substr(0, 10));
            localStorage.setItem("conferenceEndDate", data.endDate.substr(0, 10));
            window.location.replace("../pages/conf-confirm.html");
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

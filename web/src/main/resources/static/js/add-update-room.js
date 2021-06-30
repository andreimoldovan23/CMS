
var TOKEN_KEY = "jwtToken"

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

    function addRooms(roomData) {

        $.ajax({
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/rooms",
            type: "POST",
            data: JSON.stringify(roomData),
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            contentType: "application/json; charset=utf-8",
            success: popUp,
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

    function updateRooms(roomData) {

        $.ajax({
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/rooms/" + localStorage.getItem("roomsId"),
            type: "PUT",
            data: JSON.stringify(roomData),
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            contentType:'application/json',
            success: popUp,
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

    $("#roomAddForm").submit(function (e) {
        e.preventDefault();

        var $form = $("#roomAddForm");
        var formData = {
            name: $form.find('input[name="name"]').val(),
            capacity: $form.find('input[name="capacity"]').val()
        };
        if (localStorage.getItem("actionRoom") === "add"){
            addRooms(formData);
        }
        else {
            updateRooms(formData);
        }
    })


    function popUp() {
        var modal = document.getElementById("ses-pop");

        // Get the button that opens the modal
        var btn = document.getElementById("trigg-confirm");

        // Get the <span> element that closes the modal
        var span = document.getElementsByClassName("close")[0];

        // console.log("Pop up rooms");

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

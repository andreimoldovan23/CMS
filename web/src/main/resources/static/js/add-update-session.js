
var TOKEN_KEY = "jwtToken"

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}


var noOfSpeakers;
var SpeakersListIndexes = [];
var SpeakersList = [];
var chairsAutocompleteList = [];
var roomsAutocompleteList = [];
var speakersAutocompleteList = [];
$(document).ready(function (){
    noOfSpeakers = 1;
    localStorage.setItem("noOfSpeakers", noOfSpeakers.toString());
    SpeakersListIndexes.push(1);
    if (localStorage.getItem("actionSes") === "edit") {
        $("#name").attr('value', localStorage.getItem("sessionName"));
        $("#topic").attr('value', localStorage.getItem("sessionTopic"));
        $("#chair").attr('value', localStorage.getItem("sessionChair"));
        $("#rname").attr('value', localStorage.getItem("sessionRoomName"));
        $("#capacity").attr('value', localStorage.getItem("sessionRoomCapacity"));
        getSpeakers();
    }
    getAllChairs();
    getAllRooms();
    getAuthors();
});

function addSessionChairAutocomplete() {
    autocomplete(document.getElementById("chair"), chairsAutocompleteList);
}

function addSessionRoomAutocomplete() {
    autocomplete(document.getElementById("rname"), roomsAutocompleteList);
}

function addSessionSpeakerAutocomplete() {
    autocomplete(document.getElementById("speaker" + noOfSpeakers), speakersAutocompleteList);
}

function getAllChairs() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/chairs",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            chairsAutocompleteList = data.map(elem => elem.user.username);
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

function getAllRooms() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/rooms",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            roomsAutocompleteList = data.map(elem => elem.name);
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

function getAuthors() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/authors",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            speakersAutocompleteList = data.map(elem => elem.user.username);
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

function getSpeakers () {
    $.ajax({ url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/sessions/" + localStorage.getItem("sessionId") + "/speakers",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data, textStatus, jqXHR) {
            console.log(data);
            for (var i = 0; i < data.length; i++) {
                SpeakersList.push(data[i].username);
            }

            if (SpeakersList.length > 0) {
                $("#speaker1").attr('value', SpeakersList[0]);
                $("#speaker1").prop("readonly", true);
                var toRemoveIndex = SpeakersListIndexes.indexOf(1);
                SpeakersListIndexes.splice(toRemoveIndex, 1);
            }

            for (var i = 1; i < SpeakersList.length; i++) {
                addLabel();
                $("#speaker" + (i + 1).toString()).attr('value', SpeakersList[i]);
                $("#speaker" + (i + 1).toString()).prop("readonly", true);
                var toRemoveIndex = SpeakersListIndexes.indexOf(i + 1);
                SpeakersListIndexes.splice(toRemoveIndex, 1);
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


function addLabel() {
    console.log(noOfSpeakers);
    noOfSpeakers += 1;
    $("#speaker-labels").append(
        "<span id=\"group"+ noOfSpeakers +"\">" +
        "<div class=\"one_half first autocomplete\">\n" +
        "              <input type=\"text\" autocomplete='off' name=\"speaker"+ noOfSpeakers + "\" id=\"speaker"+ noOfSpeakers + "\" value=\"\" size=\"22\" onclick='addSessionSpeakerAutocomplete()' required>\n" +
        "            </div>" +
        "<div class=\"one_quarter last\">\n" +
        "              <button type=\"button\" class=\"btn\" id=\"remove-speaker-button"+ noOfSpeakers + "\" name=\"remove-speaker-button"+ noOfSpeakers + "\" onclick=\"removeLabel("+ noOfSpeakers + ")\">-</button>\n" +
        "            </div>" +
        "</span>"
    );
    console.log(noOfSpeakers);
    SpeakersListIndexes.push(noOfSpeakers);
    localStorage.setItem("noOfSpeakers", noOfSpeakers.toString());
}

function removeLabel(Speakers) {
    //noOfSpeakers -= 1;
    var inputValue = $("#sessionAddForm").find('input[name="speaker' + Speakers + '"]').val();
    var ind = SpeakersList.indexOf(inputValue);
    if (ind !== -1) {
        if (localStorage.getItem("actionSes") !== "add") removeSpeaker(inputValue);
        SpeakersList.splice(ind, 1);
    }

    $("#group" + Speakers).remove();
    const index = SpeakersListIndexes.indexOf(Speakers);
    SpeakersListIndexes.splice(index, 1);
}

function removeSpeaker (value) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +  "/sessions/" + localStorage.getItem("sessionId") + "/speakers?username=" + value,
        type: "DELETE",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
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

function addSpeaker (value) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/sessions/" + localStorage.getItem("sessionId") + "/speakers",
        type: "POST",
        data: JSON.stringify(value),
        contentType: 'application/json',
        headers: {
            "Authorization": "Bearer " + getJwtToken()
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


    function addSession(sessionData, roomData, speakersData) {
        $.ajax({
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/sessions",
            type: "POST",
            data: JSON.stringify(sessionData),
            contentType: 'application/json',
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            success: function (data) {
                localStorage.setItem("sessionId", data.id.toString());
                localStorage.setItem("room" + data.id.toString(), roomData);
                speakersData.forEach(addSpeaker);
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
        });
    }

    function updateSession(sessionData, roomData, speakersData) {
        $.ajax({
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/sessions/" + localStorage.getItem("sessionId"),
            type: "PUT",
            data: JSON.stringify(sessionData),
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            contentType:'application/json',
            success: function (data, textStatus, jqXHR) {
                localStorage.setItem("room" + localStorage.getItem("sessionId"), roomData);
                speakersData.forEach(addSpeaker);
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
        });
    }

    $("#sessionAddForm").submit(function (event) {
        event.preventDefault();


        var $form = $(this);
        var formData = {
            name: $form.find('input[name="name"]').val(),
            topic:$form.find('input[name="topic"]').val(),
            room:{
                name:$form.find('input[name="rname"]').val()
            },
            chair: {
                user: {
                    username:$form.find('input[name="chair"]').val()
                }
            }
        };
        var formDataUpdate = {
            name: $form.find('input[name="name"]').val(),
            topic:$form.find('input[name="topic"]').val()
        }
        var room = {
            name: $form.find('input[name="rname"]').val(),
            capacity: $form.find('input[name="capacity"]').val()
        }
        var speakers = [];
        for (var i = 0; i < SpeakersListIndexes.toString(); i++) {
            speakers.push(
                {user: {
                username: $form.find('input[name="speaker'+ SpeakersListIndexes[i] +'"]').val()
            }});
        }

        if (localStorage.getItem("actionSes") === "add"){
            addSession(formData, room, speakers);
        }
        else {
            updateSession(formDataUpdate, room, speakers);
        }
    });


function popUp() {
    var modal = document.getElementById("ses-pop");

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
        if (event.target == modal) {
            modal.style.display = "none";
            window.location.replace("../pages/conf-page.html");
        }
    }
}

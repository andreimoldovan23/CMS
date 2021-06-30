
var TOKEN_KEY = "jwtToken"

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

var noOfChairs;
var chairsListIndexes = [];
var chairsList = [];
var userAutocompleteList = [];
var phases = {};
$(document).ready(function (){
    noOfChairs = 1;
    localStorage.setItem("noOfChairs", noOfChairs.toString());
    chairsListIndexes.push(1);
    if (localStorage.getItem("action") === "edit") {
        phases = {};
        getChairs();
        getPhases();
        getNotChairsAutocomplete();
    } else {
        getAllUsersAutocomplete();
    }

});

function getNotChairsAutocomplete() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/filterNotChair",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            userAutocompleteList = data.map(elem => {
                return elem.username;
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

function getAllUsersAutocomplete() {
    $.ajax({
        url: "http://localhost:8080/api/user/all",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            userAutocompleteList = data.map(elem => {
                return elem.username;
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

function addChairAutocomplete() {
    autocomplete(document.getElementById("personResponsible" + noOfChairs), userAutocompleteList);
}

function addLabel() {
    console.log(noOfChairs);
    noOfChairs += 1;
    $("#chair-labels").append(
        "<span id=\"group"+ noOfChairs +"\">" +
        "\n" +
        "              <div class=\"two_third autocomplete\">\n" +
        "\n" +
        "          <input type=\"text\" autocomplete='off' name=\"personResponsible" + noOfChairs + "\" id=\"personResponsible" + noOfChairs + "\" value=\"\" size=\"22\" onclick='addChairAutocomplete()' required>\n" +
        "\n" +
        "            </div>\n" +
        "\n" +
        "            <div class=\"one_quarter last\">\n" +
        "              <button type=\"button\" class=\"btn\" id=\"remove-chair-button" + noOfChairs + "\" name=\"remove-chair-button" + noOfChairs + "\" onclick=\"removeLabel(" + noOfChairs +")\">-</button>\n" +
        "            </div></span>"
    );
    console.log(noOfChairs);
    chairsListIndexes.push(noOfChairs);
    localStorage.setItem("noOfChairs", noOfChairs.toString());
}

function removeLabel(chairs) {
    var inputValue = $("#confAddForm").find('input[name="personResponsible' + chairs + '"]').val();
    var ind = chairsList.indexOf(inputValue);
    if (ind !== -1) {
        if (localStorage.getItem("action") === "edit") removeChair(inputValue);
        chairsList.splice(ind, 1);
    }
    $("#group" + chairs).remove();
    const index = chairsListIndexes.indexOf(chairs);
    chairsListIndexes.splice(index, 1);
}

function popUp() {
    var modal = document.getElementById("conf-pop");

    // Get the button that opens the modal
    var btn = document.getElementById("trigg-confirm");

    // Get the <span> element that closes the modal
    var span = document.getElementsByClassName("close")[0];

    modal.style.display = "block";
    span.onclick = function() {
        modal.style.display = "none";
        window.location.replace("../pages/conf-list.html");
    }

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = "none";
            window.location.replace("../pages/conf-list.html");
        }
    }
}



    function getChairs() {
        $.ajax({ url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/chairs",
            type: "GET",
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    chairsList.push(data[i].user.username);
                }
                console.log(chairsList);

                $("#name").attr('value', localStorage.getItem("conferenceName"));
                if (chairsList.length > 0) {
                    $("#personResponsible1").attr('value', chairsList[0]);
                    $("#personResponsible1").prop("readonly", true);
                    var toRemoveIndex = chairsListIndexes.indexOf(1);
                    chairsListIndexes.splice(toRemoveIndex, 1);
                }

                console.log(chairsList.length);
                for (var i = 1; i < chairsList.length; i++) {
                    addLabel();
                    $("#personResponsible" + (i + 1).toString()).attr('value', chairsList[i]);
                    $("#personResponsible" + (i + 1).toString()).prop("readonly", true);
                    var toRemoveIndex = chairsListIndexes.indexOf(i + 1);
                    chairsListIndexes.splice(toRemoveIndex, 1);
                }
                $("#start-date").attr('value', localStorage.getItem("conferenceStartDate"));
                $("#end-date").attr('value', localStorage.getItem("conferenceEndDate"));
                $("#location").attr('value', localStorage.getItem("conferenceLocation"));
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


    function addConference(conferenceData, chairUser, phasesData) {
        $.ajax({
            url: "http://localhost:8080/api/conferences",
            type: "POST",
            data: JSON.stringify(conferenceData),
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            contentType:'application/json',
            success: function (data) {
                localStorage.setItem("conferenceId", data.id.toString());
                phasesData.forEach(addPhase);
                chairUser.forEach(addChair);
                localStorage.setItem("addedPcMembersForConference" +  data.id.toString(), "false");
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

    function updateConference(conferenceData, chairUser, phasesData) {
        $.ajax({
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId"),
            type: "PUT",
            data: JSON.stringify(conferenceData),
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            contentType:'application/json',
            success: function () {
                phasesData.forEach(updatePhase);
                chairUser.forEach(addChair);
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

    function addChair (value) {
        console.log("In add chair");
        console.log(value);
        $.ajax({
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/chairs",
            type: "POST",
            data: JSON.stringify(value),
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            contentType:'application/json',
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


function removeChair (value) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/chairs?username=" + value,
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

function getPhases() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/phases",
        type: "GET",
        success: function (data) {
            console.log(data);
            for (var i = 0; i < data.length; ++i) {
                phases[data[i].name] = data[i].id;
                $("#" + data[i].name.toLocaleLowerCase()).attr("value", data[i].deadlineString.substr(0, 10));
                console.log(phases);
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

function addPhase(phaseData) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId")  + "/phases",
        type: "POST",
        data: JSON.stringify(phaseData),
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        contentType:'application/json',
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

function updatePhase(phaseData) {
    console.log(phases);
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId")  + "/phases/" + phases[phaseData.name],
        type: "PUT",
        data: JSON.stringify(phaseData),
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        contentType:'application/json',
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


    $("#confAddForm").submit(function (event) {
        event.preventDefault();
        console.log()
        var $form = $(this);
        var formData = {
            name: $form.find('input[name="name"]').val(),
            startDate: $form.find('input[name="start-date"]').val().toString() + "//00::00::00",
            endDate: $form.find('input[name="end-date"]').val().toString() + "//00::00::00",
            city: $form.find('input[name="location"]').val()
        };
        var i = 1;
        var chairs = [];
        for (i = 0; i < chairsListIndexes.length; i++)
        {
            chairs.push({
                user: {
                    username: $form.find('input[name="personResponsible'+ chairsListIndexes[i] +'"]').val()
                }
            })
        }
        var phasesData = [
            {"name":"REGISTRATION", "deadlineString":$form.find('input[name="registration"]').val().toString() + "//00::00::00", "isActive":true},
            {"name":"SESSION_REGISTRATION", "deadlineString":$form.find('input[name="session_registration"]').val().toString() + "//00::00::00", "isActive":false},
            {"name":"BIDDING", "deadlineString":$form.find('input[name="bidding"]').val().toString() + "//00::00::00", "isActive":false},
            {"name":"REVIEWING", "deadlineString":$form.find('input[name="reviewing"]').val().toString() + "//00::00::00", "isActive":false},
            {"name":"SUBMITTING", "deadlineString":$form.find('input[name="submitting"]').val().toString() + "//00::00::00", "isActive":false},
        ]

        var phasesDataUpdate = [
            {"name":"REGISTRATION", "deadlineString":$form.find('input[name="registration"]').val().toString() + "//00::00::00"},
            {"name":"SESSION_REGISTRATION", "deadlineString":$form.find('input[name="session_registration"]').val().toString() + "//00::00::00"},
            {"name":"BIDDING", "deadlineString":$form.find('input[name="bidding"]').val().toString() + "//00::00::00"},
            {"name":"REVIEWING", "deadlineString":$form.find('input[name="reviewing"]').val().toString() + "//00::00::00"},
            {"name":"SUBMITTING", "deadlineString":$form.find('input[name="submitting"]').val().toString() + "//00::00::00"},
        ]

        if (localStorage.getItem("action") === "add"){
            addConference(formData, chairs, phasesData);
        }
        else {
            updateConference(formData, chairs, phasesDataUpdate);
        }
    });




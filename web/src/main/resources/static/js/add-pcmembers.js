
var TOKEN_KEY = "jwtToken"

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}


var noOfPcmembers;
var PcmembersListIndexes = [];
var PcmembersList = [];
var usersAutocompleteList = [];
$(document).ready(function (){
    noOfPcmembers = 1;
    localStorage.setItem("noOfPcmembers", noOfPcmembers.toString());
    PcmembersListIndexes.push(1);
    getPcMembers();
    getUsersAutocomplete();
});

function addAutocomplete() {
    autocomplete(document.getElementById("pcmember" + noOfPcmembers), usersAutocompleteList);
}

function addLabel() {
    console.log(noOfPcmembers);
    noOfPcmembers += 1;
    $("#pcmember-labels").append(
        "<span id=\"group"+ noOfPcmembers +"\">" +
        "<div class=\"one_half first autocomplete\">\n" +
        "              <input type=\"text\" autocomplete='off' name=\"pcmember"+ noOfPcmembers + "\" id=\"pcmember"+ noOfPcmembers + "\" value=\"\" size=\"22\" onclick='addAutocomplete()' required>\n" +
        "            </div>" +
        "<div class=\"one_quarter last\">\n" +
        "              <button type=\"button\" class=\"btn\" id=\"remove-pcmember-button"+ noOfPcmembers + "\" name=\"remove-pcmember-button"+ noOfPcmembers + "\" onclick=\"removeLabel("+ noOfPcmembers + ")\">-</button>\n" +
        "            </div>" +
        "</span>"
    );
    console.log(noOfPcmembers);
    PcmembersListIndexes.push(noOfPcmembers);
    localStorage.setItem("noOfPcmembers", noOfPcmembers.toString());
}

function removeLabel(Pcmembers) {
    //noOfPcmembers -= 1;
    var inputValue = $("#pcmembersAddForm").find('input[name="pcmember' + Pcmembers + '"]').val();
    var ind = PcmembersList.indexOf(inputValue);
    if (ind !== -1) {
        PcmembersList.splice(ind, 1);
    }

    $("#group" + Pcmembers).remove();
    const index = PcmembersListIndexes.indexOf(Pcmembers);
    PcmembersListIndexes.splice(index, 1);
}

function getUsersAutocomplete() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/filterNotPCMember",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            usersAutocompleteList = data.map(elem => {
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

function addPcMember(value) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/pcmembers",
        type: "POST",
        data: JSON.stringify(value),
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        contentType:'application/json',
        success: function () {
            localStorage.setItem("addedPcMembersForConference" +  localStorage.getItem("conferenceId"), "true");
            $(location).attr('href', '../pages/conf-page.html');
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

function getPcMembers() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/pcmembers",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function(data) {
            data.forEach(member => {
                $("#pcList").append("<li>" + member.user.username + " - " + member.user.name + "</li>");
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

$("#pcmembersAddForm").submit(function (event) {
    event.preventDefault();

    let $form = $(this);

    let Pcmembers = [];
    for (let i = 0; i < PcmembersListIndexes.length; i++) {
        Pcmembers.push(
            {user: {
                    username: $form.find('input[name="pcmember'+ PcmembersListIndexes[i] +'"]').val()
                }});
    }

    Pcmembers.forEach(addPcMember);
});


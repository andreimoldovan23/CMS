var TOKEN_KEY = "jwtToken";

function getJwtToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function popUp() {
    var modal = document.getElementById("paper-pop");

    // Get the button that opens the modal
    var btn = document.getElementById("trigg-confirm");

    // Get the <span> element that closes the modal
    var span = document.getElementById("close");

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


function uploadPaperFile() {
    let file = $("#paperFile");
    if (file.val()) {
        let fd = new FormData();
        fd.append("paperFile", file[0].files[0]);
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
                "/papers/" + localStorage.getItem("papersId") + "/file",
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            data: fd,
            contentType: false,
            processData: false,
            cache: false,
            success: uploadPresentationFile,
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
    } else {
        uploadPresentationFile();
    }
}

function uploadPresentationFile() {
    let prez = $("#presentationFile");
    if (prez.val()) {
        let fd = new FormData();
        fd.append("presentationFile", prez[0].files[0]);
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
                "/papers/" + localStorage.getItem("papersId") + "/presentationFile",
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            contentType: false,
            data: fd,
            processData: false,
            cache: false,
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
        })
    } else {
        popUp();
    }
}

function isAddForm() {
    return localStorage.getItem("actionPap") === "add";
}

function addPaper(paperData, authors) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers",
        type: "POST",
        data: JSON.stringify(paperData),
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        contentType: 'application/json',
        success: function (data) {
            localStorage.setItem("papersId", data.id);
            uploadPaperFile();
            authors.forEach(addAuthorToPaper);
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

function updatePaper(paperData) {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/papers/" +
            localStorage.getItem("papersId"),
        type: "PUT",
        data: JSON.stringify(paperData),
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        contentType: 'application/json',
        success: function () {
            uploadPaperFile();
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

$("#paperAddForm").submit(function (event) {
    event.preventDefault();

    let $form = $(this);

    let authors = [];
    for (let i = 0; i < authorsListIndexes.length; i++) {
        let username = $form.find('input[name="author'+ authorsListIndexes[i] +'"]').val();
        if (username) {
            authors.push({
                user: {
                    username: username
                }
            })
        }
    }

    let formData = {
        name: $form.find('input[name="name"]').val(),
        topics: $("#topics").val().split(","),
        keywords: $("#keywords").val().split(",")
    };

    if (localStorage.getItem("actionPap") === "add") {
        addPaper(formData, authors);
    } else {
        updatePaper(formData);
    }
});


//-------------------------------------------------------------------
var noOfAuthors;
var authorsListIndexes = [];
var authorsAutocompleteList = [];
$(document).ready(function () {
    if (!isAddForm()) {
        $("#author1Div").hide();
    } else {
        noOfAuthors = 1;
        localStorage.setItem("noOfAuthors", noOfAuthors.toString());
        authorsListIndexes.push(1);
    }

    if (!isAddForm()) {
        $.ajax({
            url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") +
                "/papers/" + localStorage.getItem("papersId"),
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            type: "GET",
            success: function (data) {
                $("#paperAddForm").find('input[name="name"]').val(data.name);
                $("#topics").val(data.topics.join(", "));
                $("#keywords").val(data.keywords.join(", "));
            },
            error: function (jqXHR) {
                if (jqXHR.status === 403) {
                    $("#paperName").text("FORBIDDEN");
                }
            }
        });
    }

    getAuthorsAutocomplete();
});

function addLabel() {
    console.log(noOfAuthors);
    noOfAuthors += 1;
    $("#author-labels").append(
    "<span id=\"group"+ noOfAuthors +"\">" +
    "\n" +
    "              <div class=\"two_third autocomplete\">\n" +
    "\n" +
    "          <input type=\"text\" autocomplete='off' name=\"author" + noOfAuthors + "\" id=\"author" + noOfAuthors + "\" value=\"\" size=\"22\" onclick='addAutocompletePaperAuthors()'>\n" +
    "\n" +
    "            </div>\n" +
    "\n" +
    "            <div class=\"one_quarter last\">\n" +
    "              <button type=\"button\" class=\"btn\" id=\"remove-author-button" + noOfAuthors + "\" name=\"remove-author-button" + noOfAuthors + "\" onclick=\"removeLabel(" + noOfAuthors +")\">-</button>\n" +
    "            </div></span>"
    );
    console.log(noOfAuthors);
    authorsListIndexes.push(noOfAuthors);
    localStorage.setItem("noOfAuthors", noOfAuthors.toString());
}

function removeLabel(authors) {
    $("#group" + authors).remove();
    const index = authorsListIndexes.indexOf(authors);
    authorsListIndexes.splice(index, 1);
}

function addAutocompletePaperAuthors() {
    autocomplete(document.getElementById("author" + noOfAuthors), authorsAutocompleteList);
}

function getAuthorsAutocomplete() {
    $.ajax({
        url: "http://localhost:8080/api/conferences/" + localStorage.getItem("conferenceId") + "/allAuthors",
        type: "GET",
        headers: {
            "Authorization": "Bearer " + getJwtToken()
        },
        success: function (data) {
            authorsAutocompleteList = data.map(elem => elem.user.username);
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

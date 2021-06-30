// Get the modal
var modal = document.getElementById("ses-pop");

// Get the button that opens the modal
var btn = document.getElementById("trigg-confirm");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close");

// When the user clicks on the button, open the modal
btn.onclick = function() {
    modal.style.display = "block";
}

span.onclick = function() {
    modal.style.display = "none";
    window.location.replace("conf-page.html");
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target === modal) {
        modal.style.display = "none";
        window.location.replace("conf-page.html");
    }
}

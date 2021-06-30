span.onclick = function() {
    modal.style.display = "none";
    window.location.replace("conf-page.html");
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
        window.location.replace("conf-page.html");
    }
}
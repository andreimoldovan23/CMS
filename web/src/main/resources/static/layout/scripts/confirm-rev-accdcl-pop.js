// Get the modal
var modalacc = document.getElementById("rev-acc-pop");
var modaldcl = document.getElementById("rev-dcl-pop");

// Get the button that opens the modal
var btnacc = document.getElementsByClassName("trigg-acc-confirm");
var btndcl = document.getElementsByClassName("trigg-dcl-confirm");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks on the button, open the modal
var i;
for(i in btnacc){
    btnacc[i].onclick = function() {
        modalacc.style.display = "block";
    }
}
var dclopen = false;
var j;
for(j in btndcl){
    btndcl[j].onclick = function() {
        modaldcl.style.display = "block";
        dclopen = true;
    }
}

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    if(modalacc.style.display == "block"){
        modalacc.style.display = "none";
        window.location.replace("view-paper.html");
    }
    if(dclopen == true){
        alert("ok");
        modaldcl.style.display = "none";
        window.location.replace("paper-list.html");
    }
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modalacc) {
        modalacc.style.display = "none";
        window.location.replace("view-paper.html");
    }
    if (event.target == modaldcl) {
        modaldcl.style.display = "none";
        window.location.replace("paper-list.html");
    }
}

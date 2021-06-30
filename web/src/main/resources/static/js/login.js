$(function () {
    // VARIABLES =============================================================
    let TOKEN_KEY = "jwtToken";

   // FUNCTIONS =============================================================
   function getJwtToken() {
      return localStorage.getItem(TOKEN_KEY);
   }

   function setJwtToken(token) {
      localStorage.setItem(TOKEN_KEY, token);
   }

   function removeJwtToken() {
      localStorage.removeItem(TOKEN_KEY);
   }

    function getUserType() {
        localStorage.setItem("USER", false);
        localStorage.setItem("ADMIN", false);
        $.ajax({ url: "http://localhost:8080/api/user/type",
            type: "GET",
            headers: {
                "Authorization": "Bearer " + getJwtToken()
            },
            success: function (data) {
                const roles = data.roles;
                roles.forEach(role => {localStorage.setItem(role, "true")});
                $(location).attr('href', '../pages/home.html');
            },
            error: function (jqXhr) {
                if (jqXhr.status === 401) {
                    alert("Unauthorized");
                } else {
                    alert(jqXhr.responseText);
                }
            }
        });
    }

    function doLogout() {
        removeJwtToken();
        $(location).attr('href', '../pages/home.html');
    }

    function doLogin(loginData) {

          $.ajax({
             url: "http://localhost:8080/api/authenticate",
             type: "POST",
             data: JSON.stringify(loginData),
             contentType: "application/json; charset=utf-8",
             dataType: "json",
             success: function (data) {
                  setJwtToken(data.idToken);
                  getUserType();
             },
             error: function (jqXHR) {
                if (jqXHR.status === 401) {
                    alert("Invalid credentials")
                } else {
                    alert(jqXHR.responseText);
                }
             }
          });
       }

    $("#loginForm").submit(function (event) {

          event.preventDefault();

          var $form = $(this);
          var formData = {
             username: $form.find('input[name="username"]').val(),
             password: $form.find('input[name="password"]').val()
          };

          doLogin(formData);
       });
})






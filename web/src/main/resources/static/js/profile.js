



    var TOKEN_KEY = "jwtToken"

    function removeJwtToken() {
        localStorage.removeItem(TOKEN_KEY);
    }

    function doLogout() {
        removeJwtToken();
        $(location).attr('href', '../pages/home.html');
    }

    function getJwtToken() {
          return localStorage.getItem(TOKEN_KEY);
       }

       $(document).ready(profileSetup());



    function profileSetup () {
        $.ajax({

                         url: "http://localhost:8080/api/user",
                         type: "GET",
                         headers: {
                            "Authorization": "Bearer " + getJwtToken()
                         },
                         success: function (data) {
                             $("#userNameProfile").text(data.username);
                             $("#nameProfile").text(data.name);
                             $("#affiliationProfile").text(data.job);
                             $("#emailProfile").text(data.mail);
                             $("#phoneProfile").text(data.phoneNumber);
                         },
                         error: function () {
                                doLogout();
                         }
                      });
    }



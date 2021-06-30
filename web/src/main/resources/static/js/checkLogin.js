
    // VARIABLES =============================================================
       var TOKEN_KEY = "jwtToken";
       var $logInNav = $("#logInNav");
       var $signUpNav = $("#signUpNav");
       var $userNav = $("#userNav");
       var $logOutNav = $("#logOutNav");


   // FUNCTIONS =============================================================
   function getJwtToken() {
      return localStorage.getItem(TOKEN_KEY);
   }

   function isLoggedIn() {
      return getJwtToken() != null;
   }

    $(document).ready(function() {
        if (isLoggedIn())
        {
            $logInNav.hide();
            $signUpNav.hide();
            $userNav.show();
            $logOutNav.show();
        }
        else
        {
            $logInNav.show();
            $signUpNav.show();
            $userNav.hide();
            $logOutNav.hide();
        }
    });

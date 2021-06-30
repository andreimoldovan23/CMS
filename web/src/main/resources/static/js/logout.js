

   // VARIABLES =============================================================
   var TOKEN_KEY = "jwtToken";
   var $logOutNav = $("#logOutNav");

   // FUNCTIONS =============================================================
   function getJwtToken() {
      return localStorage.getItem(TOKEN_KEY);
   }


   function removeJwtToken() {
      localStorage.removeItem(TOKEN_KEY);
   }

   function doLogout() {
      removeJwtToken();
      $(location).attr('href', '../pages/home.html');
   }

   $logOutNav.click( function () {
        localStorage.setItem("ADMIN", "false");
        localStorage.setItem("USER", "false");
        doLogout();
   }
   );



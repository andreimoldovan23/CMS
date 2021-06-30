$(function () {


function doSignUp(signupData) {
      $.ajax({
         url: "http://localhost:8080/api/user/new",
         type: "POST",
         data: JSON.stringify(signupData),
         contentType: "application/json; charset=utf-8",
         success: function () {
             $(location).attr('href', '../pages/account-valid.html');
         },
         error: function (jqXHR) {
                alert(jqXHR.responseText);
            }
      });
   }

$("#signUpForm").submit(function (event) {
      event.preventDefault();

      var $form = $(this);
      var formData = {
         username: $form.find('input[name="username"]').val(),
         password: $form.find('input[name="password"]').val(),
         name: $form.find('input[name="name"]').val(),
         job: $form.find('input[name="affiliation"]').val(),
         mail: $form.find('input[name="email"]').val(),
         phoneNumber: $form.find('input[name="tel"]').val()
      };

      doSignUp(formData);
   });


})

function handleLoginCall() {
  document.getElementById("messages").innerText = "";
  document.getElementsByClassName("lds-ring")[0].style.display = "flex";
  document.getElementById("login").setAttribute("disabled", "");
  var username = document.getElementById("username");
  var password = document.getElementById("password");
  var xhr = new XMLHttpRequest();

  xhr.open("POST", window.location.origin + "/api/login");

  var data = {
    username: username.value,
    password: password.value,
  };

  xhr.onreadystatechange = (request, event) => {
    if (request.target.readyState == 4) {
      try {
        var json_data = JSON.parse(request.target.responseText);
        if (json_data["success"]) {
          console.log("Success: " + json_data["2faRequired"]);
          //let code = prompt("Please input your Multi-factor Code from your phone.", "");
          document.getElementById("pop-up").style.display = "block";
          document.getElementById("pop-up-content").style.display = "block";
        } else {
          document.getElementById("messages").innerText = json_data["message"];
        }
        validateLogin()
        document.getElementsByClassName("lds-ring")[0].style.display = "none";
      } catch (SyntaxError) {
        document.getElementById("messages").innerText =
          "Server responded with an invalid response. Please try again later.";
          validateLogin()
        document.getElementsByClassName("lds-ring")[0].style.display = "none";
      }
    }
  };
  xhr.send(JSON.stringify(data));
}

document.getElementById("twofasubmit").onclick = (event) => {
  event.preventDefault();
  var code =
    document.getElementById("twofainput-1").value +
    document.getElementById("twofainput-2").value +
    document.getElementById("twofainput-3").value +
    document.getElementById("twofainput-4").value +
    document.getElementById("twofainput-5").value +
    document.getElementById("twofainput-6").value;

  var mfa = new XMLHttpRequest();
  mfa.open("POST", window.location.origin + "/api/login/mfa");
  var data = {
    code: code,
  };
  mfa.onreadystatechange = (request, event) => {
    if (request.target.readyState == 4) {
      try {
        var json_data = JSON.parse(request.target.responseText);
        if (json_data["success"]) {
          console.log("Success: ");
          console.log(json_data);
          window.location.replace(window.location.origin + "/dashboard.html");
        } else {
            if(json_data["retry"] == null || !json_data["retry"]) {
                document.getElementById("pop-up").style.display = "none";
                document.getElementById("pop-up-content").style.display = "none";
                document.getElementById("messages").innerText = json_data["message"];
            } else if(json_data["retry"]) {
                document.getElementById("twofa-messages").innerText = json_data["message"];
            }
        }
      } catch (SyntaxError) {
        document.getElementById("twofa-messages").innerText =
          "Server responded with an invalid response. Please try again later.";
      }
    }
  };
  mfa.send(JSON.stringify(data));
};

document.getElementById("login").onclick = (event) => {
  event.preventDefault();
  handleLoginCall();
};

function validateLogin() {
  var ele = document.getElementById("login-form").elements;
  var valid = true;

  for (var i = 0; i < ele.length; i++) {
    if (ele[i].value.length === 0) valid = false;
  }

  if (valid) {
    document.getElementById("login").removeAttribute("disabled");
    return;
  }
  document.getElementById("login").setAttribute("disabled", "");
}

function handleLoginCall() {
    var username = document.getElementById("username");
    var password = document.getElementById("password");
    var xhr = new XMLHttpRequest();

    xhr.open("POST", window.location.origin + "/api/login");
    
    var data = {
        "username": username.value,
        "password": password.value
    };
    
    xhr.onreadystatechange = (request, event) => {
        if(request.target.readyState == 4) {
            var json_data = JSON.parse(request.target.responseText);
            if(json_data['success']) {
                console.log("Success: " + json_data["2faRequired"]);
                //let code = prompt("Please input your Multi-factor Code from your phone.", "");
                document.getElementById("pop-up").style.display = "block";
                document.getElementById("pop-up-content").style.display = "block";
            } else {
                console.log("Error: " + json_data["message"]);
            }
        }
    }
    xhr.send(JSON.stringify(data));
}

document.getElementById("twofasubmit").onclick = (event) => {
    event.preventDefault();
    var code = document.getElementById("twofainput-1").value +
    document.getElementById("twofainput-2").value + 
    document.getElementById("twofainput-3").value +
    document.getElementById("twofainput-4").value +
    document.getElementById("twofainput-5").value +
    document.getElementById("twofainput-6").value;

    var mfa = new XMLHttpRequest();
    mfa.open("POST", window.location.origin + "/api/login/mfa");
    var data = {
        "code": code
    };
    mfa.onreadystatechange = (request, event) => {
        if(request.target.readyState == 4) {
            var json_data = JSON.parse(request.target.responseText);
            if(json_data['success']) {
                console.log("Success: ");
                console.log(json_data);
                window.location.replace(window.location.origin + "/dashboard.html");
            }
        }
    };
    mfa.send(JSON.stringify(data));
}

document.getElementById("login").onclick = (event) => {
    event.preventDefault();
    handleLoginCall();
};

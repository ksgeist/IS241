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
                let code = prompt("Please input your Multi-factor Code from your phone.", "");
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
            } else {
                console.log("Error: " + json_data["message"]);
            }
        }
    }
    xhr.send(JSON.stringify(data));
}

document.getElementById("login").onclick = (event) => {
    event.preventDefault();
    handleLoginCall();
};

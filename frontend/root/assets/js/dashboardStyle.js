window.onload = function () {

    console.log();
  };

// TEMPORARY dummy hrefs TEMPORARY 
var dashboardArray = document.getElementsByClassName("dashboard");
for(const dash of dashboardArray ){
    //let l = 1/(100/ dashboardArray.length + 1);
    for(const child of dash.children){
        
        child.setAttribute('href', "dummy.html")
        //child.setAttribute("style", `width:f{}`)
    }
}



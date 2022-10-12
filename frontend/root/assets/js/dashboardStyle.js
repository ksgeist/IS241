var dashboardArray = document.getElementsByClassName("dashboard");
window.onload = function () {

    //console.log(dashboardArray.length);
    //console.log(dashboardArray.children)
  };

function resize(){
    for(const dash of dashboardArray ){
        console.log(dash.children.length);
        let l = (100/ (dash.children.length ));
        //console.log(dash.length);
        //console.log(l)
        let counter = 1
        for(const child of dash.children){
            // TEMPORARY dummy hrefs TEMPORARY 
            child.setAttribute('href', "dummy.html")

            child.setAttribute("style", `margin-left:${l*counter}%`)
            counter++;

        }
    }

};

resize(); 
window.onresize = resize();

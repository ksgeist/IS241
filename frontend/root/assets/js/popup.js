/*
	Missouri COVID-19 Vaccine Tracker Application 
	Filename: 	popup.js
	
	Written by Justin Macapanan

*/ 

window.onload = function(){
    console.log("hi")
}

const submitButton = document.getElementById("login");
const exit = document.getElementById("exit");

submitButton.onclick = function(){
    document.getElementById("pop-up").style.display = "block";
}

exit.onclick=function(){
    document.getElementById("pop-up").style.display = "none";
}

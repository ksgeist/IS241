/*
	Missouri COVID-19 Vaccine Tracker Application 
	Filename: 	popup_help.js
	
	Written by Jasmin Milicevic, put into separate file by KSG & updated to work with dashboards

*/
window.onload = function () {
  console.log("logout executed");
};

// Have to add multiple logout variables because multiple instances in dashboard HTML
const logout = document.querySelector(".logout");
const logout2 = document.querySelector(".logout");
const logout3 = document.querySelector(".logout");
const logout4 = document.querySelector(".logout");

function gohome() {
	window.location.replace(window.location.origin + "/api/logout");
} 
logout.onclick = gohome;
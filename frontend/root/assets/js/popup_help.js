/*
	Missouri COVID-19 Vaccine Tracker Application 
	Filename: 	popup_help.js
	
	Written by Jasmin Milicevic, put into separate file by KSG & updated to work with dashboards

*/
window.onload = function () {
  console.log("help popup opens");
};

const modal = document.querySelector(".modal");
const trigger = document.querySelector(".trigger");
const closeButton = document.querySelector(".close-button");


function toggleModal() {
	modal.classList.toggle("show-modal");
	//document.getElementById("pop-up").style.display = (document.getElementById("pop-up").style.display != "block" ? "block" : "none");
}

function windowOnClick(event) {
	if (event.target === modal) {
		toggleModal();
	}
}

trigger.addEventListener("click", toggleModal);
closeButton.addEventListener("click", toggleModal);
window.addEventListener("click", windowOnClick);

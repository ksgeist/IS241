/*
	Missouri COVID-19 Vaccine Tracker Application 
	Filename: 	popup_help.js
	
	Written by Jasmin Milicevic, put into separate file by KSG & updated to work with dashboards

*/
window.onload = function () {
  console.log("help popup opens");
};

const modal = document.querySelector(".modal");

// We have multiple trigger events in order for the modal to work for each instance in the dashboard HTML
const trigger = document.querySelector(".trigger");
const trigger2 = document.querySelector(".trigger2");
const trigger3 = document.querySelector(".trigger3");
const trigger4 = document.querySelector(".trigger4");

const closeButton = document.querySelector(".close-button");


function toggleModal() {
	modal.classList.toggle("show-modal");
	document.getElementById("pop-up").style.display = (document.getElementById("pop-up").style.display != "block" ? "block" : "none");
}

function windowOnClick(event) {
	if (event.target === modal) {
		toggleModal();
	}
}

// We have multiple trigger events in order for the modal to work for each instance in the dashboard HTML
trigger.addEventListener("click", toggleModal);
trigger2.addEventListener("click", toggleModal);
trigger3.addEventListener("click", toggleModal);
trigger4.addEventListener("click", toggleModal);

closeButton.addEventListener("click", toggleModal);
window.addEventListener("click", windowOnClick);

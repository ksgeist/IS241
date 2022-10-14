/*
	Missouri COVID-19 Vaccine Tracker Application 
	Filename: 	popup_24a.js
	
	Written by Justin Macapanan

*/
window.onload = function () {
  console.log("hi");
};

const submitButton = document.getElementById("login");
const exit = document.getElementById("exit");

exit.onclick = function () {
  document.getElementById("pop-up").style.display = "none";
  document.getElementById("pop-up-content").style.display = "none";
};

function handle2FAInput(event) {
  event.value = event.value.replace(/[^0-9.]/g, "").replace(/(\..*)\./g, "$1");
  if (Number(event.value) != NaN && event.value != "") {
    if (event.nextElementSibling != null) {
      event.nextElementSibling.focus();
    }
  } else if (event.value == "") {
    if (event.previousElementSibling != null) {
      event.previousElementSibling.focus();
    }
  }
}

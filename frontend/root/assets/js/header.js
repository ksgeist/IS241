/*
	Header JavaScript
  	Filename: header.js
  
	Author: Katherine Geist
  	Date: 19 Sept 2022
*/

// Create a function that contains the HTML to inject 
function addHeader(data) {
    return `<div class="header">
				<img src="assets/images/logo-banner.png" alt="Missouri DHSS Logo: COVID-19 Vaccine Tracker, Authorized users only"/>
				<a href="#help">Help</a>
				<a href="#logout">Logout</a>
			</div>
    `
}

var data = {
     header: 'Missouri COVID-19 Vaccine Tracker',
}

const addHeader = addHeaderTemplate(data);
document.getElementById('myRandomElement').insertAdjacentHTML("afterbegin", addHeader);
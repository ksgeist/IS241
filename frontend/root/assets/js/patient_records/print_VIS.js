"use strict";
/*
	Missouri COVID-19 Vaccine Tracker Application 
	Filename: 	print_VIS.js
	
	Written by Katherine Geist, to print the appropriate Vaccine Info Sheet

*/

window.onload = function() {
  console.log("prints VIS");
};


	const dose = document.querySelector("#dose"); 
	const printButton = document.querySelector(".print-button");
	var url = "";
	var vaxMan = document.getElementsByName('vaxMan');
	var lang = document.getElementsByName('lang');

	function getFromRadio(radios) {
		val = "english"
		// 	Get the manufacturer selected from the radio buttons
		for (var i = 0, length = radios.length; i < length; i++) {
		  if (radios[i].checked) {
			// 	Set the manufacturer flag
			val = radios[i].value;
			// Only one radio can be logically checked, so don't check the rest
			break;
		  }
		}	
		return val;
	}
	
	function getOptions(event) {
		event.preventDefault();
		// 	Get the vaccine dose selected from the dropdown
		vax = dose.options[dose.selectedIndex].value;
// 		document.querySelector('.vax').textContent = vax;
		
		manu = getFromRadio(vaxMan);
// 		document.querySelector('.manu').textContent = manu;
		
		language = getFromRadio(lang);
// 		document.querySelector('.language').textContent = language;

		// 	Pass to the fx where we actually open the VIS based on specs provided
		openVIS(lang, vax, manu);		
	}

	function openVIS(lang, vax, manu) {
		
		if(manu == "novavax") {
			if(language == "english") {
				if(vax == "adDose1" | vax == "teenDose1") {
					url = "https://www.fda.gov/media/159898/download";
				} else {
					window.alert("This vaccine dose is not available for this manufacturer and/or age group.")
					url = "";
				}	
			}
			else if(language == "spanish") {
				if(vax == "adDose1" | vax == "teenDose1") {
					url = "https://www.fda.gov/media/160242/download";
				} else {
					window.alert("This vaccine dose is not available for this manufacturer and/or age group.")
					url = "";
				}				
			}
		}
		
		if(manu == "janssen") {
			if(language == "english") {
				if(vax == "adDose1" | vax == "adDose2" | "adBoosterBiv") {
					url = "https://www.janssenlabels.com/emergency-use-authorization/Janssen+COVID-19+Vaccine-Recipient-fact-sheet.pdf";
				} else {
					window.alert("This vaccine dose is not available for this manufacturer and/or age group.")
					url = "";
				}	
			}
			else if(language == "spanish") {
				if(vax == "adDose1" | vax == "adDose2" | "adBoosterBiv") {
					url = "https://www.janssenlabels.com/emergency-use-authorization/Janssen+COVID-19+Vaccine-Recipient-fact-sheet-es.pdf";
				} else {
					window.alert("This vaccine dose is not available for this manufacturer and/or age group.")
					url = "";
				}				
			}
		}
		
		if(manu == "pfizer") {
			if(language == "english") {
				if(vax == "adDose1" | vax == "adDose2" | vax == "adBooster1" | vax == "adBooster2" | vax == "adBoosterBiv" | vax == "teenDose1" | vax == "teenDose2" | vax == "teenBooster1"  | vax == "teenBooster2" | vax == "teenBoosterBiv") {
					url = "https://labeling.pfizer.com/ShowLabeling.aspx?id=14472&format=pdf";
				} else if(vax == "chDose1" | vax == "chDose2" | vax == "chBooster1" | vax == "chBoosterBiv") {
					url = "https://labeling.pfizer.com/ShowLabeling.aspx?id=16074&format=pdf";
				} else if(vax == "infDose1" | vax == "infDose2" | vax == "infBooster1") {
					url = "https://labeling.pfizer.com/ShowLabeling.aspx?id=17228&format=pdf";
				} else {
					url = "";
				}
			}
		else if(language == "spanish") {
				if(vax == "adDose1" | vax == "adDose2" | vax == "adBooster1" | vax == "adBooster2" | vax == "adBoosterBiv" | vax == "teenDose1" | vax == "teenDose2" | vax == "teenBooster1" | vax == "teenBooster2" | vax == "teenBoosterBiv") {
					url = "https://webfiles.pfizer.com/spanisheuarecipientfs12yrsandup";
				} else if(vax == "chDose1" | vax == "chDose2" | vax == "chBooster1" | vax == "chBoosterBiv") {
					url = "https://webfiles.pfizer.com/spanisheuarecipientfs5through11years";
				} else if(vax == "infDose1" | vax == "infDose2" | vax == "infBooster1") {
					url = "https://webfiles.pfizer.com/spanisheuarecipientfs6mo4yr";
				} else {
					url = "";
				}
			}
		}
			
		if(manu == "moderna") {
			if(language == "english") {
				if(vax == "adDose1" | vax == "adDose2" | vax == "adBooster1" | vax == "adBooster2" | vax == "adBoosterBiv" | vax == "teenDose1" | vax == "teenDose2" | vax == "teenBooster1"  | vax == "teenBooster2" | vax == "teenBoosterBiv") {
					url = "https://eua.modernatx.com/covid19vaccine-eua/bivalent-dose-recipient.pdf";
				} else if(vax == "chDose1" | vax == "chDose2" | vax == "chBooster1" | vax == "chBoosterBiv") {
					url = "https://eua.modernatx.com/covid19vaccine-eua/6-11y-facts-recipient.pdf";
				} else if(vax == "infDose1" | vax == "infDose2" | vax == "infBooster1") {
					url = "https://eua.modernatx.com/covid19vaccine-eua/6m-5y-facts-recipient.pdf";
				} else {
					url = "";
				}
			}
		else if(language == "spanish") {
				if(vax == "adDose1" | vax == "adDose2" | vax == "adBooster1" | vax == "adBooster2" | vax == "adBoosterBiv" | vax == "teenDose1" | vax == "teenDose2" | vax == "teenBooster1"  | vax == "teenBooster2" | vax == "teenBoosterBiv") {
					url = "https://eua.modernatx.com/covid19vaccine-eua/EUAFactSheet-Recipients-Prime-Bivalent-Boost-12y+-Highlighted8.31.22_es-ES_Clean.pdf";
				} else if(vax == "chDose1" | vax == "chDose2" | vax == "chBooster1" | vax == "chBoosterBiv") {
					url = "https://eua.modernatx.com/covid19vaccine-eua/6-11y-facts-recipient_es-ES.pdf";
				} else if(vax == "infDose1" | vax == "infDose2" | vax == "infBooster1") {
					url = "https://eua.modernatx.com/covid19vaccine-eua/6m-5y-facts-recipient_es-ES.pdf";
				} else {
					url = "";
				}
			}		
		}
		
		// 	And then we don't want to open an empty webpage:
		if (url !== "") {
			window.open(url);	
		}
	}

	printButton.addEventListener("click", getOptions);

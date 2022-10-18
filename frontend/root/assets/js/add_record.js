var currentDate = new Date();
currentDate.setMilliseconds(0);
document.getElementById("date-input").value = currentDate.toISOString().slice(0, -1);
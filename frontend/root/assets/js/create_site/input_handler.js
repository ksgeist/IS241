const counties = [
  { fips: 29001, name: "Adair County" },
  { fips: 29003, name: "Andrew County" },
  { fips: 29005, name: "Atchison County" },
  { fips: 29007, name: "Audrain County" },
  { fips: 29009, name: "Barry County" },
  { fips: 29011, name: "Barton County" },
  { fips: 29013, name: "Bates County" },
  { fips: 29015, name: "Benton County" },
  { fips: 29017, name: "Bollinger County" },
  { fips: 29019, name: "Boone County" },
  { fips: 29021, name: "Buchanan County" },
  { fips: 29023, name: "Butler County" },
  { fips: 29025, name: "Caldwell County" },
  { fips: 29027, name: "Callaway County" },
  { fips: 29029, name: "Camden County" },
  { fips: 29031, name: "Cape Girardeau County" },
  { fips: 29033, name: "Carroll County" },
  { fips: 29035, name: "Carter County" },
  { fips: 29037, name: "Cass County" },
  { fips: 29039, name: "Cedar County" },
  { fips: 29041, name: "Chariton County" },
  { fips: 29043, name: "Christian County" },
  { fips: 29045, name: "Clark County" },
  { fips: 29047, name: "Clay County" },
  { fips: 29049, name: "Clinton County" },
  { fips: 29051, name: "Cole County" },
  { fips: 29053, name: "Cooper County" },
  { fips: 29055, name: "Crawford County" },
  { fips: 29057, name: "Dade County" },
  { fips: 29059, name: "Dallas County" },
  { fips: 29061, name: "Daviess County" },
  { fips: 29063, name: "DeKalb County" },
  { fips: 29065, name: "Dent County" },
  { fips: 29067, name: "Douglas County" },
  { fips: 29069, name: "Dunklin County" },
  { fips: 29071, name: "Franklin County" },
  { fips: 29073, name: "Gasconade County" },
  { fips: 29075, name: "Gentry County" },
  { fips: 29077, name: "Greene County" },
  { fips: 29079, name: "Grundy County" },
  { fips: 29081, name: "Harrison County" },
  { fips: 29083, name: "Henry County" },
  { fips: 29085, name: "Hickory County" },
  { fips: 29087, name: "Holt County" },
  { fips: 29089, name: "Howard County" },
  { fips: 29091, name: "Howell County" },
  { fips: 29093, name: "Iron County" },
  { fips: 29095, name: "Jackson County" },
  { fips: 29097, name: "Jasper County" },
  { fips: 29099, name: "Jefferson County" },
  { fips: 29101, name: "Johnson County" },
  { fips: 29103, name: "Knox County" },
  { fips: 29105, name: "Laclede County" },
  { fips: 29107, name: "Lafayette County" },
  { fips: 29109, name: "Lawrence County" },
  { fips: 29111, name: "Lewis County" },
  { fips: 29113, name: "Lincoln County" },
  { fips: 29115, name: "Linn County" },
  { fips: 29117, name: "Livingston County" },
  { fips: 29119, name: "McDonald County" },
  { fips: 29121, name: "Macon County" },
  { fips: 29123, name: "Madison County" },
  { fips: 29125, name: "Maries County" },
  { fips: 29127, name: "Marion County" },
  { fips: 29129, name: "Mercer County" },
  { fips: 29131, name: "Miller County" },
  { fips: 29133, name: "Mississippi County" },
  { fips: 29135, name: "Moniteau County" },
  { fips: 29137, name: "Monroe County" },
  { fips: 29139, name: "Montgomery County" },
  { fips: 29141, name: "Morgan County" },
  { fips: 29143, name: "New Madrid County" },
  { fips: 29145, name: "Newton County" },
  { fips: 29147, name: "Nodaway County" },
  { fips: 29149, name: "Oregon County" },
  { fips: 29151, name: "Osage County" },
  { fips: 29153, name: "Ozark County" },
  { fips: 29155, name: "Pemiscot County" },
  { fips: 29157, name: "Perry County" },
  { fips: 29159, name: "Pettis County" },
  { fips: 29161, name: "Phelps County" },
  { fips: 29163, name: "Pike County" },
  { fips: 29165, name: "Platte County" },
  { fips: 29167, name: "Polk County" },
  { fips: 29169, name: "Pulaski County" },
  { fips: 29171, name: "Putnam County" },
  { fips: 29173, name: "Ralls County" },
  { fips: 29175, name: "Randolph County" },
  { fips: 29177, name: "Ray County" },
  { fips: 29179, name: "Reynolds County" },
  { fips: 29181, name: "Ripley County" },
  { fips: 29183, name: "St. Charles County" },
  { fips: 29185, name: "St. Clair County" },
  { fips: 29186, name: "Ste. Genevieve County" },
  { fips: 29187, name: "St. Francois County" },
  { fips: 29189, name: "St. Louis County" },
  { fips: 29195, name: "Saline County" },
  { fips: 29197, name: "Schuyler County" },
  { fips: 29199, name: "Scotland County" },
  { fips: 29201, name: "Scott County" },
  { fips: 29203, name: "Shannon County" },
  { fips: 29205, name: "Shelby County" },
  { fips: 29207, name: "Stoddard County" },
  { fips: 29209, name: "Stone County" },
  { fips: 29211, name: "Sullivan County" },
  { fips: 29213, name: "Taney County" },
  { fips: 29215, name: "Texas County" },
  { fips: 29217, name: "Vernon County" },
  { fips: 29219, name: "Warren County" },
  { fips: 29221, name: "Washington County" },
  { fips: 29223, name: "Wayne County" },
  { fips: 29225, name: "Webster County" },
  { fips: 29227, name: "Worth County" },
  { fips: 29229, name: "Wright County" },
  { fips: 29510, name: "St. Louis city" },
];

var list = "";
for (county in counties) {
    list += '<option value="' + counties[county]["name"] + '\">'
    
}
document.getElementById("counties-list").innerHTML = list;

document.getElementById("address-autocomplete").onblur = function () {
  fetch(
    `${window.location.hostname}/lookupAddress?address=${
      document.getElementById("address-autocomplete").value
    }`
  ).then(async (resp) => {
    var info = await resp.json();
    console.log(info);
    if (
      info.hasOwnProperty("addressMatches") &&
      info["addressMatches"].length > 0
    ) {
      var markerLoc = new ol.geom.Point(
        proj4("EPSG:4326", "EPSG:3857", [
          info["addressMatches"][0]["coordinates"]["x"],
          info["addressMatches"][0]["coordinates"]["y"],
        ]),
        "EPSG:3857"
      );
      console.log(markerLoc);
      var marker = new ol.Feature(markerLoc);
      console.log(marker);
      markers.getSource().addFeature(marker);
      map.getView().fit(markerLoc.extent_, map.getSize());
      map.getView().setZoom(17);
    }
  });
};
document.getElementById("county").onchange = function (ele) {
  counties.forEach((county) => {
    if (ele.currentTarget.value == county["name"]) {
      document.getElementById("fips").value = county["fips"];
      return;
    }
  });
};


const isNumericInput = (event) => {
    const key = event.keyCode;
    return ((key >= 48 && key <= 57) || // Allow number line
        (key >= 96 && key <= 105) // Allow number pad
    );
};

const isModifierKey = (event) => {
    const key = event.keyCode;
    return (event.shiftKey === true || key === 35 || key === 36) || // Allow Shift, Home, End
        (key === 8 || key === 9 || key === 13 || key === 46) || // Allow Backspace, Tab, Enter, Delete
        (key > 36 && key < 41) || // Allow left, up, right, down
        (
            // Allow Ctrl/Command + A,C,V,X,Z
            (event.ctrlKey === true || event.metaKey === true) &&
            (key === 65 || key === 67 || key === 86 || key === 88 || key === 90)
        )
};

const enforceFormat = (event) => {
    // Input must be of a valid number format or a modifier key, and not longer than ten digits
    if(!isNumericInput(event) && !isModifierKey(event)){
        event.preventDefault();
    }
};

const formatToPhone = (event) => {
    if(isModifierKey(event)) {return;}

    const input = event.target.value.replace(/\D/g,'').substring(0,10); // First ten digits of input only
    const areaCode = input.substring(0,3);
    const middle = input.substring(3,6);
    const last = input.substring(6,10);

    if(input.length > 6){event.target.value = `(${areaCode}) ${middle} - ${last}`;}
    else if(input.length > 3){event.target.value = `(${areaCode}) ${middle}`;}
    else if(input.length > 0){event.target.value = `(${areaCode}`;}
};

const inputElement = document.getElementById('phoneNumber');
inputElement.addEventListener('keydown',enforceFormat);
inputElement.addEventListener('keyup',formatToPhone);
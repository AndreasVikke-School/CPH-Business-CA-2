import 'bootstrap/dist/css/bootstrap.css'


window.onload = () => {
    document.getElementById("output").append(createTable([{ "firstname": "Andreas", "lastname": "Vikke" },
    { "firstname": "Emil", "lastname": "Svensmark" }]));
    var btnHobby = document.getElementById("hobbyBtn");
    btnHobby.addEventListener("click", getHobby);

    var btnCity = document.getElementById("cityBtn");
    btnCity.addEventListener("click", getCity);

    var btnCount = document.getElementById("countBtn");
    btnCount.addEventListener("click", getHobbyCount);

    var zipcodesBtn = document.getElementById("zipcodesBtn");
    zipcodesBtn.addEventListener("click", testZipcodes);
}


//Hobby button
//Sets .output to a table with all persons with a hobby
function getHobby() {
    var hobby = document.getElementById("inputGroupSelect01").value;
    getFetchData1(hobby);
}

function getFetchData1(hobby) {
    fetch("/CA2/api/Hobbies/" + hobby) //Need correct API when its made.
        .then(res => res.json())
        .then(data => {
            //console.log("data", data);
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").appendChild = createTable(data);
        })
}


//City button
//Sets .output to a table with all persons from a city
function getCity() {
    var city = document.getElementById("inputGroupSelect02").value;
    getFetchData2(city);
}

function getFetchData2(city) {
    fetch("/CA2/api/Cities/" + city) //Need correct API when its made.
        .then(res => res.json())
        .then(data => {
            //console.log("data", data);
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").appendChild = createTable(data);
        })
}


//Hobby Count button
//Sets .output to a number equal to the amount of people from a city
function getHobbyCount() {
    var hobby = document.getElementById("inputGroupSelect03").value;
    getFetchData3(hobby);
}

function getFetchData3(hobby) {
    fetch("/CA2/api/hobby/count/" + hobby) //Need correct API when its made.
        .then(res => res.json())
        .then(data => {
            //console.log("data", data);
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").append(createTable(data)); //needs a better method
        })
}

//Zipcodes button
//
/*function getZipcodes() {
    fetch("https://dawa.aws.dk/postnumre")
        .then(res => res.json())
        .then(data => {
            //console.log("data", data);
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").appendChild = createTable(data);
        })
}*/

function testZipcodes() {
    fetch("https://dawa.aws.dk/postnumre")
        .then(res => res.json())
        .then(data => {
            var zipcodes = [];
            for (var i in data) {
                var zip = data[i].nr;
                var name = data[i].navn;
                var obj = {
                    zipcode: zip,
                    name: name
                };
                zipcodes.push(obj);
            }
            console.log(zipcodes);
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").appendChild(createTable(zipcodes));
        })
}



//Automatic table generator
function createTable(array) {
    if (!Array.isArray(array))
        array = [array];

    var table = document.createElement("table");
    table.classList.add("table");

    var tHead = table.createTHead();
    tHead.classList.add("thead-dark");

    var tRow = tHead.insertRow(0);
    Object.keys(array[0]).map(function (key, index) {
        tRow.insertCell(index).outerHTML = "<th>" + key.charAt(0).toUpperCase() + key.slice(1); + "</th>";
    });

    var tBody = table.createTBody();
    tBody.addEventListener("click", (e) => { tBodyClickHandler(e) });
    array.map(function (obj, index) {
        var tBRow = tBody.insertRow(index);
        Object.keys(obj).map(function (key, index) {
            if (typeof obj[key] === 'object') {
                var objS = [];
                Object.keys(obj[key]).map(k => {
                    if (k != "id")
                        objS.push(obj[key][k])
                });
                tBRow.insertCell(index).innerHTML = objS.join(", ");
            }
            else
                tBRow.insertCell(index).innerHTML = obj[key];

        });
    });

    return table;
}
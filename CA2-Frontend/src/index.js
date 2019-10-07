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
    zipcodesBtn.addEventListener("click", getZipcodes);

    var cmpyListBtn = document.getElementById("cmpyListBtn");
    cmpyListBtn.addEventListener("click", getCompanyList);
}


//Hobby Button
//Sets .output to a table with all persons with a hobby
function getHobby() {
    var hobby = document.getElementById("inputGroupSelect01").value;
    getFetchData1(hobby);
}

function getFetchData1(hobby) {
    fetch("/CA2/api/Hobbies/" + hobby) //Need correct API when its made.
        .then(res => res.json())
        .then(data => {
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").appendChild(createTable(data));
        })
}


//City Button
//Sets .output to a table with all persons from a city
function getCity() {
    var city = document.getElementById("inputGroupSelect02").value;
    getFetchData2(city);
}

function getFetchData2(city) {
    fetch("/CA2/api/Cities/" + city) //Need correct API when its made.
        .then(res => res.json())
        .then(data => {
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").appendChild(createTable(data));
        })
}


//Hobby Count Button
//Sets .output to a number equal to the amount of people from a city
function getHobbyCount() {
    var hobby = document.getElementById("inputGroupSelect03").value;
    getFetchData3(hobby);
}

function getFetchData3(hobby) {
    fetch("/CA2/api/hobby/count/" + hobby) //Need correct API when its made.
        .then(res => res.json())
        .then(data => {
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").appendChild(createTable(data)); //needs a better method
        })
}


//Zipcode Button
//Sets .output to have a div with an overflow class, then sets that div
//to a table of all zipcodes in denmark. Zipcodes is from API dawa.aws.dk/postnumre.
function getZipcodes() {
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
            document.getElementById("output").innerHTML = "<div id=\"output2\" class=\"table-wrapper-scroll-y my-custom-scrollbar\"></div >";
            document.getElementById("output2").appendChild(createTable(zipcodes));
        })
}


//Automatic Table Generator
function createTable(array) {
    if (!Array.isArray(array)) {
        array = [array];
    }

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


//Company List Button
//Sets .output to a table of companies with more than a given amount of employees
function getCompanyList() {
    var amount = document.getElementById("companyAmountInput").value; //Har brug for fejlhåndtering
    getFetchData4(amount);
}

function getFetchData4(amount) {
    fetch("/CA2/api") //Need correct API when its made.
        .then(res => res.json())
        .then(data => {
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").appendChild(createTable(data)); //needs a better method
        })
}
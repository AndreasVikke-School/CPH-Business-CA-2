import 'bootstrap/dist/css/bootstrap.css'


window.onload = () => {
    document.getElementById("output").append(createTable([{ "firstname": "Andreas", "lastname": "Vikke" },
    { "firstname": "Emil", "lastname": "Svensmark" }]));

    loadHobbyOptions1();
    loadHobbyOptions2();
    loadCityOptions();

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

    var hobbyButton = document.getElementById("hobbyBtnCol");
    hobbyButton.addEventListener("click", addHobbyColoumn);

    var phoneButton = document.getElementById("phoneBtn");
    phoneButton.addEventListener("click", addPhoneColoumn);
}


//Hobby Button
//Sets .output to a table with all persons with a hobby
function getHobby() {
    var hobbyid = document.getElementById("inputGroupHobby1").value;
    getFetchData1(hobbyid);
}

function getFetchData1(hobbyid) {
    fetch("http://localhost:8080/ca2/api/person/all") //Need correct API when its made.
        .then(res => res.json())
        .then(data => {
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").append(createTable(data));
        })
}


//City Button
//Sets .output to a table with all persons from a city
function getCity() {
    var zip = document.getElementById("inputGroupCity1").value;
    getFetchData2(zip);
}

function getFetchData2(zip) {
    fetch("http://localhost:8080/ca2/api/person/findByZip/" + zip)
        .then(res => res.json())
        .then(data => {
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").append(createTable(data));
        })
}


//Hobby Count Button
//Sets .output to a number equal to the amount of people from a city
function getHobbyCount() {
    var hobby = document.getElementById("inputGroupHobby2").value;
    getFetchData3(hobby);
}

function getFetchData3(hobbyid) {
    fetch("http://localhost:8080/ca2/api/hobby/personCount/" + hobbyid)
        .then(res => res.json())
        .then(data => {
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").append(createTable(data));
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
            document.getElementById("output2").append(createTable(zipcodes));
        })
}


//Sets the Options of hobbies to available hobbies
//from end point all hobbies.
function loadHobbyOptions1() {
    fetch("http://localhost:8080/ca2/api/hobby/all")
        .then(res => res.json())
        .then(data => {
            for (var i in data) {
                var option = document.createElement('option');
                option.setAttribute('value', data[i].id);
                option.innerHTML = data[i].name;
                document.getElementById("inputGroupHobby1").appendChild(option);
            }
        })
}

//Sets the Options of hobbies to available hobbies
//from end point all hobbies.
function loadHobbyOptions2() {
    fetch("http://localhost:8080/ca2/api/hobby/all")
        .then(res => res.json())
        .then(data => {
            for (var i in data) {
                var option = document.createElement('option');
                option.setAttribute('value', data[i].id);
                option.innerHTML = data[i].name;
                document.getElementById("inputGroupHobby2").appendChild(option);
            }
        })
}


//Sets the Options of citites to available cities
//might need another endpoint.
function loadCityOptions() {
    fetch("https://dawa.aws.dk/postnumre")
        .then(res => res.json())
        .then(data => {
            for (var i in data) {
                var option = document.createElement('option');
                option.setAttribute('value', data[i].id);
                option.innerHTML = data[i].navn;
                document.getElementById("inputGroupCity1").appendChild(option);
            }
        })
}



//Automatic Table Generator
function createTable(array) {
    if (!Array.isArray(array)) {
        array = [array];
    }
    if (array.length <= 0) {
        return document.createElement("p").innerHTML = "No persons found ..";
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
                    if(typeof obj[key][k] === 'object')
                        Object.keys(obj[key][k]).map(l => {
                            objS.push(obj[key][k][l])
                        });
                    else if (k != "id")
                        objS.push(obj[key][k])
                });
                tBRow.insertCell(index).innerHTML = objS.join(", ");
            }
            else if (Array.isArray(obj[key])) {
                var objS = [];
                for(var i = 0; i < obj[key].length; i++) {
                    Object.keys(obj[key][i]).map(k => {
                        if (k != "id")
                            objS.push(obj[key][i][k])
                    });
                    tBRow.insertCell(index).innerHTML = objS.join(", ");
                }
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


var hobbyCounter = 0;
//Adding Coloumns to the Modal
//Hobby Coloumn
function addHobbyColoumn() {
    hobbyCounter--;
    var outerDiv = document.createElement("div");
    outerDiv.setAttribute('class', 'row d-flex justify-content-center');
    outerDiv.setAttribute('id', 'outerDiv' + hobbyCounter);

    var innerDivCol1 = document.createElement("div");
    innerDivCol1.setAttribute('class', 'col-sm');
    var input1 = document.createElement("input");
    input1.setAttribute('type', 'text');
    input1.setAttribute('class', 'form-control');
    input1.setAttribute('id', 'recipient-name');
    input1.setAttribute('placeholder', 'Name');
    innerDivCol1.appendChild(input1);

    var innerDivCol2 = document.createElement("div");
    innerDivCol2.setAttribute('class', 'col-sm');
    var input2 = document.createElement("input");
    input2.setAttribute('type', 'text');
    input2.setAttribute('class', 'form-control');
    input2.setAttribute('id', 'recipient-name');
    input2.setAttribute('placeholder', 'Description');
    innerDivCol2.appendChild(input2);

    var innerDivCol3 = document.createElement("div");
    innerDivCol3.setAttribute('class', 'col-sm');
    var deleteButton = document.createElement('button');
    deleteButton.setAttribute('class', 'btn btn-danger');
    deleteButton.setAttribute("id", hobbyCounter);
    deleteButton.addEventListener("click", deleteColoumn);
    deleteButton.innerHTML = "Delete";
    innerDivCol3.appendChild(deleteButton);

    outerDiv.appendChild(innerDivCol1);
    outerDiv.appendChild(innerDivCol2);
    outerDiv.appendChild(innerDivCol3);

    document.getElementById("hobbyColoumn").appendChild(outerDiv);
}


var phoneCounter = 0;
//Adding Coloumns to the Modal
//Phone Coloumn
function addPhoneColoumn() {
    phoneCounter++;
    var outerDiv = document.createElement("div");
    outerDiv.setAttribute('class', 'row d-flex justify-content-center');
    outerDiv.setAttribute('id', 'outerDiv' + phoneCounter);

    var innerDivCol1 = document.createElement("div");
    innerDivCol1.setAttribute('class', 'col-sm');
    var input1 = document.createElement("input");
    input1.setAttribute('type', 'text');
    input1.setAttribute('class', 'form-control');
    input1.setAttribute('id', 'recipient-name');
    input1.setAttribute('placeholder', 'Number');
    innerDivCol1.appendChild(input1);

    var innerDivCol2 = document.createElement("div");
    innerDivCol2.setAttribute('class', 'col-sm');
    var input2 = document.createElement("input");
    input2.setAttribute('type', 'text');
    input2.setAttribute('class', 'form-control');
    input2.setAttribute('id', 'recipient-name');
    input2.setAttribute('placeholder', 'Description');
    innerDivCol2.appendChild(input2);

    var innerDivCol3 = document.createElement("div");
    innerDivCol3.setAttribute('class', 'col-sm');
    var deleteButton = document.createElement('button');
    deleteButton.setAttribute('class', 'btn btn-danger');
    deleteButton.setAttribute("id", phoneCounter);
    deleteButton.addEventListener("click", deleteColoumn);
    deleteButton.innerHTML = "Delete";
    innerDivCol3.appendChild(deleteButton);

    outerDiv.appendChild(innerDivCol1);
    outerDiv.appendChild(innerDivCol2);
    outerDiv.appendChild(innerDivCol3);

    document.getElementById("phoneColoumn").appendChild(outerDiv);
}


function deleteColoumn(e) {
    document.getElementById('outerDiv' + e.target.id).outerHTML = "";
}
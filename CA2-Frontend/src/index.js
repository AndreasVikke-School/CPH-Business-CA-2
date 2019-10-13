import 'bootstrap/dist/css/bootstrap.css'

var baseurl = "https://andreasvikke.dk/CA2-Backend/api/";


window.onload = () => {
    startFetch(baseurl + "person/all");

    loadHobbyOptions();

    var allBtn = document.getElementById("allBtn");
    allBtn.addEventListener("click", getAll);

    var btnHobby = document.getElementById("hobbyBtn");
    btnHobby.addEventListener("click", getByHobby);

    var btnCount = document.getElementById("countBtn");
    btnCount.addEventListener("click", getCountByHobby);

    var btnCity = document.getElementById("cityBtn");
    btnCity.addEventListener("click", getCity);

    var zipcodesBtn = document.getElementById("zipcodesBtn");
    zipcodesBtn.addEventListener("click", getZipcodes);

    var cmpyListBtn = document.getElementById("cmpyListBtn");
    cmpyListBtn.addEventListener("click", getCompanyList);

    var hobbyButton = document.getElementById("hobbyBtnCol");
    hobbyButton.addEventListener("click", addHobbyColoumn);

    var phoneButton = document.getElementById("phoneBtn");
    phoneButton.addEventListener("click", addPhoneColoumn);

    var createPersonBtn = document.getElementById("createPersonBtn");
    createPersonBtn.addEventListener("click", createPerson);
}

//All Button
//Sets .output to a table with all persons
function getAll() {
    startFetch(baseurl + "person/all");
}

//Hobby Button
//Sets .output to a table with all persons with a given hobby
function getByHobby() {
    var hobbyid = document.getElementById("inputGroupHobby1").value;
    startFetch(baseurl + "person/all");
}

//Hobby Count Button
//Sets .output to a number equal to the amount of people from a hobby
function getCountByHobby() {
    var hobbyid = document.getElementById("inputGroupHobby1").value;
    startFetch(baseurl + "hobby/personCount/" + hobbyid);
}

//City Button
//Sets .output to a table with all persons from a city
function getCity() {
    var zip = document.getElementById("inputGroupCity1").value;
    startFetch(baseurl + "person/findByZip/" + zip);
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
function loadHobbyOptions() {
    fetch(baseurl + "hobby/all")
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
                    if (typeof obj[key][k] === 'object')
                        Object.keys(obj[key][k]).map(l => {
                            if (l != "id")
                                objS.push(obj[key][k][l])
                        });
                    else if (k != "id")
                        objS.push(obj[key][k])
                });
                tBRow.insertCell(index).innerHTML = objS.join(", ");
            }
            else if (Array.isArray(obj[key])) {
                var objS = [];
                for (var i = 0; i < obj[key].length; i++) {
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
    startFetch(baseurl + "company/getByCount/" + amount);
}



var hobbyCounter = 0;
//Adding Coloumns to the Modal
//Hobby Coloumn
function addHobbyColoumn() {
    hobbyCounter++;
    var outerDiv = document.createElement("div");
    outerDiv.setAttribute('class', 'row d-flex justify-content-center');
    outerDiv.setAttribute('id', 'outerDivh' + hobbyCounter);

    var innerDivCol1 = document.createElement("div");
    innerDivCol1.setAttribute('class', 'col-sm');
    var input1 = document.createElement("input");
    input1.setAttribute('type', 'text');
    input1.setAttribute('class', 'form-control');
    input1.setAttribute('id', 'hobbyname-'+hobbyCounter);
    input1.setAttribute('placeholder', 'Name');
    innerDivCol1.appendChild(input1);

    var innerDivCol2 = document.createElement("div");
    innerDivCol2.setAttribute('class', 'col-sm');
    var input2 = document.createElement("input");
    input2.setAttribute('type', 'text');
    input2.setAttribute('class', 'form-control');
    input2.setAttribute('id', 'hobbydesc-'+hobbyCounter);
    input2.setAttribute('placeholder', 'Description');
    innerDivCol2.appendChild(input2);

    var innerDivCol3 = document.createElement("div");
    innerDivCol3.setAttribute('class', 'col-sm');
    var deleteButton = document.createElement('button');
    deleteButton.setAttribute('class', 'btn btn-danger');
    deleteButton.setAttribute("id", "h" + hobbyCounter);
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
    outerDiv.setAttribute('id', 'outerDivp' + phoneCounter);

    var innerDivCol1 = document.createElement("div");
    innerDivCol1.setAttribute('class', 'col-sm');
    var input1 = document.createElement("input");
    input1.setAttribute('type', 'text');
    input1.setAttribute('class', 'form-control');
    input1.setAttribute('id', 'number-'+phoneCounter);
    input1.setAttribute('placeholder', 'Number');
    innerDivCol1.appendChild(input1);

    var innerDivCol2 = document.createElement("div");
    innerDivCol2.setAttribute('class', 'col-sm');
    var input2 = document.createElement("input");
    input2.setAttribute('type', 'text');
    input2.setAttribute('class', 'form-control');
    input2.setAttribute('id', 'phonedesc-'+phoneCounter);
    input2.setAttribute('placeholder', 'Description');
    innerDivCol2.appendChild(input2);

    var innerDivCol3 = document.createElement("div");
    innerDivCol3.setAttribute('class', 'col-sm');
    var deleteButton = document.createElement('button');
    deleteButton.setAttribute('class', 'btn btn-danger');
    deleteButton.setAttribute("id", "p" + phoneCounter);
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


//Get the values of all the phone inputs returns a string
function getPhonesFromForm() {
    var phones = [];
    for (var i = 1; i <= phoneCounter; ++i) {
        var number = document.getElementById("number-"+i);
        var desc = document.getElementById("phonedesc-"+i);
        if (number != null && desc != null)
            phones.push({name: number.value, description: desc.value});
    }
    return phones;
}

//Get the values of all the hobby inputs returns a string
function getHobbiesFromForm() {
    var hobbies = [];
    for (var i = 1; i <= hobbyCounter; ++i) {
        var name = document.getElementById("hobbyname-"+i);
        var desc = document.getElementById("hobbydesc-"+i);
        if (name != null && desc != null)
            hobbies.push({name: name.value, description: desc.value, persons: [null]});
    }
    return hobbies;
}

//Creates a person with the json aswell as the strings of phones and hobbies
function createPerson() {
    var phones = getPhonesFromForm();
    var hobbies = getHobbiesFromForm();
    fetch(baseurl + "person/add", makeOptions("POST", {
        email: document.getElementById("emailInput").value,
        phones: phones,
        address: {
            street: document.getElementById("streetNameInput").value,
            cityInfo: {
                city: document.getElementById("cityNameInput").value,
                zip: document.getElementById("zipCodeInput").value
            }
        },
        firsName: document.getElementById("firstNameInput").value,
        lastName: document.getElementById("lastNameInput").value,
        hobbies: hobbies
    }));
}

function startFetch(url, option) {
    fetch(url, option)
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            document.getElementById("output").innerHTML = "";
            document.getElementById("output").append(createTable(data));
        });
};

function makeOptions(method, body) {
    var opts =  {
        method: method,
        headers: {
        'Accept': 'application/json',
        "Content-type": "application/json"
        }
    }
    if(body){
        opts.body = JSON.stringify(body);
    }
    return opts;
}
   
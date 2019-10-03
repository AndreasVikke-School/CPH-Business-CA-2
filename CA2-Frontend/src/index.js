import 'bootstrap/dist/css/bootstrap.css'

window.onload = () => {
    document.getElementById("container").append(createTable([{"firstname" : "Andreas", "lastname" : "Vikke"},
    {"firstname" : "Emil", "lastname" : "Svensmark"}]));
}


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
            if(typeof obj[key] === 'object') {
                var objS = [];
                Object.keys(obj[key]).map(k => {
                    if(k != "id")
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
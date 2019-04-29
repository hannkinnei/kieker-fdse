let startButton = $("#startScenario");
let endButton = $("#endScenario");
let scenarioNames = $("#scenarioName");
let scenarios = $("#scenarios");
let startTime, endTime, scenarioName;
let xmlHttp;
startButton.attr("disabled", false);
endButton.attr("disabled", true);
startButton.click(function () {
    let name = scenarioNames.val();
    if(name === ""){
        alert("Scenario name can not be null!");
        return;
    }
    scenarioName = name;
    sendRequest(1, name);
    startButton.attr("disabled", true);
    endButton.attr("disabled", false);
    scenarioNames.attr("disabled", true);
    startTime = new Date().toLocaleString();
});

endButton.click(function () {
   sendRequest(2, "");
    startButton.attr("disabled", false);
    endButton.attr("disabled", true);
    endTime = new Date().toLocaleString();
    scenarioNames.attr("disabled", false);
    addScenario();
});

function sendRequest(type, name) {
    if (window.XMLHttpRequest){
        xmlHttp=new XMLHttpRequest();
    }
    else {
        xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    let queryString="scenario?";
    queryString=queryString+"type=" + type +"&name=" + name;
    xmlHttp.onreadystatechange= function () {
        if(xmlHttp.readyState === 4){
            if(xmlHttp.status === 200){
                let responseText = document.createTextNode(xmlHttp.responseText);
                alert(responseText);
            }
        }
    };
    xmlHttp.open("GET",queryString,true);
    xmlHttp.send(null);
}

function addScenario() {
    let html = scenarios.html();
    if($.trim(html) === ""){
        html = "<tr>\n" +
            "            <th>Scenario Name</th>\n" +
            "            <th>Start Time</th>\n" +
            "            <th>End Time</th>\n" +
            "        </tr>";
    }
    html += "<tr>\n" +
        "            <td>" + scenarioName + "</td>\n" +
        "            <td>" + startTime + "</td>\n" +
        "            <td>" + endTime + "</td>\n" +
        "        </tr>";
    scenarios.html(html);
}
let startButton = $("#startScenario");
let endButton = $("#endScenario");
let scenarioNames = $("#scenarioName");
let scenarioFrequencies = $("#scenarioFrequency");
let scenarios = $("#scenarios");
let startTime, endTime, scenarioName, scenarioFrequency;
let xmlHttp;
startButton.attr("disabled", false);
endButton.attr("disabled", true);
startButton.click(function () {
    let name = scenarioNames.val();
    let frequency = scenarioFrequencies.val();
    if(name === ""){
        alert("Scenario name can not be null!");
        return;
    }
    scenarioName = name;
    scenarioFrequency = frequency;
    sendRequest(1, name, frequency);
    startButton.attr("disabled", true);
    endButton.attr("disabled", false);
    scenarioNames.attr("disabled", true);
    scenarioFrequencies.attr("disabled", true);
    startTime = new Date().toLocaleString();
});

endButton.click(function () {
   sendRequest(2, "", 0);
    startButton.attr("disabled", false);
    endButton.attr("disabled", true);
    endTime = new Date().toLocaleString();
    scenarioNames.attr("disabled", false);
    scenarioFrequencies.attr("disabled", false);
    addScenario();
});

function sendRequest(type, name, frequency) {
    if (window.XMLHttpRequest){
        xmlHttp=new XMLHttpRequest();
    }
    else {
        xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    let queryString="scenario?";
    /*解决中文乱码*/
    name = encodeURI(encodeURI(name));
    queryString=queryString+"type=" + type +"&name=" + name + "&frequency=" + frequency;
    xmlHttp.onreadystatechange= function () {
        if(xmlHttp.readyState === 4){
            if(xmlHttp.status === 200){
                // let responseText = document.createTextNode(xmlHttp.responseText);
                if(type === 1){
                    startTime = xmlHttp.responseText;
                }
                else {
                    endTime = xmlHttp.responseText;
                }
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
            "            <th>Scenario Frequency</th>\n" +
            "        </tr>";
    }
    html += "<tr>\n" +
        "            <td>" + scenarioName + "</td>\n" +
        "            <td>" + startTime + "</td>\n" +
        "            <td>" + endTime + "</td>\n" +
        "            <td>" + scenarioFrequency + "</td>\n" +
        "        </tr>";
    scenarios.html(html);
}
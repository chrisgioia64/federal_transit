window.onload = function() {
    loadNumberAgencies();
    // loadAggregateStatistics("UPT", "AGENCY", "LR");

    const searchButton = document.querySelector('#search_button');
    searchButton.addEventListener('click', () => {
       updateResultsTable();
    });
}

async function updateResultsTable() {
    const statisticElement = document.querySelector("#aggregate_statistic");
    const entityType = document.querySelector("#entity_type");
    console.log(statisticElement.value);
    console.log(entityType.value);

    let entityValue = entityType.options[entityType.selectedIndex].text;
    let aggregateValue = statisticElement.options[statisticElement.selectedIndex].text;
    let innerHTML = getResultsTableHeaderRow(entityValue, aggregateValue);
    console.log(innerHTML);

    let json = await loadAggregateStatistics(statisticElement.value, entityType.value, "LR");
    let endIndex = Math.min(10, json.length);
    for (var i = 0; i < endIndex; i++) {
        htmlRow = getHtmlRow(json[i], i);
        innerHTML += htmlRow;
        console.log("row: " + htmlRow);
    }

    let table = document.querySelector("#result_table");
    table.innerHTML = innerHTML;

}

function getResultsTableHeaderRow(entityValue, aggregateValue) {
    let str = "<tr class='row_header'>";
    str += "<td class='col_rank'></td>";
    str += "<td class='col_entity'>" + entityValue + "</td>";
    str += "<td class='col_statistic'>" + aggregateValue + "</td>";
    str += "</tr>";
    return str;
}

function getHtmlRow(jsonElement, idx) {
    let str = "<tr>";
    str += "<td class='col_rank'>" + (idx + 1) + "</td>";
    str += "<td class='col_entity'>" + jsonElement.entityName + "</td>";
    str += "<td class='col_statistic'>" + jsonElement.aggregateStatistic.toLocaleString() + "</td>";
    str += "</tr>";
    return str;
}

async function loadAggregateStatistics(statistic, entity, travelMode) {

    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var raw = JSON.stringify({
      "aggregateStatistic": statistic,
      "aggregateEntity": entity,
      "travelMode": travelMode
    });

    var requestOptions = {
      method: 'POST',
      headers: myHeaders,
      body: raw,
      redirect: 'follow'
    };

    const response = await fetch("http://localhost:8081/query/get_aggregate", requestOptions);
    let json = await response.json();
    return json;
}

async function loadNumberAgencies() {
    var requestOptions = {
      method: 'GET',
      redirect: 'follow'
    };

    const response = await fetch("http://localhost:8081/database/num_agencies", requestOptions);
    let parts = await response.text();
    console.log(parts);
}

function foo(x) {
    return x * x;
}

console.log(foo(5));

window.onload = function() {
    loadNumberAgencies();
    // loadAggregateStatistics("UPT", "AGENCY", "LR");

    const searchButton = document.querySelector('#search_button');
    searchButton.addEventListener('click', () => {
       updateResultsTable();
        printResults();
    });

    const nextButton = document.querySelector("#next_button");
    nextButton.addEventListener('click', () => {
        let pageNumber = CURRENT_PAGE + 1;
        CURRENT_PAGE++;
        setPage(pageNumber);
        loadResults(pageNumber);
    });

    const prevButton = document.querySelector("#prev_button");
    prevButton.addEventListener('click', () => {
        let pageNumber = CURRENT_PAGE - 1;
        CURRENT_PAGE--;
        setPage(pageNumber);
        loadResults(pageNumber);
    });

}

var resultsGlobal = [];
var CURRENT_PAGE = 0;
var PAGINATION_LIMIT = 10;
var PAGE_COUNT = 0;

function printResults() {
    console.log("resultsGlobal: " + resultsGlobal.length);
}

// Main method for updating the results table
// Reads the input values, performs a fetch request, and loads the data for the
// first page
// why is this an async function?
async function updateResultsTable() {
    const statisticElement = document.querySelector("#aggregate_statistic");
    const entityType = document.querySelector("#entity_type");
    console.log(statisticElement.value);
    console.log(entityType.value);

    let entityValue = entityType.options[entityType.selectedIndex].text;
    let aggregateValue = statisticElement.options[statisticElement.selectedIndex].text;
    let innerHTML = getResultsTableHeaderRow(entityValue, aggregateValue);
    let tableHeader = document.querySelector("#result_table thead");
    tableHeader.innerHTML = innerHTML;

    innerHTML = "";
    let json = await loadAggregateStatistics(statisticElement.value, entityType.value, "LR");
    resultsGlobal = json;
    PAGE_COUNT = Math.ceil(resultsGlobal.length / PAGINATION_LIMIT);
    console.log("pages: " + PAGE_COUNT);
    let endIndex = Math.min(10, json.length);
    for (var i = 0; i < endIndex; i++) {
        htmlRow = getHtmlRow(json[i], i);
        innerHTML += htmlRow;
    }

    let table = document.querySelector("#result_table tbody");
    table.innerHTML = innerHTML;
    CURRENT_PAGE = 1;
    setPage(CURRENT_PAGE);
}

// Loads the data for the results table for a given page number
// The results data is stored globally
function loadResults(pageNumber) {
    let startIndex = pageNumber * PAGINATION_LIMIT;
    let endIndex = Math.min(startIndex + PAGINATION_LIMIT, resultsGlobal.length);
    let innerHTML = "";
    for (var i = startIndex; i < endIndex; i++) {
        htmlRow = getHtmlRow(resultsGlobal[i], i);
        innerHTML += htmlRow;
    }

    let table = document.querySelector("#result_table tbody");
    table.innerHTML = innerHTML;
}

// Update the enabled properties of the previous and next buttons
// based on the page number
function setPage(pageNumber) {
    let prevButton = document.querySelector("#prev_button");
    let nextButton = document.querySelector("#next_button");
    if (pageNumber == 0) {
        prevButton.disabled = true;
        prevButton.classList.add("disabled");
    } else {
        prevButton.disabled = false;
        prevButton.classList.remove("disabled");
    }
    if (pageNumber == PAGE_COUNT - 1) {
        nextButton.disabled = true;
        nextButton.classList.add("disabled");
    } else {
        nextButton.disabled = false;
        nextButton.classList.remove("disabled");
    }
}

// Get the table header row as a string
function getResultsTableHeaderRow(entityValue, aggregateValue) {
    let str = "<tr class='row_header'>";
    str += "<td class='col_rank'></td>";
    str += "<td class='col_entity'>" + entityValue + "</td>";
    str += "<td class='col_statistic'>" + aggregateValue + "</td>";
    str += "</tr>";
    return str;
}

// Get a particular row of the table
function getHtmlRow(jsonElement, idx) {
    let str = "<tr>";
    str += "<td class='col_rank'>" + (idx + 1) + "</td>";
    str += "<td class='col_entity'>" + jsonElement.entityName + "</td>";
    str += "<td class='col_statistic'>" + jsonElement.aggregateStatistic.toLocaleString() + "</td>";
    str += "</tr>";
    return str;
}

// Performs the API call for retrieving the aggregate statistics date
// Copied the headers and body response data from Postman
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

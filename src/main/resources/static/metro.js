
const useState = React.useState;
const useEffect = React.useEffect;

function OurApp() {
    return (
        <div>
            <Header/>
            <StateAreaCards/>
        </div>
    )
}

function Header() {
    return <h1>States</h1>
}

function StateAreaCards() {
    const [states, setStates] = useState([]);

    useEffect(() => {
        setStatesAPI(setStates);
    }, [])

    return (
    <div>
        {states.map(
            function(state) {
                        return <StateAreaCard state={state} />;
                    })
        }
    </div>)
}

function StateAreaCard(props) {
    return (
     <div className="state_card">
        <p className="state_title">{props.state}</p>
        <MetroAreaCards state={props.state} />
     </div>)
}

function MetroAreaCards(props) {
    const [metros, setMetros] = useState([]);

    useEffect(() => {
        setMetroAPI(setMetros, props.state);
    }, [])

    return (
        <ul>
            {metros.map(function(metro) {
                        return <MetroAreaCard metro={metro} />;
                    })
            }
        </ul>
    )

}

function MetroAreaCard(props) {
    return (
        <div className="metro_card">
        <div className="metro_card_title">
        {props.metro}
        </div>
        <div className="metro_card_content">
            <MetroAreaTable metro={props.metro} />
        </div>
        </div>)
}

function MetroAreaTable(props) {
    const [metros, setMetros] = useState([]);

    if (props.metro.startsWith("")) {
        useEffect(() => {
            setMetroInfoAPI(setMetros, props.metro);
        }, [])
    }

    return (
      <table class="metro_table">
          <thead>
              <tr>
                  <td class="table_col_attribute"></td>
                  <td class="table_col_total">Total</td>
                  <td class="table_col_total_rank">Total Rank</td>
                  <td class="table_col_per_capita">Per Capita</td>
                  <td class="table_col_per_capita_rank">Per Capita Rank</td>
              </tr>
          </thead>
          <tbody>
              {metros.map(function(metro) {
                        return <MetroAreaTableRow row={metro} />;
                    })
              }
          </tbody>
      </table>
    );
}

function MetroAreaTableRow(props) {
    let row = props.row;
    return (
        <tr>
                <td class="table_col_attribute">{row.statisticName}</td>
                  <td class="table_col_total">{row.totalAmount.toLocaleString()}</td>
                  <td class="table_col_total_rank">{row.totalRank}</td>
                  <td class="table_col_per_capita">{row.perCapitaAmount.toLocaleString()}</td>
                  <td class="table_col_per_capita_rank">{row.perCapitaRank}</td>
        </tr>
    )
}

const domContainer = document.querySelector("#app");
const root = ReactDOM.createRoot(domContainer);

root.render(<OurApp />);


async function setStatesAPI(setStates) {
    var requestOptions = {
      method: 'POST',
      redirect: 'follow'
    };

    const response = await fetch("http://localhost:8081/query/states", requestOptions);
    let json = await response.json();
    console.log("json: " + json);
    setStates(json);
    return json;
}

async function setMetroAPI(setMetros, state) {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var raw = JSON.stringify({
      "value": state
    });

    var requestOptions = {
      method: 'POST',
      headers: myHeaders,
      body: raw,
      redirect: 'follow'
    };

    let response = await fetch("http://localhost:8081/query/metro_by_state", requestOptions)
    let json = await response.json();
    setMetros(json);
    return json;
}

async function setMetroInfoAPI(setMetros, metro) {
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    var raw = JSON.stringify({
      "value": metro
    });

    var requestOptions = {
      method: 'POST',
      headers: myHeaders,
      body: raw,
      redirect: 'follow'
    };

    let response = await fetch("http://localhost:8081/query/metro_rank", requestOptions);
    let json = await response.json();
    console.log("status: " + response.status);
    if (response.status == 200) {
        setMetros(json);
    }
    // console.log(json);
    return json;
}



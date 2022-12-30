
// function OurApp() {
//     return React.createElement("div", null,
//         [
//             React.createElement("h1", null, "HW"),
//             React.createElement("p", null, `This is a paragraph. Time is ${new Date().toLocaleString()}`)
//         ]
//     );
// }

const useState = React.useState;


// const persons = [
//     {name : 'chris', age : 30},
//     {name : 'john' , age : 64},
//     {name: 'maria' , age : 60}
// ]

function OurApp() {
    const [persons, setPersons] = useState([
    // {name : 'chris', age : 30, id: 1},
    // {name : 'john' , age : 64, id: 2},
    // {name: 'maria' , age : 60, id: 3}
    ]);

    const [id, setId] = useState(4);

    return (
        <div>
            <OurHeader/>
            <OurTime/>
            <LikeArea/>
            <ul>
                {persons.map(function(person) {
                        return <OurItem setPersons={setPersons}
                                name={person.name} age={person.age} id={person.id} />;
                    })
                }
            </ul>
            <AddPersonForm elementId = {id}
                setElementId={setId} setPersons={setPersons}/>
        </div>
    )
}


// This is referred to as a React Component
function OurHeader() {
    return <h1>This is a Header</h1>
}

function OurTime() {
    const [theTime, setTime] = useState(new Date().toLocaleString());
    setTimeout(function() {
        setTime(new Date().toLocaleString())
    }, 1000)
    return <p className='time'>The current time is {theTime}
        </p>
}

function OurItem(props) {
    function handleDelete() {
        props.setPersons(prev => {
            return prev.filter(x => x.id != props.id);
        })
    }

    return (<li>A person {props.name} who is {props.age} years old
        ({props.id})
        <button onClick={handleDelete}>Delete</button>
        </li>)
}

function LikeArea() {
    const [likes, setLikes] = useState(0);

    function increaseLike() {
        // alert('Thanks for clicking name');
        setLikes(function(prev) {
            return prev + 1;
        });
    }
    function decreaseLike() {
        setLikes(likes-1);
    }
    return (
        <div>
           <button onClick={increaseLike}>Increase</button>
            <button onClick={decreaseLike}>Decrease</button>
           <h2>This page has been liked {likes} times</h2>
        </div>
    )
}


function AddPersonForm(props) {
    const [name, setName] = useState();
    const [age, setAge] = useState();

    function handleSubmit(e) {
        e.preventDefault();
        // alert("add person");
        props.setPersons(prev => {
            return prev.concat({name: name , age : age, id: props.elementId});
        })
        props.setElementId( x => x + 1);
        setName("");
        setAge("");
    }
    return (
        <form onSubmit={handleSubmit}>
            <fieldset>
                <legend>Add new person</legend>
                <input value={name} onChange = {e => setName(e.target.value)} placeholder="name" />
                <input value={age} onChange = {e => setAge(e.target.value)} placeholder="age" />
                <button>Add person</button>
            </fieldset>
        </form>
    )
}

const domContainer = document.querySelector("#app");
const root = ReactDOM.createRoot(domContainer);

// root.render(React.createElement(OurApp));


root.render(<OurApp />);
import logo from './logo.svg';
import './App.css';
import axios from 'axios';
function giveCoupons(e){
       
        }
function App() {
  return (
    <div className="App">
      <br></br>
      <div>
        <h2 id="mainHeading">Url_Shortern</h2>
        <div>
       
      <div style={{
        backgroundColor: '#ffffff',
        border: '2px solid #ccc',
        borderRadius: '5px',
        padding: '15px',
        marginBottom: '20px'
      }}>
        <label htmlFor="input">Input:</label><br />
        <textarea
          id="input"
          placeholder="Type something..."
          style={{
            width: '20%',
            height: '20px',
            fontSize: '15px',
            padding: '5px'
          }}
        />
        <button  style={{
        marginBottom: '5px',
        padding: '2px 3px',
        fontSize: '10px',
        cursor: 'pointer'
      }} onClick={giveCoupons}>
        Get Short Url
      </button>
      </div>
      <div style={{
        backgroundColor: '#ffffff',
        border: '2px solid #ccc',
        borderRadius: '5px',
        padding: '15px',
        minHeight: '100px',
        fontSize: '16px'
      }}>
      <label htmlFor="output">Output:</label><br />
        <textarea
          id="output"
          readOnly
          style={{
            width: '20%',
            height: '20px',
            fontSize: '15px',
            padding: '5px'
          }}></textarea>
      </div>
      </div>
      </div>
      <br></br>
      <br></br>
    </div>
  );
}

export default App;

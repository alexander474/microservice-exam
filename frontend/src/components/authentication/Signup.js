// https://github.com/arcuri82/web_development_and_api_design
import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {requestHandler} from "../../utils/RequestHandler"


export class Signup extends React.Component{

    constructor(props){
        super(props);

        this.state = {
            username: "",
            password: "",
            errorMsg: null
        };
    }

    onUsernameChange = (event) =>{
        this.setState({username: event.target.value});
    };

    onPasswordChange = (event) => {
        this.setState({password: event.target.value});
    };

    doSignUp = async () => {
        const { onUserChange } = this.props;
        const {username, password} = this.state;
        this.setState({errorMsg: null});

        requestHandler.signUp(username, password).then(res => {
            if(res.status === 204){
                requestHandler.getAuthUser().then(res => {
                    if(res.status === 200){
                        console.log(res)
                        onUserChange(res.data)
                        //this.props.history.push('/');
                    }else if(res.message !== null){
                        this.setState({errorMsg: res.message});
                    }
                })
            }else if(res.message !== null){
                this.setState({errorMsg: res.message});
            }
        }).catch( e => this.setState({errorMsg: "Could not execute request"}))

    };


    render(){

        let error = <div></div>;
        if(this.state.errorMsg !== null){
            error = <div className="errorMsg"><p>{this.state.errorMsg}</p></div>
        }


        return(
            <div>
                <div>
                    <p>Username:</p>
                    <input type="text"
                           value={this.state.username}
                           onChange={this.onUsernameChange}
                           id="usernameInput"
                    />
                </div>
                <div>
                    <p>Password:</p>
                    <input type="password"
                           value={this.state.password}
                           onChange={this.onPasswordChange}
                           id="passwordInput"
                    />
                </div>

                {error}

                <div className="btn" onClick={()=>this.doSignUp()} id="registerBtn">Register</div>
            </div>);
    }
}

export default withRouter(Signup);

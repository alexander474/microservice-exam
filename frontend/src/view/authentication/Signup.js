// https://github.com/arcuri82/web_development_and_api_design
import React from 'react';
import {Link, withRouter} from 'react-router-dom';
import {requestHandler} from "../../utils/RequestHandler"
import {
    Alert,
    Button,
    Card,
    CardBody, CardTitle,
    Input,
    InputGroup,
    InputGroupAddon,
    InputGroupText,
    UncontrolledAlert
} from "reactstrap";


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
                        onUserChange(res.data.data)
                        this.props.history.push('/');
                    }else if(res.message !== null){
                        this.setState({errorMsg: res.data.message});
                    }
                })
            }else if(res.data.message !== null){
                this.setState({errorMsg: res.data.message});
            }
        }).catch( e => this.setState({errorMsg: "Could not execute request"}))

    };


    render(){

        let error = <div></div>;
        if(this.state.errorMsg !== null){
            error = <UncontrolledAlert color="danger">{this.state.errorMsg}</UncontrolledAlert>
        }


        return(
            <div>
                <Card>
                    <CardBody>
                        <CardTitle>Login</CardTitle>
                        <InputGroup>
                            <Input type="text"
                                   id="usernameInput"
                                   onChange={(e)=>this.onUsernameChange(e)}
                                   value={this.state.username}
                                   placeholder="username" />
                        </InputGroup>
                        <InputGroup>
                            <Input type="password"
                                   id="passwordInput"
                                   onChange={(e)=>this.onPasswordChange(e)}
                                   value={this.state.password}
                                   placeholder="password" />
                        </InputGroup>

                        {error}

                        <Button onClick={()=>this.doSignUp()} id="loginBtn" color="primary">Register</Button>
                    </CardBody>
                </Card>
            </div>);
    }
}

export default withRouter(Signup);

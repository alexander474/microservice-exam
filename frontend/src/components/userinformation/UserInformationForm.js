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


export class UserInformationForm extends React.Component{

    constructor(props){
        super(props);

        this.state = {
            username: this.props.match.params.userId,
            name: "",
            surname: "",
            middlename: "",
            email: "",
            errorMsg: null
        };
    }

    onUsernameChange = (event) =>{
        this.setState({username: event.target.value});
    };

    onNameChange = (event) =>{
        this.setState({name: event.target.value});
    };

    onMiddleNameChange = (event) =>{
        this.setState({middlename: event.target.value});
    };

    onSurnameChange = (event) =>{
        this.setState({surname: event.target.value});
    };

    onEmailChange = (event) =>{
        this.setState({email: event.target.value});
    };

    updateUser = async () => {
        const {username, name, middlename, surname, email} = this.state;
        this.setState({errorMsg: null});

        if(username !== null && username !== undefined) {
            requestHandler.updateUserInformation(username, name, surname, middlename, email).then(res => {
                if (res.status === 201 || res.status === 204) {
                    this.props.history.push('/');
                } else if (res.message !== null) {
                    this.setState({errorMsg: res.data.message});
                }
            })
        }else{
            this.setState({errorMsg: "UserId could not be read from path"});
        }

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
                        <CardTitle>Update user information</CardTitle>
                        <InputGroup>
                            <Input type="text"
                                   id="nameInput"
                                   onChange={(e)=>this.onNameChange(e)}
                                   value={this.state.name}
                                   placeholder="name" />
                        </InputGroup>
                        <InputGroup>
                            <Input type="text"
                                   id="middlenameInput"
                                   onChange={(e)=>this.onMiddleNameChange(e)}
                                   value={this.state.middlename}
                                   placeholder="middlename" />
                        </InputGroup>
                        <InputGroup>
                            <Input type="text"
                                   id="surnameInput"
                                   onChange={(e)=>this.onSurnameChange(e)}
                                   value={this.state.surname}
                                   placeholder="surname" />
                        </InputGroup>
                        <InputGroup>
                            <Input type="text"
                                   id="emailInput"
                                   onChange={(e)=>this.onEmailChange(e)}
                                   value={this.state.email}
                                   placeholder="email" />
                        </InputGroup>

                        {error}

                        <Button onClick={()=>this.updateUser()} id="userUpdateBtn" color="primary">Update</Button>
                    </CardBody>
                </Card>
            </div>);
    }
}

export default withRouter(UserInformationForm);

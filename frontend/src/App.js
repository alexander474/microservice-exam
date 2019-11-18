// https://github.com/arcuri82/web_development_and_api_design
import React from "react";
import ReactDOM from "react-dom";
import {BrowserRouter, Switch, Route} from 'react-router-dom';


import HomePage from "./view/HomePage";
import Login from "./components/authentication/Login"
import Signup from "./components/authentication/Signup"
import Header from "./components/header/Header";


export default class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            user: null,
            isLoggedIn: false,
            isAdmin: false
        };
    }

    onUserChange = (user) => {
        if(user !== null){
            this.onIsLoggedInChange(true);
            this.onisAdminChange(user.roles.contains("ADMIN"))
        }
        this.setState({user});
    };

    onIsLoggedInChange = (isLoggedIn) => {
        this.setState({isLoggedIn});
    };

    onisAdminChange = (isAdmin) => {
        this.setState({isAdmin});
    };

    notFound() {
        return (
            <div>
                <h2>NOT FOUND: 404</h2>
                <p>
                    ERROR: the page you requested in not available.
                </p>
            </div>
        );
    };


    render() {

        return (
            <BrowserRouter>
                <div>
                    <Header/>
                    <Switch>
                        <Route exact path="/"
                               render={props => <HomePage onUserChange={this.onUserChange} {...props}/>}/>
                        <Route exact path="/login"
                               render={props => <Login onUserChange={this.onUserChange} {...props}/>}/>
                        <Route exact path="/register"
                               render={props => <Signup onUserChange={this.onUserChange} {...props}/>}/>
                        <Route component={this.notFound}/>
                    </Switch>
                </div>
            </BrowserRouter>
        );
    }
}


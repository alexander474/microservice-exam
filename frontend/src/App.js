// https://github.com/arcuri82/web_development_and_api_design
import React from "react";
import ReactDOM from "react-dom";
import {BrowserRouter, Switch, Route} from 'react-router-dom';


import HomePage from "./view/HomePage";
import Login from "./view/authentication/Login"
import Signup from "./view/authentication/Signup"
import Header from "./components/header/Header";
import "./Global.css"
import { requestHandler } from "./utils/RequestHandler";


export default class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            user: null,
            isLoggedIn: false,
            isAdmin: false
        };
    }

    async componentDidMount() {
        await requestHandler.getAuthUser().then(res => {
            if(res.status === 200){
                if(res.data !== null && res.data !== undefined) {
                    if (res.data.data !== null && res.data.data !== undefined) {
                        this.onUserChange(res.data.data);
                    }
                }
            }
        }).catch(e => {
            // no authenticated user found
        });
    }


    onUserChange = (user) => {
        if(user !== null){
            this.onIsLoggedInChange(true);
            if(user.roles !== null) {
                this.onisAdminChange(user.roles.includes("ROLE_ADMIN"))
            }
        }else{
            this.onIsLoggedInChange(false);
            this.onisAdminChange(false);
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
                    <Header
                        isLoggedIn={this.state.isLoggedIn}
                        isAdmin={this.state.isAdmin}
                        user={this.state.user}/>
                    <div className={"content-view"}>
                        <Switch>
                            <Route exact path="/"
                                   render={props => <HomePage
                                       onUserChange={this.onUserChange}
                                       isLoggedIn={this.state.isLoggedIn}
                                       isAdmin={this.state.isAdmin}
                                       user={this.state.user}
                                       {...props}/>}/>
                            <Route exact path="/login"
                                   render={props => <Login
                                       onUserChange={this.onUserChange}
                                       isLoggedIn={this.state.isLoggedIn}
                                       isAdmin={this.state.isAdmin}
                                       user={this.state.user}
                                       {...props}/>}/>
                            <Route exact path="/register"
                                   render={props => <Signup
                                       onUserChange={this.onUserChange}
                                       isLoggedIn={this.state.isLoggedIn}
                                       isAdmin={this.state.isAdmin}
                                       user={this.state.user}
                                       {...props}/>}/>
                            <Route component={this.notFound}/>
                        </Switch>
                    </div>
                </div>
            </BrowserRouter>
        );
    }
}


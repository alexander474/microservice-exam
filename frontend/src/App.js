// https://github.com/arcuri82/web_development_and_api_design
import React from "react";
import {BrowserRouter, Route, Switch} from 'react-router-dom';


import HomePage from "./view/HomePage";
import Login from "./view/authentication/Login"
import Signup from "./view/authentication/Signup"
import Header from "./components/header/Header";
import UserInformationForm from "./components/userinformation/UserInformationForm";
import "./Global.css"
import {requestHandler} from "./utils/RequestHandler";


export default class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            user: null,
            isLoggedIn: false,
            isAdmin: false,
            userInformation: null
        };
    }

    async componentDidMount() {
        await this.updateAuthUser()
    }

    updateAuthUser = async () => {
        await requestHandler.getAuthUser().then(res => {
            if (res.status === 200) {
                if (res.data !== null && res.data !== undefined) {
                    if (res.data.data !== null && res.data.data !== undefined) {
                        this.onUserChange(res.data.data);
                    }
                }
            }
        }).catch(e => {
            // no authenticated user found
        });
    };


    getUserInformation = async (user) => {
        if (user !== null) {
            await requestHandler.getUserInformation(user.name).then(res => {
                console.log(res);
                if (res.status === 200) {
                    if (res.data !== null && res.data !== undefined) {
                        if (res.data.data !== null && res.data.data !== undefined) {
                            this.onUserInfoChange(res.data.data);
                        }
                    }
                }
            }).catch(e => {
                // no user found
            });
        }
    };


    onUserInfoChange = (userInformation) => {
        if (userInformation !== null) {
            this.setState({userInformation})
        }
    };


    onUserChange = async (user) => {
        if (user !== null) {
            this.onIsLoggedInChange(true);
            if (user.roles !== null) {
                this.onisAdminChange(user.roles.includes("ROLE_ADMIN"))
            }
        } else {
            this.onIsLoggedInChange(false);
            this.onisAdminChange(false);
        }
        await this.setState({user});
        await this.getUserInformation(user);
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
                                       userInformation={this.state.userInformation}
                                       updateAuthUser={this.updateAuthUser}
                                       getUserInformation={this.getUserInformation}
                                       {...props}/>}/>
                            <Route exact path="/login"
                                   render={props => <Login
                                       onUserChange={this.onUserChange}
                                       isLoggedIn={this.state.isLoggedIn}
                                       isAdmin={this.state.isAdmin}
                                       user={this.state.user}
                                       userInformation={this.state.userInformation}
                                       updateAuthUser={this.updateAuthUser}
                                       {...props}/>}/>
                            <Route exact path="/register/userinformation/:userId"
                                   render={props => <UserInformationForm
                                       onUserChange={this.onUserChange}
                                       isLoggedIn={this.state.isLoggedIn}
                                       isAdmin={this.state.isAdmin}
                                       user={this.state.user}
                                       userInformation={this.state.userInformation}
                                       updateAuthUser={this.updateAuthUser}
                                       {...props}/>}/>
                            <Route exact path="/register"
                                   render={props => <Signup
                                       onUserChange={this.onUserChange}
                                       isLoggedIn={this.state.isLoggedIn}
                                       isAdmin={this.state.isAdmin}
                                       user={this.state.user}
                                       userInformation={this.state.userInformation}
                                       updateAuthUser={this.updateAuthUser}
                                       {...props}/>}/>
                            <Route component={this.notFound}/>
                        </Switch>
                    </div>
                </div>
            </BrowserRouter>
        );
    }
}


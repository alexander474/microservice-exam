import React from "react";
import "./Header.css";
import {Col, Collapse, Nav, Navbar, NavItem, NavbarBrand, Button, NavbarToggler, Row} from "reactstrap";
import { Link, withRouter } from "react-router-dom";
import {requestHandler} from "../../utils/RequestHandler"

export default class Header extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isOpen: false
        };
    }

    toggleNav = () => {
        this.setState({isOpen: !this.state.isOpen});
    };

    logout = async () => {
        console.log("LOGOUT CLICKED");
        await requestHandler.logout().then(r =>  {
            this.props.history.push('/')
        }).catch(e =>  this.props.history.push('/'))
    };

    render() {
        const {isLoggedIn, isAdmin, user} = this.props;
        return (
            <Row className={"header-row"}>
                <Col sm="12" md="12" lg="12" className={"header-row-col"}>
                    <Navbar className={"header-row-col-navbar"} light expand="md">
                        <NavbarToggler onClick={this.toggleNav}/>
                        <Collapse isOpen={this.state.isOpen} navbar>
                            {!isLoggedIn ?
                                <Nav className="ml-auto" navbar>
                                    <NavItem>
                                        <Button color="primary">
                                            <Link className={"link-no-decoration"} to={"/"}>Home</Link>
                                        </Button>
                                    </NavItem>
                                    <NavItem>
                                        <Button color="primary">
                                            <Link className={"link-no-decoration"} to={"/login"}>Login</Link>
                                        </Button>
                                    </NavItem>
                                    <NavItem>
                                        <Button color="primary">
                                            <Link className={"link-no-decoration"} to={"/register"}>Register</Link>
                                        </Button>
                                    </NavItem>
                                </Nav>
                            :
                                <Nav className="ml-auto" navbar>
                                    <NavItem>
                                        <Button color="primary">
                                            <Link className={"link-no-decoration"} to={"/"}>Home</Link>
                                        </Button>
                                    </NavItem>
                                    <NavItem>
                                        <Button onClick={()=>this.logout()} color="primary">
                                            Logout
                                        </Button>
                                    </NavItem>
                                </Nav>}
                        </Collapse>
                    </Navbar>
                </Col>
            </Row>
        );
    }
}
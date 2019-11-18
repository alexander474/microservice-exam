
import React from "react";
import "./Header.css";
import {Col, Collapse, Nav, Navbar, NavbarBrand, NavbarToggler, Row} from "reactstrap";

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

    render() {
        return (
            <Row className={"header-row"}>
                <Col sm="12" md="12" lg="12" className={"header-row-col"}>
                    <Navbar className={"header-row-col-navbar"} light expand="md">
                        <NavbarToggler onClick={this.toggleNav}/>
                        <Collapse isOpen={this.state.isOpen} navbar>
                            <Nav className="ml-auto" navbar></Nav>
                        </Collapse>
                    </Navbar>
                </Col>
            </Row>
        );
    }
}
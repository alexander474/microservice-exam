import React from "react";
import { Link, withRouter } from "react-router-dom";
import {Col, Row} from "reactstrap";


export class HomePage extends React.Component {
    constructor(props) {
        super(props);
    }


    render() {
        const {isLoggedIn, isAdmin, user} = this.props;
        return (
            <div >
                <Row >
                    <Col md={4}>
                        Welcome to my page {isLoggedIn ? user.name : "You are not logged in"}
                    </Col>
                </Row>
            </div>
        );
    }
}

export default withRouter(HomePage);

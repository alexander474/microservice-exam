import React from "react";
import { Link, withRouter } from "react-router-dom";
import {Col, Row} from "reactstrap";
import UserCount from "../components/UserCount/UserCount";
import Post from "../components/post/Post";


export class HomePage extends React.Component {
    constructor(props) {
        super(props);
    }

    renderLoggedIn = () => {

        return(
            <div>
                You are logged in
                <Post/>
            </div>
        )
    };

    renderNotLoggedIn = () => {

        return(
            <div>
                You are not logged in
            </div>
        )
    };


    render() {
        const {isLoggedIn, isAdmin, user} = this.props;
        return (
            <div >
                <Row >
                    <Col md={12}>
                        <UserCount/>
                    </Col>
                </Row>
                <Row >
                    <Col md={12}>
                        {isLoggedIn ? this.renderLoggedIn() : this.renderNotLoggedIn()}
                    </Col>
                </Row>
            </div>
        );
    }
}

export default withRouter(HomePage);

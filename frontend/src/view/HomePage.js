import React from "react";
import { Link, withRouter } from "react-router-dom";
import {Col, Row} from "reactstrap";
import UserCount from "../components/UserCount/UserCount";
import Post from "../components/post/Post";
import PostForm from "../components/post/PostForm";

export class HomePage extends React.Component {
    constructor(props) {
        super(props);
    }

    renderLoggedIn = () => {
        const {isLoggedIn, isAdmin, user, userInformation} = this.props;
        let name = "";
        let email = "";
        if(userInformation !== null && userInformation !== undefined) {
            if (userInformation.name !== null && userInformation.name !== undefined) name += userInformation.name;
            if (userInformation.middlename !== null && userInformation.middlename !== undefined) name += " " + userInformation.middlename;
            if (userInformation.surname !== null && userInformation.surname !== undefined) name += " " + userInformation.surname;
            if (userInformation.email !== null && userInformation.email !== undefined) email = userInformation.email;
        }
        return(
            <div>
                <p>Currently logged in as: {name} - {email}</p>
                <br/>
                <PostForm/>
                <br/>
                <h1>Feed:</h1>
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
        const {isLoggedIn, isAdmin, user, userInformation} = this.props;
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

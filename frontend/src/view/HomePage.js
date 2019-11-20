import React from "react";
import { Link, withRouter } from "react-router-dom";
import {Col, Row} from "reactstrap";
import UserCount from "../components/UserCount/UserCount";
import Post from "../components/post/Post";
import PostForm from "../components/post/PostForm";
import FriendRequest from "../components/friendRequest/FriendRequest";

export class HomePage extends React.Component {
    constructor(props) {
        super(props);
    }

    renderLoggedIn = () => {
        const {isLoggedIn, isAdmin, user, userInformation, getUserInformation} = this.props;
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
                <Row>
                    <Col md={12}>
                        <p>Currently logged in as: {name} - {email}</p>
                    </Col>
                </Row>
                <Row>
                    <Col md={6}>
                        <PostForm/>
                        <br/>
                        <h1>Feed:</h1>
                        <Post/>
                    </Col>
                    <Col md={6}>
                        <FriendRequest getUserInformation={getUserInformation} userInformation={userInformation} user={user}/>
                    </Col>
                </Row>
            </div>
        )
    };

    renderNotLoggedIn = () => {
        return(
            <div>
                <Row>
                    <Col md={12}>
                        <p>You are not logged in and can therefore not se any posts</p>
                    </Col>
                </Row>
            </div>
        );
    };


    render() {
        const {isLoggedIn, isAdmin, user, userInformation, updateAuthUse} = this.props;
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

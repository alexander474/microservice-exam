import React from "react";
import {Badge, Button, Card, CardBody, CardSubtitle, CardText, CardTitle,} from "reactstrap";
import {requestHandler} from "../../utils/RequestHandler"

export default class FriendRequest extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            users: null
        };
    }

    async componentDidMount() {
        await this.getAllUsersBasicInformation()
    }


    onUsersChange = (users) => {
        this.setState({users})
    };

    getAllUsersBasicInformation = async () => {
        const {updateAuthUser, user} = this.props;
        if (user !== null) {
            await requestHandler.getAllUserBasicInformation().then(r => {
                console.log("UserBasicFetch: ", r);
                if (r.status === 200) {
                    this.onUsersChange(r.data.data.list)
                }
            }).catch(e => console.log("Error retrieving users!"))
        }
    };

    sendFriendRequest = async (id) => {
        const {updateAuthUser, user, getUserInformation} = this.props;
        await requestHandler.sendFriendRequest(user.name, id).then(r => {
            if (r.status === 200) {
                getUserInformation()
            }
        }).catch(e => console.log("Error retrieving posts!"))
    };

    approveFriendRequest = async (id) => {
        const {updateAuthUser, user, getUserInformation} = this.props;
        await requestHandler.approveFriendRequest(user.name, id).then(r => {
            if (r.status === 200) {
                getUserInformation()
            }
        }).catch(e => console.log("Error retrieving posts!"))
    };

    denyFriendRequest = async (id) => {
        const {updateAuthUser, user, getUserInformation} = this.props;
        await requestHandler.denyFriendRequest(user.name, id).then(r => {
            if (r.status === 200) {
                getUserInformation()
            }
        }).catch(e => console.log("Error retrieving posts!"))
    };

    renderUser = (user) => {
        const {userInformation} = this.props;
        if (userInformation !== null && user !== null) {
            let name = "";
            if (user !== undefined) {
                if (user.name !== null && user.name !== undefined) name += user.name;
                if (user.middlename !== null && user.middlename !== undefined) name += " " + user.middlename;
                if (user.surname !== null && user.surname !== undefined) name += " " + user.surname;
            }
            if (userInformation.userId !== user.userId && !userInformation.friends.includes(user.userId)) {
                return (
                    <CardBody>
                        <CardText>
                            {name}
                        </CardText>
                        <CardSubtitle>
                            <Button color="success" onClick={() => this.sendFriendRequest(user.userId)}>Add
                                friend</Button>
                        </CardSubtitle>
                    </CardBody>
                );
            } else if (userInformation.userId === user.userId) {
                return (
                    <CardBody>
                        <CardText>
                            {name} (YOU)
                        </CardText>
                    </CardBody>
                );
            } else if (userInformation.friends.includes(user.userId)) {
                return (
                    <CardBody>
                        <CardText>
                            {name} (Is your friend)
                        </CardText>
                    </CardBody>
                );
            } else return null
        } else return null
    };

    render() {
        const {isLoggedIn, user, userInformation} = this.props;
        const {users} = this.state;
        return (
            <div>
                <Card style={{marginTop: "1rem", marginBottom: "1rem"}}>
                    <p>All users <Badge color="info" onClick={() => this.getAllUsersBasicInformation()}>Reload</Badge>
                    </p>
                    {users !== null && users !== undefined ? users.map((u) => (<div>{this.renderUser(u)}</div>)) :
                        <CardBody><CardTitle>No users found</CardTitle></CardBody>}
                </Card>
                <Card style={{marginTop: "1rem", marginBottom: "1rem"}}>
                    <p>Friends</p>
                    {userInformation !== null && userInformation.requestsIn !== null ? userInformation.friends.map((p, i) => (
                        <CardBody>
                            <CardText>
                                You and the person with id: {p} is friends
                            </CardText>
                        </CardBody>
                    )) : <CardBody><CardTitle>No requests sent</CardTitle></CardBody>}
                </Card>
                <Card style={{marginTop: "1rem", marginBottom: "1rem"}}>
                    <p>Incoming requests</p>
                    {userInformation !== null && userInformation.requestsIn !== null ? userInformation.requestsIn.map((p, i) => (
                        <CardBody>
                            <CardText>
                                Person with userId: {p} has sent you a request <Button color="success"
                                                                                       onClick={() => this.approveFriendRequest(p)}>Approve</Button>
                                <Button color="danger" onClick={() => this.denyFriendRequest(p)}>Deny</Button>
                            </CardText>
                        </CardBody>
                    )) : <CardBody><CardTitle>No requests sent</CardTitle></CardBody>}
                </Card>
                <Card style={{marginTop: "1rem", marginBottom: "1rem"}}>
                    <p>Outgoing requests</p>
                    {userInformation !== null && userInformation.requestsOut !== null ? userInformation.requestsOut.map((p, i) => (
                        <CardBody>
                            <CardText>
                                You have sent a request to userId: {p}
                            </CardText>
                        </CardBody>
                    )) : <CardBody><CardTitle>No requests received</CardTitle></CardBody>}
                </Card>
            </div>
        );
    }
}
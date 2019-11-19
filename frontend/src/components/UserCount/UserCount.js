import React from "react";
import {Badge} from "reactstrap";
import {requestHandler} from "../../utils/RequestHandler"

export default class UserCount extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            count: 0
        };
    }

    async componentDidMount() {
        await this.userCount();
    }

    userCount = async () => {
        await requestHandler.getUserCount().then(r =>  {
            if(r.status) {
                this.setState({count: r.data.data})
            }
        }).catch(e => console.log("Error retrieving usercount!"))
    };

    render() {
        return (
            <div>
                <Badge onClick={()=>this.userCount()} color="secondary">Users: {this.state.count}</Badge>
            </div>
        );
    }
}
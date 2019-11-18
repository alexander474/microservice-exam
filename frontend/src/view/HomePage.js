import React from "react";
import { Link, withRouter } from "react-router-dom";


export class HomePage extends React.Component {
    constructor(props) {
        super(props);
    }


    render() {

        return (
            <div>
                <p>Welcome to my page</p>
            </div>
        );
    }
}

export default withRouter(HomePage);

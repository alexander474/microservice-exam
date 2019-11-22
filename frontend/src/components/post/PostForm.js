// https://github.com/arcuri82/web_development_and_api_design
import React from 'react';
import {withRouter} from 'react-router-dom';
import {requestHandler} from "../../utils/RequestHandler"
import {Button, Card, CardBody, CardTitle, Input, InputGroup, UncontrolledAlert} from "reactstrap";


export class PostForm extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            title: "",
            message: "",
            errorMsg: null
        };
    }

    onTitleChange = (event) => {
        this.setState({title: event.target.value});
    };

    onMessageChange = (event) => {
        this.setState({message: event.target.value});
    };


    createPost = async () => {
        const {title, message} = this.state;
        this.setState({errorMsg: null});

        if (title.length > 0 && message.length > 0) {
            requestHandler.createPost(title, message).then(res => {
                if (res.status === 201) {
                    this.props.history.push('/');
                } else if (res.data.message !== null) {
                    this.setState({errorMsg: res.data.message});
                }
            })
        } else {
            this.setState({errorMsg: "Pleas fill out the form"});
        }

    };


    render() {

        let error = <div></div>;
        if (this.state.errorMsg !== null) {
            error = <UncontrolledAlert color="danger">{this.state.errorMsg}</UncontrolledAlert>
        }


        return (
            <div>
                <Card>
                    <CardBody>
                        <CardTitle>Create a new post</CardTitle>
                        <InputGroup>
                            <Input type="text"
                                   id="titleInput"
                                   onChange={(e) => this.onTitleChange(e)}
                                   value={this.state.title}
                                   placeholder="title"/>
                        </InputGroup>
                        <InputGroup>
                            <Input type="text"
                                   id="messageInput"
                                   onChange={(e) => this.onMessageChange(e)}
                                   value={this.state.message}
                                   placeholder="message"/>
                        </InputGroup>

                        {error}

                        <Button onClick={() => this.createPost()} id="userUpdateBtn" color="primary">Post</Button>
                    </CardBody>
                </Card>
            </div>);
    }
}

export default withRouter(PostForm);

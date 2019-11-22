import React from "react";
import {Badge, Card, CardBody, CardSubtitle, CardText, CardTitle} from "reactstrap";
import {requestHandler} from "../../utils/RequestHandler"
import {readableTime} from "../../utils/UnixTranslate";

export default class Post extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            posts: null
        };
    }

    async componentDidMount() {
        await this.getPosts();
    }

    onPostsChange = (posts) => {
        this.setState({posts})
    };

    getPosts = async (uri) => {
        await requestHandler.getAllPosts(uri).then(r => {
            console.log(r);
            if (r.status === 200) {
                this.onPostsChange(r.data.data.list);
            } else {
                console.log("Could not retrieve posts!")
            }
        }).catch(e => console.log("Error retrieving posts!"))
    };

    render() {
        const {posts} = this.state;
        return (
            <div>
                <Badge onClick={() => this.getPosts()} color="secondary">Reload</Badge>
                {posts !== null ? posts.map((p, i) => (
                    <Card style={{marginTop: "1rem", marginBottom: "1rem"}}>
                        <CardBody>
                            <CardTitle>{p.userId !== null ? p.userId : "User"}: {p.title !== null ? p.title : "No title"}</CardTitle>
                            <CardSubtitle>Date: {p.date !== null ? readableTime(p.date, true) : "No date"}</CardSubtitle>
                            <CardText>{p.message !== null ? p.message : "No message"}</CardText>
                        </CardBody>
                    </Card>
                )) : <p>No posts found</p>}
            </div>
        );
    }
}
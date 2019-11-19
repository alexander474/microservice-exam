import React from "react";
import {
    Badge,
    Card,
    CardBody,
    CardSubtitle,
    CardText,
    CardTitle,
    Pagination,
    PaginationItem,
    PaginationLink
} from "reactstrap";
import {requestHandler} from "../../utils/RequestHandler"

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
        await requestHandler.getAllPosts(uri).then(r =>  {
            console.log(r);
            if(r.status === 200) {
                this.onPostsChange(r.data.data.list);
            }else{
                console.log("Could not retrieve posts!")
            }
        }).catch(e => console.log("Error retrieving posts!"))
    };

    render() {
        const { posts } = this.state;
        return (
            <div>
                { posts !== null ? posts.map((p,i) => (
                    <Card>
                        <CardBody>
                            <CardTitle>{p.userId !== null ? p.userId : "User"}: {p.title !== null ? p.title : "-"}</CardTitle>
                            <CardSubtitle>Date: {p.date !== null ? p.date : "-"}</CardSubtitle>
                            <CardText>{p.message !== null ? p.message : "-"}</CardText>
                        </CardBody>
                    </Card>
                )): <p>No posts found</p>}
            </div>
        );
    }
}
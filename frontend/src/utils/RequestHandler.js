import axios from 'axios';

const basePath = "api/v1";

const login = (username, password) => {
    return axios.post(basePath+'/auth/login',
        {
            'userId': username,
            'password': password
        }).then(response => {
        return response
    })
};

const signUp = (username, password) => {
    return axios.post(basePath+'/auth/signUp',
        {
            'userId': username,
            'password': password
        }).then(response => {
        return response
    })
};

const getAuthUser = () => {
    return axios.get(basePath+'/auth/user',).then(response => {
        return response
    })
};

const getUserInformation = (userId) => {
    return axios.get(basePath+'/users/'+userId).then(response => {
        return response
    })
};


export const requestHandler = {
    login,
    signUp,
    getAuthUser,
    getUserInformation
};
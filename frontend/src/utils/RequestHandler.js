import axios from 'axios';

const basePath = "api/v1";

const login = async (username, password) => {
    return await axios.post(basePath+'/auth/login',
        {
            'userId': username,
            'password': password
        }).then(response => {
        return response
    })
};

const signUp = async (username, password) => {
    return await axios.post(basePath+'/auth/signUp',
        {
            'userId': username,
            'password': password
        }).then(response => {
        return response
    })
};

const getAuthUser = async () => {
    return await axios.get(basePath+'/auth/user',).then(response => {
        return response
    })
};

const getUserInformation = async (userId) => {
    return await axios.get(basePath+'/users/'+userId).then(response => {
        return response
    })
};

const logout = async () => {
    return await axios.get(basePath+'/auth/logout').then(response => {
        return response
    })
};


export const requestHandler = {
    login,
    logout,
    signUp,
    getAuthUser,
    getUserInformation
};
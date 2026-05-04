import api, { formBody, unwrap, withToken } from './api'

export const getCurrentUser = () => unwrap(api.get('/user/getmessage'))

export const updateCurrentUser = (payload) => unwrap(api.put('/user/editmessage', formBody(payload)))

export const getAllUsers = () => unwrap(api.get('/user/getallmessage'))

export const addUser = (payload) => unwrap(api.post('/user/adduser', formBody(payload)))

export const updateUser = (payload) => unwrap(api.put('/user/editallmessage', formBody(payload)))

export const updateRegisterState = ({ username, registered }) => unwrap(api.put('/user/editregister', null, {
  params: withToken({ username, registered }),
}))

export const deleteUserByUsername = (username) => unwrap(api.put('/user/deleteuser', null, {
  params: withToken({ username }),
}))

export const getUserProfile = () => unwrap(api.get('/userprofile/getuserprofile'))

export const updateUserProfile = (payload) => unwrap(api.put('/userprofile/edituserprofile', null, {
  params: withToken(payload),
}))

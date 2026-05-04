import api, { unwrap, withToken } from './api'

export const getSplitMode = (daytime) => unwrap(api.get('/fitnessplan/getsplitmode', {
  params: withToken({ daytime }),
}))

export const getActionTasks = (daytime) => unwrap(api.get('/fitnessplan/getactiontask', {
  params: withToken({ daytime }),
}))

export const getTodayCheckin = () => unwrap(api.get('/checkin/today'))

export const getCheckinStats = () => unwrap(api.get('/checkin/stats'))

export const saveTodayCheckin = (payload) => unwrap(api.put('/checkin/today', null, {
  params: payload,
}))

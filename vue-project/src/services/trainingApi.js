import api, { formBody, unwrap, withToken } from './api'

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

export const getPlanTaskRecords = (daytime) => unwrap(api.get('/fitnessplan/taskrecords', {
  params: withToken({ daytime }),
}))

export const savePlanTaskRecord = (payload) => unwrap(api.put('/fitnessplan/taskrecord', formBody(payload)))

export const getTrainingPlanInsight = () => unwrap(api.get('/fitnessplan/insight'))

export const getTrainingCycle = () => unwrap(api.get('/fitnessplan/cycle'))

export const getPlanAdjustments = () => unwrap(api.get('/fitnessplan/adjustments'))

export const replacePlanAction = (payload) => unwrap(api.get('/fitnessplan/action/replace', {
  params: withToken(payload),
}))

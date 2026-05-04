import api, { formBody, unwrap } from './api'

export const getNutritionRecommendation = () => unwrap(api.get('/nutrition/recommendation'))

export const getNutritionPreference = () => unwrap(api.get('/nutrition/preference'))

export const updateNutritionPreference = (payload) => unwrap(api.put('/nutrition/preference', formBody(payload)))

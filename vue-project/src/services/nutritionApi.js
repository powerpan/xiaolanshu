import api, { unwrap } from './api'

export const getNutritionRecommendation = () => unwrap(api.get('/nutrition/recommendation'))

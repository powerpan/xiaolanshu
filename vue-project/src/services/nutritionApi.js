import api, { formBody, unwrap } from './api'

export const getNutritionRecommendation = () => unwrap(api.get('/nutrition/recommendation'))

export const getNutritionLibrary = () => unwrap(api.get('/nutrition/library'))

export const getAdminNutritionLibrary = () => unwrap(api.get('/nutrition/admin/library'))

export const addFoodItem = (payload) => unwrap(api.post('/nutrition/admin/fooditem', formBody(payload)))

export const updateFoodItem = (payload) => unwrap(api.put('/nutrition/admin/fooditem', formBody(payload)))

export const deleteFoodItem = (id) => unwrap(api.delete('/nutrition/admin/fooditem', { params: { id } }))

export const addMealTemplate = (payload) => unwrap(api.post('/nutrition/admin/mealtemplate', formBody(payload)))

export const updateMealTemplate = (payload) => unwrap(api.put('/nutrition/admin/mealtemplate', formBody(payload)))

export const deleteMealTemplate = (id) => unwrap(api.delete('/nutrition/admin/mealtemplate', { params: { id } }))

export const addFoodReplacement = (payload) => unwrap(api.post('/nutrition/admin/foodreplacement', formBody(payload)))

export const updateFoodReplacement = (payload) => unwrap(api.put('/nutrition/admin/foodreplacement', formBody(payload)))

export const deleteFoodReplacement = (id) => unwrap(api.delete('/nutrition/admin/foodreplacement', { params: { id } }))

export const addEatingScenario = (payload) => unwrap(api.post('/nutrition/admin/eatingscenario', formBody(payload)))

export const updateEatingScenario = (payload) => unwrap(api.put('/nutrition/admin/eatingscenario', formBody(payload)))

export const deleteEatingScenario = (id) => unwrap(api.delete('/nutrition/admin/eatingscenario', { params: { id } }))

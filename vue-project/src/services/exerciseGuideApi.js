import api, { formBody, unwrap, withToken } from './api'

export const getExerciseGuide = ({ actionPattern, equipment }) => unwrap(api.get('/exerciseguide/getexerciseguide', {
  params: withToken({ actionPattern, equipment }),
}))

export const listExerciseGuides = ({
  actionPattern,
  equipment,
  missingImageOnly,
  incompleteOnly,
  missingStepsOnly,
  missingTipsOnly,
  missingMistakesOnly,
  missingAlternativesOnly,
} = {}) => unwrap(api.get('/exerciseguide/list', {
  params: withToken({
    actionPattern: actionPattern || undefined,
    equipment: equipment || undefined,
    missingImageOnly: missingImageOnly || undefined,
    incompleteOnly: incompleteOnly || undefined,
    missingStepsOnly: missingStepsOnly || undefined,
    missingTipsOnly: missingTipsOnly || undefined,
    missingMistakesOnly: missingMistakesOnly || undefined,
    missingAlternativesOnly: missingAlternativesOnly || undefined,
  }),
}))

export const listExerciseAlternatives = (payload) => unwrap(api.get('/exerciseguide/alternatives', {
  params: withToken(payload),
}))

export const addExerciseGuide = (payload) => unwrap(api.post('/exerciseguide/addguide', formBody(payload)))

export const updateExerciseGuide = (payload) => unwrap(api.put('/exerciseguide/editguide', formBody(payload)))

export const deleteExerciseGuide = (id) => unwrap(api.put('/exerciseguide/deleteguide', null, {
  params: withToken({ id }),
}))

export const uploadExerciseGuideImage = (file) => {
  const body = new FormData()
  body.append('file', file)
  return unwrap(api.post('/exerciseguide/uploadimage', body))
}

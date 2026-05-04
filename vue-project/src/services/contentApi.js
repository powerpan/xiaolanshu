import api, { unwrap, withToken } from './api'

export const getNotices = () => unwrap(api.get('/notice/getnotice'))

export const getNoticeDetail = (id) => unwrap(api.get('/notice/getnoticedetail', {
  params: withToken({ id }),
}))

export const addNotice = (payload) => unwrap(api.put('/notice/addnotice', null, {
  params: withToken(payload),
}))

export const updateNotice = (payload) => unwrap(api.put('/notice/editnotice', null, {
  params: withToken(payload),
}))

export const deleteNoticeById = (id) => unwrap(api.put('/notice/deletenotice', null, {
  params: withToken({ id }),
}))

export const saveNoticeDraftApi = (payload) => unwrap(api.put('/notice/savetempnotice', null, {
  params: withToken(payload),
}))

export const getNoticeDrafts = () => unwrap(api.get('/notice/gettempnotice'))

export const deleteNoticeDrafts = () => unwrap(api.put('/notice/deletetempnotice', null, {
  params: withToken(),
}))

export const getArticles = ({ title = '', topic = '' } = {}) => unwrap(api.get('/article/getarticle', {
  params: withToken({ title, topic }),
}))

export const getMyArticles = () => unwrap(api.get('/article/getmyarticle', {
  params: withToken(),
}))

export const getArticleDetail = (id) => unwrap(api.get('/article/getarticledetail', {
  params: withToken({ id }),
}))

export const addArticleApi = (payload) => unwrap(api.put('/article/addarticle', null, {
  params: withToken(payload),
}))

export const updateArticle = (payload) => unwrap(api.put('/article/editarticle', null, {
  params: withToken(payload),
}))

export const deleteArticleById = (payload) => unwrap(api.put('/article/deletearticle', null, {
  params: withToken(payload),
}))

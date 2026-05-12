import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

export async function createShortUrl(payload) {
  const { data } = await api.post('/shorten', payload)
  return data
}

export async function getStats(shortCode) {
  const { data } = await api.get(`/stats/${encodeURIComponent(shortCode)}`)
  return data
}


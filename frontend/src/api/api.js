import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_BASE_URL || ''

const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const getBestSlotInfo = (stationId) => {
  return api.get(`/api/stations/${stationId}/best-slot`)
}

export const initiateRental = (data) =>
  api.post('/api/rentals/initiate', data)

export const createPaymentOrder = (data) =>
  api.post('/api/payments/create-order', data)

export const verifyPayment = (data) =>
  api.post('/api/payments/verify', data)

export default api

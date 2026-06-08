import { Routes, Route, Navigate } from 'react-router-dom'
import RentalPage from './pages/RentalPage'
import SuccessPage from './pages/SuccessPage'
import ErrorPage from './pages/ErrorPage'

function App() {
  return (
    <Routes>
      <Route path="/rent" element={<RentalPage />} />
      <Route path="/success" element={<SuccessPage />} />
      <Route path="/error" element={<ErrorPage />} />
      <Route path="*" element={<Navigate to="/error" replace />} />
    </Routes>
  )
}

export default App

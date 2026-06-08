import { useState, useEffect } from 'react'
import { useSearchParams, useNavigate } from 'react-router-dom'
import { getBestSlotInfo, initiateRental, createPaymentOrder, verifyPayment } from '../api/api'
import UserModal from '../components/UserModal'
import SlotCard from '../components/SlotCard'
import PricingCard from '../components/PricingCard'
import { toast } from 'react-hot-toast'

export default function RentalPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()

  const stationId = searchParams.get('deviceId') || searchParams.get('qrcode') || searchParams.get('station')

  const [slotInfo, setSlotInfo] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showModal, setShowModal] = useState(false)
  const [processing, setProcessing] = useState(false)

  useEffect(() => {
    if (!stationId) {
      navigate('/error?reason=invalid_qr')
      return
    }
    fetchSlotInfo()
  }, [stationId])

  const fetchSlotInfo = async () => {
    try {
      setLoading(true)
      const { data } = await getBestSlotInfo(stationId)
      setSlotInfo(data)
      if (data.status !== 'AVAILABLE') {
        navigate(`/error?reason=slot_unavailable&station=${stationId}`)
      }
    } catch (err) {
      if (err.response?.data?.error) {
        setError(err.response.data.error)
      } else {
        setError('Could not load station info. Please try scanning again.')
      }
    } finally {
      setLoading(false)
    }
  }

  const handleProceed = () => {
    setShowModal(true)
  }

  const handleUserConfirm = async (userDetails) => {
    setShowModal(false)
    setProcessing(true)
    try {
      // 1. Initiate rental
      const rentalRes = await initiateRental({
        stationId: slotInfo.stationId,
        slotNumber: slotInfo.slotNumber,
        userPhone: userDetails.phone,
        userEmail: userDetails.email,
        userName: userDetails.name,
        amount: slotInfo.depositAmount,
        currency: slotInfo.currency || 'INR',
      })
      const rental = rentalRes.data

      // 2. Create Razorpay order
      const orderRes = await createPaymentOrder({
        rentalId: rental.rentalId,
        stationId: slotInfo.stationId,
        slotNumber: slotInfo.slotNumber,
        amount: slotInfo.depositAmount,
        currency: slotInfo.currency || 'INR',
      })
      const order = orderRes.data

      // 3. Open Razorpay checkout
      openRazorpay(order, rental, userDetails)
    } catch (err) {
      toast.error('Something went wrong. Please try again.')
      setProcessing(false)
    }
  }

  const openRazorpay = (order, rental, userDetails) => {
    const options = {
      key: order.keyId,
      amount: Math.round(order.amount * 100),
      currency: order.currency,
      name: 'Watt\\'Ever PowerBank',
      description: `Power Bank Rental — ${slotInfo.stationName}, Slot ${slotInfo.slotNumber}`,
      image: '/logo.svg',
      order_id: order.orderId,
      prefill: {
        name: userDetails.name || '',
        email: userDetails.email || '',
        contact: userDetails.phone || '',
      },
      theme: {
        color: '#6C63FF',
      },
      modal: {
        ondismiss: () => {
          toast('Payment cancelled.', { icon: 'ℹ️' })
          setProcessing(false)
        },
      },
      handler: async (response) => {
        try {
          const verifyRes = await verifyPayment({
            rentalId: rental.rentalId,
            stationId: slotInfo.stationId,
            slotNumber: slotInfo.slotNumber,
            razorpayOrderId: response.razorpay_order_id,
            razorpayPaymentId: response.razorpay_payment_id,
            razorpaySignature: response.razorpay_signature,
          })

          const result = verifyRes.data
          if (result.success) {
            navigate(`/success?payment=${response.razorpay_payment_id}&station=${slotInfo.stationName}&slot=${slotInfo.slotNumber}`)
          } else {
            navigate(`/error?reason=payment_failed`)
          }
        } catch (err) {
          navigate(`/error?reason=payment_failed`)
        }
        setProcessing(false)
      },
    }

    if (typeof window.Razorpay === 'undefined') {
      toast.error('Payment gateway not loaded. Please refresh.')
      setProcessing(false)
      return
    }

    const rzp = new window.Razorpay(options)
    rzp.on('payment.failed', () => {
      toast.error('Payment failed. Please try again.')
      setProcessing(false)
    })
    rzp.open()
  }

  if (loading) return <LoadingScreen />
  if (error) return <ErrorScreen message={error} />
  if (!slotInfo) return null

  return (
    <div className="page-wrapper">
      <div style={{ width: '100%', maxWidth: '440px', position: 'relative', zIndex: 1 }}>
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div className="animate-float" style={{ display: 'inline-block', marginBottom: '16px' }}>
            <PowerBankIcon />
          </div>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px', marginBottom: '8px' }}>
            <span style={{ color: 'var(--primary-light)', fontSize: '13px', fontWeight: '700', letterSpacing: '2px', textTransform: 'uppercase' }}>Watt'Ever</span>
          </div>
          <h1 style={{ fontSize: '28px', fontWeight: '800', color: 'var(--text-primary)', marginBottom: '8px', lineHeight: '1.2' }}>
            PowerBank Rental
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '15px' }}>
            Charge up, pay later, go anywhere
          </p>
        </div>

        {/* Slot Card */}
        <SlotCard slotInfo={slotInfo} />

        {/* Pricing Card */}
        <PricingCard slotInfo={slotInfo} />

        {/* CTA */}
        <div style={{ marginTop: '20px' }}>
          <button
            className="btn-primary"
            onClick={handleProceed}
            disabled={processing || slotInfo.status !== 'AVAILABLE'}
            id="btn-proceed-payment"
          >
            {processing ? (
              <><div className="spinner" /> Processing...</>
            ) : (
              <>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
                  <line x1="1" y1="10" x2="23" y2="10"/>
                </svg>
                Proceed to Payment
              </>
            )}
          </button>
          <p style={{ textAlign: 'center', fontSize: '12px', color: 'var(--text-muted)', marginTop: '12px' }}>
            🔒 Secured by Razorpay · UPI, Cards, NetBanking
          </p>
        </div>

        {/* Footer */}
        <div style={{ textAlign: 'center', marginTop: '28px', color: 'var(--text-muted)', fontSize: '12px' }}>
          Need help? Contact support@wattever.in
        </div>
      </div>

      {showModal && (
        <UserModal
          onConfirm={handleUserConfirm}
          onClose={() => setShowModal(false)}
        />
      )}
    </div>
  )
}

function LoadingScreen() {
  return (
    <div className="page-wrapper">
      <div style={{ textAlign: 'center', zIndex: 1 }}>
        <div style={{ marginBottom: '24px' }}>
          <div className="animate-float">
            <PowerBankIcon />
          </div>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', color: 'var(--text-secondary)' }}>
          <div className="spinner" />
          <span>Loading station info...</span>
        </div>
      </div>
    </div>
  )
}

function ErrorScreen({ message }) {
  return (
    <div className="page-wrapper">
      <div className="glass-card" style={{ padding: '32px', maxWidth: '400px', textAlign: 'center' }}>
        <p style={{ color: 'var(--danger)', marginBottom: '12px', fontSize: '17px', fontWeight: '600' }}>⚠️ Error</p>
        <p style={{ color: 'var(--text-secondary)' }}>{message}</p>
      </div>
    </div>
  )
}

function PowerBankIcon() {
  return (
    <div style={{
      width: '72px',
      height: '72px',
      borderRadius: '20px',
      background: 'linear-gradient(135deg, rgba(108, 99, 255, 0.3), rgba(0, 212, 170, 0.2))',
      border: '1px solid rgba(108, 99, 255, 0.4)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      animation: 'pulse-glow 2s ease-in-out infinite',
    }}>
      <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#6C63FF" strokeWidth="1.5">
        <rect x="2" y="7" width="16" height="11" rx="2"/>
        <path d="M22 11v3" strokeLinecap="round"/>
        <path d="M7 10v4M10 12H7" strokeLinecap="round" strokeLinejoin="round" stroke="#00D4AA" strokeWidth="2"/>
      </svg>
    </div>
  )
}

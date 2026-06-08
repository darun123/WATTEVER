import { useSearchParams, useNavigate } from 'react-router-dom'

const errorMessages = {
  invalid_qr: {
    title: 'Invalid QR Code',
    message: 'This QR code is not valid. Please scan the QR code on the power bank station.',
    icon: '📷',
  },
  slot_unavailable: {
    title: 'Slot Unavailable',
    message: 'This slot is currently occupied or under maintenance. Please try a different slot.',
    icon: '🔒',
  },
  payment_failed: {
    title: 'Payment Failed',
    message: 'Your payment could not be processed. No charges have been made. Please try again.',
    icon: '💳',
  },
  invalid: {
    title: 'Something Went Wrong',
    message: 'An unexpected error occurred. Please scan the QR code again.',
    icon: '⚠️',
  },
}

export default function ErrorPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const reason = searchParams.get('reason') || 'invalid'
  const stationParam = searchParams.get('station')

  const error = errorMessages[reason] || errorMessages.invalid

  return (
    <div className="page-wrapper">
      <div style={{ width: '100%', maxWidth: '440px', position: 'relative', zIndex: 1, textAlign: 'center' }}>

        <div style={{ marginBottom: '32px' }}>
          <div style={{
            width: '90px', height: '90px',
            borderRadius: '50%',
            background: 'rgba(255, 77, 109, 0.1)',
            border: '2px solid rgba(255, 77, 109, 0.3)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            margin: '0 auto 24px',
            fontSize: '40px',
          }}>
            {error.icon}
          </div>
          <h1 style={{ fontSize: '26px', fontWeight: '800', color: 'var(--text-primary)', marginBottom: '12px' }}>
            {error.title}
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '15px', lineHeight: '1.6', maxWidth: '320px', margin: '0 auto' }}>
            {error.message}
          </p>
        </div>

        <div className="glass-card" style={{ padding: '24px' }}>
          <p style={{ color: 'var(--text-muted)', fontSize: '13px', marginBottom: '16px' }}>
            Need help? Contact our support team
          </p>
          <a
            href="mailto:support@wattever.in"
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '8px',
              padding: '12px 24px',
              background: 'rgba(108, 99, 255, 0.15)',
              border: '1px solid rgba(108, 99, 255, 0.3)',
              borderRadius: '10px',
              color: 'var(--primary-light)',
              textDecoration: 'none',
              fontSize: '14px',
              fontWeight: '600',
              transition: 'all 0.2s ease',
            }}
          >
            ✉️ support@wattever.in
          </a>
        </div>

        <div style={{ marginTop: '24px', color: 'var(--text-muted)', fontSize: '12px' }}>
          <p>Watt'Ever PowerBank Rental System</p>
        </div>
      </div>
    </div>
  )
}

import { useSearchParams, useNavigate } from 'react-router-dom'
import { useEffect, useRef } from 'react'

export default function SuccessPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const paymentId = searchParams.get('payment')
  const station = searchParams.get('station')
  const slot = searchParams.get('slot')
  const confettiRef = useRef(false)

  useEffect(() => {
    if (!paymentId) {
      navigate('/error?reason=invalid')
    }
  }, [paymentId])

  const truncateId = (id) => id ? `...${id.slice(-8)}` : ''

  return (
    <div className="page-wrapper">
      <div style={{ width: '100%', maxWidth: '440px', position: 'relative', zIndex: 1 }}>

        {/* Success animation */}
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <div style={{ position: 'relative', display: 'inline-block', marginBottom: '24px' }}>
            <div style={{
              width: '100px',
              height: '100px',
              borderRadius: '50%',
              background: 'linear-gradient(135deg, rgba(0, 212, 170, 0.2), rgba(0, 212, 170, 0.05))',
              border: '2px solid rgba(0, 212, 170, 0.4)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              animation: 'pulse-glow 2s ease-in-out infinite',
            }}>
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
                <polyline
                  points="20 6 9 17 4 12"
                  stroke="#00D4AA"
                  strokeWidth="2.5"
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  style={{
                    strokeDasharray: 50,
                    strokeDashoffset: 0,
                    animation: 'checkmark 0.6s ease 0.2s both',
                  }}
                />
              </svg>
            </div>
          </div>

          <h1 style={{ fontSize: '30px', fontWeight: '800', marginBottom: '10px', color: 'var(--text-primary)' }}>
            Power Bank Released! 🎉
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '16px', lineHeight: '1.5' }}>
            Your power bank has been released. Grab it and go!
          </p>
        </div>

        {/* Release animation card */}
        <div className="glass-card" style={{
          padding: '24px',
          marginBottom: '20px',
          background: 'linear-gradient(135deg, rgba(0, 212, 170, 0.08), rgba(0, 212, 170, 0.03))',
          border: '1px solid rgba(0, 212, 170, 0.2)',
        }}>
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '48px', marginBottom: '12px', animation: 'float 2s ease-in-out infinite' }}>⚡</div>
            <p style={{ fontWeight: '700', fontSize: '18px', color: 'var(--accent)', marginBottom: '6px' }}>
              Slot #{slot} is now open
            </p>
            <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>
              Please collect your power bank within 2 minutes
            </p>
          </div>
        </div>

        {/* Receipt */}
        <div className="glass-card" style={{ padding: '20px', marginBottom: '20px' }}>
          <h3 style={{ fontSize: '13px', fontWeight: '600', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '16px' }}>
            Payment Receipt
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <ReceiptRow label="Station" value={station || 'N/A'} />
            <ReceiptRow label="Slot" value={`#${slot || 'N/A'}`} />
            <ReceiptRow label="Payment ID" value={truncateId(paymentId)} mono />
            <ReceiptRow label="Status" value="✅ Paid" accent />
          </div>
        </div>

        {/* Help text */}
        <div style={{
          background: 'rgba(255, 179, 71, 0.06)',
          border: '1px solid rgba(255, 179, 71, 0.15)',
          borderRadius: '12px',
          padding: '16px',
          display: 'flex',
          gap: '12px',
          alignItems: 'flex-start',
        }}>
          <span style={{ fontSize: '20px', flexShrink: 0 }}>ℹ️</span>
          <p style={{ color: 'var(--text-secondary)', fontSize: '13px', lineHeight: '1.5' }}>
            Return the power bank to any WaterX station when done. Billing continues until returned.
          </p>
        </div>

      </div>
    </div>
  )
}

function ReceiptRow({ label, value, mono, accent }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>{label}</span>
      <span style={{
        fontSize: '13px',
        fontWeight: '600',
        color: accent ? 'var(--accent)' : 'var(--text-primary)',
        fontFamily: mono ? 'monospace' : 'inherit',
      }}>{value}</span>
    </div>
  )
}

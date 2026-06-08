import { useState } from 'react'

export default function UserModal({ onConfirm, onClose }) {
  const [mode, setMode] = useState('choose') // 'choose' | 'guest' | 'login'
  const [form, setForm] = useState({ name: '', phone: '', email: '' })
  const [loading, setLoading] = useState(false)

  const handleGuestContinue = async () => {
    setLoading(true)
    // Small delay for UX
    await new Promise(r => setTimeout(r, 300))
    setLoading(false)
    onConfirm({ name: form.name, phone: form.phone, email: form.email })
  }

  return (
    <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal-content">
        {mode === 'choose' && (
          <>
            <div style={{ textAlign: 'center', marginBottom: '28px' }}>
              <div style={{
                width: '56px', height: '56px', borderRadius: '16px',
                background: 'linear-gradient(135deg, rgba(108,99,255,0.3), rgba(0,212,170,0.2))',
                border: '1px solid rgba(108,99,255,0.3)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                margin: '0 auto 16px',
              }}>
                <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#6C63FF" strokeWidth="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                  <circle cx="12" cy="7" r="4"/>
                </svg>
              </div>
              <h2 style={{ fontSize: '22px', fontWeight: '700', marginBottom: '8px' }}>How to continue?</h2>
              <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Choose how you'd like to proceed with your rental</p>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <button
                id="btn-login"
                className="btn-primary"
                onClick={() => setMode('login')}
              >
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                  <polyline points="10 17 15 12 10 7"/>
                  <line x1="15" y1="12" x2="3" y2="12"/>
                </svg>
                Log In to Account
              </button>

              <div className="divider">or</div>

              <button
                id="btn-guest"
                className="btn-ghost"
                onClick={() => setMode('guest')}
              >
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                  <circle cx="9" cy="7" r="4"/>
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                Continue as Guest
              </button>
            </div>
          </>
        )}

        {mode === 'guest' && (
          <>
            <button onClick={() => setMode('choose')} style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '14px', padding: 0 }}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="15 18 9 12 15 6"/>
              </svg>
              Back
            </button>
            <h2 style={{ fontSize: '20px', fontWeight: '700', marginBottom: '6px' }}>Guest Checkout</h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '13px', marginBottom: '24px' }}>We'll send your receipt to the details below</p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '20px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '6px', fontWeight: '500' }}>Name (optional)</label>
                <input
                  id="input-guest-name"
                  className="input-field"
                  placeholder="Your name"
                  value={form.name}
                  onChange={e => setForm(f => ({...f, name: e.target.value}))}
                />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '6px', fontWeight: '500' }}>Phone Number (optional)</label>
                <input
                  id="input-guest-phone"
                  className="input-field"
                  placeholder="+91 98765 43210"
                  type="tel"
                  value={form.phone}
                  onChange={e => setForm(f => ({...f, phone: e.target.value}))}
                />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '6px', fontWeight: '500' }}>Email (optional)</label>
                <input
                  id="input-guest-email"
                  className="input-field"
                  placeholder="you@example.com"
                  type="email"
                  value={form.email}
                  onChange={e => setForm(f => ({...f, email: e.target.value}))}
                />
              </div>
            </div>

            <button
              id="btn-guest-pay"
              className="btn-primary"
              onClick={handleGuestContinue}
              disabled={loading}
            >
              {loading ? <><div className="spinner" /> Processing...</> : 'Continue to Payment'}
            </button>
          </>
        )}

        {mode === 'login' && (
          <>
            <button onClick={() => setMode('choose')} style={{ background: 'none', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '14px', padding: 0 }}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <polyline points="15 18 9 12 15 6"/>
              </svg>
              Back
            </button>
            <h2 style={{ fontSize: '20px', fontWeight: '700', marginBottom: '6px' }}>Log In</h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '13px', marginBottom: '24px' }}>Enter your credentials to continue</p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '20px' }}>
              <div>
                <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '6px', fontWeight: '500' }}>Phone / Email</label>
                <input
                  id="input-login-phone"
                  className="input-field"
                  placeholder="Phone or email"
                  value={form.phone}
                  onChange={e => setForm(f => ({...f, phone: e.target.value}))}
                />
              </div>
              <div>
                <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '6px', fontWeight: '500' }}>Password</label>
                <input
                  id="input-login-password"
                  className="input-field"
                  placeholder="••••••••"
                  type="password"
                />
              </div>
            </div>

            <button
              id="btn-login-submit"
              className="btn-primary"
              onClick={() => {
                // For now, proceed as guest with phone captured
                onConfirm({ name: form.name, phone: form.phone, email: form.email })
              }}
            >
              Log In & Pay
            </button>

            <div className="divider" style={{ margin: '16px 0' }}>or</div>

            <button className="btn-ghost" onClick={() => setMode('guest')}>Continue as Guest</button>
          </>
        )}
      </div>
    </div>
  )
}

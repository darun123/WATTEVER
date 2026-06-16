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
            <h2 style={{ fontSize: '20px', fontWeight: '700', marginBottom: '6px' }}>Log In / Sign Up</h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '13px', marginBottom: '24px' }}>Choose a method to continue</p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '20px' }}>
              
              <button className="social-btn">
                <svg width="20" height="20" viewBox="0 0 48 48">
                  <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.7 17.74 9.5 24 9.5z"/>
                  <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/>
                  <path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/>
                  <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/>
                </svg>
                Continue with Google
              </button>

              <button className="social-btn">
                <svg width="20" height="20" viewBox="0 0 384 512" fill="currentColor">
                  <path d="M318.7 268.7c-.2-36.7 16.4-64.4 50-84.8-18.8-26.9-47.2-41.7-84.7-44.6-35.5-2.8-74.3 20.7-88.5 20.7-15 0-49.4-19.7-76.4-19.7C63.3 141.2 4 184.8 4 273.5q0 39.3 14.4 81.2c12.8 36.7 59 126.7 107.2 125.2 25.2-.6 43-17.9 75.8-17.9 31.8 0 48.3 17.9 76.4 17.9 48.6-.7 90.4-82.5 102.6-119.3-65.2-30.7-61.7-90-61.7-91.9zm-56.6-164.2c27.3-32.4 24.8-61.9 24-72.5-24.1 1.4-52 16.4-67.9 34.9-17.5 19.8-27.8 44.3-25.6 71.9 26.1 2 49.9-11.4 69.5-34.3z"/>
                </svg>
                Continue with Apple
              </button>

              <button className="social-btn" style={{ borderColor: 'rgba(37, 211, 102, 0.3)', background: 'rgba(37, 211, 102, 0.05)' }}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="#25D366">
                  <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51a12.8 12.8 0 0 0-.57-.01c-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 0 1-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 0 1-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 0 1 2.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0 0 12.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 0 0 5.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 0 0-3.48-8.413Z"/>
                </svg>
                Continue with WhatsApp
              </button>

              <div className="divider" style={{ margin: '8px 0' }}>or</div>

              <div>
                <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '6px', fontWeight: '500' }}>Mobile Number</label>
                <div style={{ display: 'flex', gap: '8px' }}>
                  <input
                    className="input-field"
                    placeholder="+91"
                    style={{ width: '70px', textAlign: 'center' }}
                    defaultValue="+91"
                  />
                  <input
                    id="input-login-phone"
                    className="input-field"
                    placeholder="Enter your number"
                    type="tel"
                    value={form.phone}
                    onChange={e => setForm(f => ({...f, phone: e.target.value}))}
                  />
                </div>
              </div>
            </div>

            <button
              id="btn-login-submit"
              className="btn-primary"
              onClick={() => {
                onConfirm({ name: form.name, phone: form.phone, email: form.email })
              }}
            >
              Log In & Pay
            </button>
          </>
        )}
      </div>
    </div>
  )
}

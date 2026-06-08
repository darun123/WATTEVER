export default function PricingCard({ slotInfo }) {
  const formatCurrency = (amount, currency = 'INR') => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency,
      minimumFractionDigits: 0,
    }).format(amount)
  }

  return (
    <div className="glass-card animate-fade-in-up" style={{
      padding: '24px',
      animationDelay: '0.1s',
      opacity: 0,
      animationFillMode: 'forwards',
    }}>
      <h3 style={{ fontSize: '14px', fontWeight: '600', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '20px' }}>
        Rental Pricing
      </h3>

      {/* Main price */}
      <div style={{
        textAlign: 'center',
        padding: '24px',
        background: 'linear-gradient(135deg, rgba(108, 99, 255, 0.12), rgba(0, 212, 170, 0.08))',
        borderRadius: '16px',
        border: '1px solid rgba(108, 99, 255, 0.2)',
        marginBottom: '20px',
      }}>
        <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginBottom: '8px' }}>Refundable Deposit</p>
        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'center', gap: '4px' }}>
          <span style={{ fontSize: '42px', fontWeight: '800', color: 'var(--text-primary)', lineHeight: 1 }}>
            {formatCurrency(slotInfo.depositAmount, slotInfo.currency)}
          </span>
        </div>
        <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '8px' }}>
          Usage ({formatCurrency(slotInfo.pricePerHour, slotInfo.currency)}/hr) will be deducted from the deposit, and the rest will be refunded upon return.
        </p>
      </div>

      {/* Features */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {[
          { icon: '⚡', text: 'Instant power bank release' },
          { icon: '🔄', text: 'Return at any Watt\\'Ever station' },
          { icon: '🛡️', text: '100% secure payment' },
        ].map((item, i) => (
          <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ fontSize: '18px' }}>{item.icon}</span>
            <span style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>{item.text}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

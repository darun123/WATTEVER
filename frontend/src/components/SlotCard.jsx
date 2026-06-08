export default function SlotCard({ slotInfo }) {
  return (
    <div className="glass-card animate-fade-in-up" style={{
      padding: '24px',
      marginBottom: '16px',
    }}>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: '16px' }}>
        <div style={{ flex: 1 }}>
          <h2 style={{ fontSize: '18px', fontWeight: '700', color: 'var(--text-primary)', marginBottom: '4px' }}>
            {slotInfo.stationName}
          </h2>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-secondary)', fontSize: '13px' }}>
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
              <circle cx="12" cy="10" r="3"/>
            </svg>
            {slotInfo.location}
          </div>
        </div>
        <span className={`badge ${slotInfo.status === 'AVAILABLE' ? 'badge-available' : 'badge-unavailable'}`}>
          <span style={{ width: '6px', height: '6px', borderRadius: '50%', background: 'currentColor', display: 'inline-block' }} />
          {slotInfo.status === 'AVAILABLE' ? 'Available' : slotInfo.status}
        </span>
      </div>

      <div style={{
        display: 'grid',
        gridTemplateColumns: '1fr 1fr',
        gap: '12px',
      }}>
        <InfoTile
          icon={
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M20.91 8.84L8.56 2.23a2 2 0 0 0-1.12 0L4 3.54"/>
              <path d="m22 10-1.09 6.53A2 2 0 0 1 19 18H5a2 2 0 0 1-1.91-1.47L2 10"/>
              <rect x="2" y="7" width="20" height="5" rx="2"/>
            </svg>
          }
          label="Station ID"
          value={slotInfo.stationId}
        />
        <InfoTile
          icon={
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <rect x="2" y="7" width="16" height="11" rx="2"/>
              <path d="M22 11v3"/>
            </svg>
          }
          label="Slot Number"
          value={`#${slotInfo.slotNumber}`}
        />
      </div>
    </div>
  )
}

function InfoTile({ icon, label, value }) {
  return (
    <div style={{
      background: 'rgba(255,255,255,0.03)',
      border: '1px solid rgba(255,255,255,0.07)',
      borderRadius: '12px',
      padding: '12px',
    }}>
      <div style={{ color: 'var(--primary-light)', marginBottom: '4px' }}>{icon}</div>
      <p style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: '2px' }}>{label}</p>
      <p style={{ fontSize: '15px', fontWeight: '600', color: 'var(--text-primary)' }}>{value}</p>
    </div>
  )
}

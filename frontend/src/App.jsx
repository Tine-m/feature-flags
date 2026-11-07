import React, { useEffect, useState } from 'react';
import DiscountCodeForm from './DiscountCodeForm.jsx';

export default function App() {
  const [total, setTotal] = useState(null);
  const [features, setFeatures] = useState({ discountCodes: false });
  const [error, setError] = useState('');
  useEffect(() => {
    let active = true;
    fetch('/api/orders/preview').then(r => {
      if (!r.ok) throw new Error();
      return r.json();
    }).then(order => { if (active) setTotal(order.total); })
      .catch(() => { if (active) setError('Bestillingen kunne ikke hentes.'); });
    fetch('/api/features').then(r => {
      if (!r.ok) throw new Error();
      return r.json();
    }).then(config => { if (active) setFeatures({ discountCodes: config.discountCodes === true }); })
      .catch(() => { /* Sikker standard: behold flaget slået fra. */ });
    return () => { active = false; };
  }, []);
  return <main>
    <p className="eyebrow">Campus Kitchen</p><h1>Din bestilling</h1>
    <div className="order">2 × dagens menu</div>
    {error && <p role="alert">{error}</p>}
    {features.discountCodes && total !== null && <DiscountCodeForm onApplied={setTotal} />}
    <p className="total">{total === null ? 'Henter pris…' : `I alt: ${total.toFixed(2)} kr.`}</p>
  </main>;
}

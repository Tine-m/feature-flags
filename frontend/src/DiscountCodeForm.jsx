import React, { useState } from 'react';

export default function DiscountCodeForm({ onApplied }) {
  const [code, setCode] = useState('');
  const [error, setError] = useState('');
  const [pending, setPending] = useState(false);
  async function submit(event) {
    event.preventDefault(); setError(''); setPending(true);
    try {
      const response = await fetch('/api/orders/discount-preview', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ code })
      });
      if (!response.ok) {
        setError(response.status === 400 ? 'Ugyldig rabatkode.' : 'Der opstod en serverfejl. Prøv igen.');
        return;
      }
      const preview = await response.json();
      onApplied(preview.total);
    } catch { setError('Kunne ikke kontakte serveren.'); }
    finally { setPending(false); }
  }
  return <form onSubmit={submit}>
    <label htmlFor="discount-code">Rabatkode</label>
    <input id="discount-code" value={code} onChange={event => setCode(event.target.value)} />
    <button disabled={pending || !code.trim()}>{pending ? 'Kontrollerer…' : 'Anvend rabatkode'}</button>
    {error && <p role="alert">{error}</p>}
  </form>;
}

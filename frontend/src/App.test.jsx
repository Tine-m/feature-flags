import React from 'react';
import { afterEach, expect, test, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import App from './App.jsx';
afterEach(() => { cleanup(); vi.unstubAllGlobals(); });
function mockApi(enabled, status = 200, featureFailure = false) {
  vi.stubGlobal('fetch', vi.fn(async (url) => {
    if (url === '/api/features') {
      if (featureFailure) throw new Error('offline');
      return { ok: true, json: async () => ({ discountCodes: enabled }) };
    }
    if (url === '/api/orders/preview') return { ok: true, json: async () => ({ total: 200 }) };
    return { ok: status === 200, status, json: async () => ({ total: 180 }) };
  }));
}
test('slået fra: eksisterende bestilling virker uden rabatfelt', async () => {
  mockApi(false); render(<App />);
  expect(await screen.findByText('I alt: 200.00 kr.')).toBeInTheDocument();
  expect(screen.queryByLabelText('Rabatkode')).not.toBeInTheDocument();
});
test('slået til: gyldig kode opdaterer prisen', async () => {
  mockApi(true); render(<App />);
  fireEvent.change(await screen.findByLabelText('Rabatkode'), { target: { value: 'STUDENT10' } });
  fireEvent.click(screen.getByRole('button', { name: 'Anvend rabatkode' }));
  expect(await screen.findByText('I alt: 180.00 kr.')).toBeInTheDocument();
  expect(fetch).toHaveBeenCalledWith('/api/orders/discount-preview', expect.objectContaining({ body: '{"code":"STUDENT10"}' }));
});
test.each([[400, 'Ugyldig rabatkode.'], [403, 'Rabatkoder er ikke aktiveret.']])('viser fejl fra API (%s)', async (status, message) => {
  mockApi(true, status); render(<App />);
  fireEvent.change(await screen.findByLabelText('Rabatkode'), { target: { value: 'WRONG' } });
  fireEvent.click(screen.getByRole('button', { name: 'Anvend rabatkode' }));
  expect(await screen.findByRole('alert')).toHaveTextContent(message);
  expect(screen.getByText('I alt: 200.00 kr.')).toBeInTheDocument();
});
test('fejl i konfiguration skjuler funktionen uden at blokere bestillingen', async () => {
  mockApi(false, 200, true); render(<App />);
  expect(await screen.findByText('I alt: 200.00 kr.')).toBeInTheDocument();
  expect(screen.queryByLabelText('Rabatkode')).not.toBeInTheDocument();
});

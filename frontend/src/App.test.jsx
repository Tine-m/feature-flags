import React from 'react';
import { afterEach, expect, test, vi } from 'vitest';
import { cleanup, fireEvent, render, screen } from '@testing-library/react';
import App from './App.jsx';
afterEach(() => { cleanup(); vi.unstubAllGlobals(); });
function mockApi(status = 200) {
  vi.stubGlobal('fetch', vi.fn(async (url) => {
    if (url === '/api/orders/preview') return { ok: true, json: async () => ({ total: 200 }) };
    return { ok: status === 200, status, json: async () => ({ total: 180 }) };
  }));
}
test('slået til: gyldig kode opdaterer prisen', async () => {
  mockApi(); render(<App />);
  fireEvent.change(await screen.findByLabelText('Rabatkode'), { target: { value: 'STUDENT10' } });
  fireEvent.click(screen.getByRole('button', { name: 'Anvend rabatkode' }));
  expect(await screen.findByText('I alt: 180.00 kr.')).toBeInTheDocument();
  expect(fetch).toHaveBeenCalledWith('/api/orders/discount-preview', expect.objectContaining({ body: '{"code":"STUDENT10"}' }));
});
test.each([[400, 'Ugyldig rabatkode.']])('viser fejl fra API (%s)', async (status, message) => {
  mockApi(status); render(<App />);
  fireEvent.change(await screen.findByLabelText('Rabatkode'), { target: { value: 'WRONG' } });
  fireEvent.click(screen.getByRole('button', { name: 'Anvend rabatkode' }));
  expect(await screen.findByRole('alert')).toHaveTextContent(message);
  expect(screen.getByText('I alt: 200.00 kr.')).toBeInTheDocument();
});

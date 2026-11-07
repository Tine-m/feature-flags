import React from 'react';
import { afterEach, expect, test, vi } from 'vitest';
import { cleanup, render, screen } from '@testing-library/react';
import App from './App.jsx';
afterEach(() => { cleanup(); vi.unstubAllGlobals(); });
test('viser den eksisterende bestilling', async () => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: true, json: async () => ({ total: 200 }) }));
  render(<App />);
  expect(await screen.findByText('I alt: 200.00 kr.')).toBeInTheDocument();
  expect(screen.queryByLabelText('Rabatkode')).not.toBeInTheDocument();
});

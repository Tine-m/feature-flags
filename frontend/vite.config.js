import { defineConfig } from 'vite';
export default defineConfig({
  server: { proxy: { '/api': 'http://localhost:7070' } },
  test: { environment: 'jsdom', setupFiles: ['./src/test-setup.js'] }
});

// Street Runner 3D - service worker (offline support)
// When you update the game, change the CACHE name (v1 -> v2) so the old cache is cleared.
const CACHE = 'sr3d-v1';
const THREE = 'https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js';
const SHELL = ['./', './index.html', './manifest.webmanifest', './icons/icon-192.png', './icons/icon-512.png', './icons/icon-maskable-512.png', './icons/apple-touch-icon.png'];

self.addEventListener('install', e => {
  e.waitUntil((async () => {
    const c = await caches.open(CACHE);
    await Promise.allSettled([...SHELL, THREE].map(u => c.add(u)));
    await self.skipWaiting();
  })());
});

self.addEventListener('activate', e => {
  e.waitUntil((async () => {
    const keys = await caches.keys();
    await Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k)));
    await self.clients.claim();
  })());
});

self.addEventListener('fetch', e => {
  const r = e.request;
  if (r.method !== 'GET') return;
  const url = new URL(r.url);
  // Page: network first (so you get the newest version), cache when offline
  if (r.mode === 'navigate') {
    e.respondWith((async () => {
      try {
        const res = await fetch(r);
        const c = await caches.open(CACHE); c.put('./index.html', res.clone());
        return res;
      } catch (_) {
        return (await caches.match('./index.html')) || (await caches.match('./')) || Response.error();
      }
    })());
    return;
  }
  // three.js and other files: cache first, otherwise fetch from the network and cache it
  if (url.origin === location.origin || url.href === THREE) {
    e.respondWith((async () => {
      const hit = await caches.match(r, { ignoreSearch: true });
      if (hit) return hit;
      try {
        const res = await fetch(r);
        if (res && (res.ok || res.type === 'opaque')) { const c = await caches.open(CACHE); c.put(r, res.clone()); }
        return res;
      } catch (_) { return Response.error(); }
    })());
  }
});

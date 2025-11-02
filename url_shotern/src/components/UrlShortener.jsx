import React, { useState } from 'react';
import axios from 'axios';
import { FaQrcode, FaCopy } from 'react-icons/fa';

export default function UrlShortener() {
  const [longUrl, setLongUrl] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showQr, setShowQr] = useState(false);

  async function handleShorten(e) {
    e && e.preventDefault();
    setError('');
    if (!longUrl || !longUrl.trim()) {
      setError('Please enter a URL');
      return;
    }
    setLoading(true);
    try {
      const resp = await axios.post('/api/shorten', { originalUrl: longUrl });
      setShortUrl(resp.data.shortUrl);
      setShowQr(false);
    } catch (err) {
      console.error(err);
      setError(err?.response?.data?.message || 'Failed to shorten URL');
    } finally {
      setLoading(false);
    }
  }

  function qrUrl() {
    if (!shortUrl) return '';
    const token = shortUrl.split('/').pop();
    return `/api/qrcode/${token}?size=300`;
  }

  function normalizeLink(link) {
    if (!link) return '';
    if (link.startsWith('http://') || link.startsWith('https://')) return link;
    return 'http://' + link;
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-6">
      <div className="w-full max-w-4xl bg-white rounded-lg shadow-md p-10">
        <h1 className="text-3xl md:text-4xl font-bold text-center mb-8">Tiny URL — Shortener</h1>

        <form onSubmit={handleShorten} className="flex flex-col md:flex-row gap-4 mb-4 items-stretch">
          <input
            type="url"
            placeholder="Enter long URL (https://...)"
            value={longUrl}
            onChange={e => setLongUrl(e.target.value)}
            className="flex-1 px-6 py-4 text-lg border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
          />
          <button type="submit" disabled={loading} className="w-full md:w-auto px-6 py-3 bg-blue-600 text-white rounded-lg text-lg hover:bg-blue-700 disabled:opacity-60">
            {loading ? 'Shortening...' : 'Shorten'}
          </button>
        </form>

        {error && <div className="text-sm text-red-600 mb-3">{error}</div>}

        {shortUrl && (
          <div className="border p-6 rounded-md bg-gray-50 shadow-inner">
            <div className="flex flex-col md:flex-row items-center gap-4">
              <input readOnly value={shortUrl} className="flex-1 px-4 py-3 border rounded-md bg-white text-lg" />
              <div className="flex items-center gap-3">
                <a href={normalizeLink(shortUrl)} target="_blank" rel="noreferrer" className="text-blue-600">Open</a>
                <button onClick={() => { navigator.clipboard?.writeText(shortUrl) }} className="text-gray-600 hover:text-blue-600" title="Copy"><FaCopy size={18} /></button>
                <button onClick={() => setShowQr(s => !s)} className="text-gray-600 hover:text-blue-600" title="Toggle QR"><FaQrcode size={18} /></button>
              </div>
            </div>

            {showQr && (
              <div className="mt-6 text-center">
                <img src={qrUrl()} alt="qr" className="w-64 h-64 mx-auto" />
                <div className="text-center mt-3">
                  <a href={qrUrl()} download={`qr-${shortUrl.split('/').pop()}.png`} className="text-sm text-blue-600">Download QR</a>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

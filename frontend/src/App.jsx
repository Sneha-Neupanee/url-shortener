import { useEffect, useMemo, useState } from 'react'
import { createShortUrl, getStats } from './lib/api'

function extractShortCode(shortUrl) {
  try {
    const u = new URL(shortUrl)
    return u.pathname.replace('/', '')
  } catch {
    return ''
  }
}

export default function App() {
  const [longUrl, setLongUrl] = useState('')
  const [customAlias, setCustomAlias] = useState('')
  const [shortUrl, setShortUrl] = useState('')
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [copied, setCopied] = useState(false)

  const shortCode = useMemo(() => (shortUrl ? extractShortCode(shortUrl) : ''), [shortUrl])

  useEffect(() => {
    setCopied(false)
  }, [shortUrl])

  async function handleShorten(e) {
    e.preventDefault()
    setError('')
    setStats(null)
    setShortUrl('')
    setLoading(true)
    try {
      const res = await createShortUrl({
        longUrl,
        customAlias: customAlias.trim() ? customAlias.trim() : undefined,
      })
      setShortUrl(res.shortUrl)
      const code = extractShortCode(res.shortUrl)
      if (code) {
        const s = await getStats(code)
        setStats(s)
      }
    } catch (err) {
      setError(err?.response?.data?.error || 'Something went wrong')
    } finally {
      setLoading(false)
    }
  }

  async function refreshStats() {
    if (!shortCode) return
    setError('')
    try {
      const s = await getStats(shortCode)
      setStats(s)
    } catch (err) {
      setError(err?.response?.data?.error || 'Failed to load stats')
    }
  }

  async function handleCopy() {
    if (!shortUrl) return
    try {
      await navigator.clipboard.writeText(shortUrl)
      setCopied(true)
      window.setTimeout(() => setCopied(false), 1500)
    } catch {
      setError('Failed to copy')
    }
  }

  return (
    <div className="min-h-full bg-gradient-to-b from-slate-50 to-slate-100">
      <div className="mx-auto flex min-h-full max-w-2xl items-center px-4 py-12">
        <div className="w-full rounded-2xl border border-slate-200 bg-white p-6 shadow-sm sm:p-10">
          <div className="mb-8">
            <h1 className="text-2xl font-semibold tracking-tight text-slate-900">
              URL Shortener
            </h1>
            <p className="mt-2 text-sm text-slate-600">
              Paste a long URL, optionally reserve a custom alias, and get a short link with click
              stats.
            </p>
          </div>

          <form onSubmit={handleShorten} className="space-y-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Long URL</label>
              <input
                value={longUrl}
                onChange={(e) => setLongUrl(e.target.value)}
                placeholder="https://example.com/some/very/long/path"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-400 focus:ring-4 focus:ring-slate-100"
                required
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">
                Custom alias (optional)
              </label>
              <input
                value={customAlias}
                onChange={(e) => setCustomAlias(e.target.value)}
                placeholder="my-link (3–30 chars: a-z A-Z 0-9 _ -)"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-400 focus:ring-4 focus:ring-slate-100"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-lg bg-slate-900 px-4 py-2.5 text-sm font-medium text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? 'Shortening...' : 'Shorten URL'}
            </button>
          </form>

          {error ? (
            <div className="mt-4 rounded-lg border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">
              {error}
            </div>
          ) : null}

          {shortUrl ? (
            <div className="mt-6 rounded-xl border border-slate-200 bg-slate-50 p-4">
              <div className="text-sm font-medium text-slate-700">Short URL</div>
              <a
                className="mt-1 block break-all text-sm font-semibold text-indigo-700 underline decoration-indigo-300 underline-offset-4 hover:text-indigo-800"
                href={shortUrl}
                target="_blank"
                rel="noreferrer"
              >
                {shortUrl}
              </a>
              <div className="mt-3 flex flex-wrap gap-2">
                <button
                  type="button"
                  onClick={handleCopy}
                  className="rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50"
                >
                  {copied ? 'Copied' : 'Copy'}
                </button>
                <button
                  type="button"
                  onClick={refreshStats}
                  className="rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50"
                >
                  Refresh stats
                </button>
              </div>
            </div>
          ) : null}

          {stats ? (
            <div className="mt-6 grid gap-3 sm:grid-cols-3">
              <div className="rounded-xl border border-slate-200 bg-white p-4">
                <div className="text-xs font-medium text-slate-500">Clicks</div>
                <div className="mt-1 text-2xl font-semibold text-slate-900">
                  {stats.clickCount}
                </div>
              </div>
              <div className="rounded-xl border border-slate-200 bg-white p-4 sm:col-span-2">
                <div className="text-xs font-medium text-slate-500">Original URL</div>
                <div className="mt-1 break-all text-sm font-medium text-slate-800">
                  {stats.originalUrl}
                </div>
                <div className="mt-2 text-xs text-slate-500">
                  Created: {new Date(stats.createdAt).toLocaleString()}
                </div>
              </div>
            </div>
          ) : null}
        </div>
      </div>
    </div>
  )
}

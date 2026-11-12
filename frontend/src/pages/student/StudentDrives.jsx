import { useState, useCallback } from 'react'
import DashboardLayout from '../../layouts/DashboardLayout'
import { studentApi } from '../../api/services'
import { useInfiniteScroll } from '../../hooks/useInfiniteScroll'

const GRADS = [
  'from-indigo-600 to-indigo-800',
  'from-violet-600 to-violet-800',
  'from-slate-700 to-slate-900',
  'from-blue-600 to-blue-800',
  'from-emerald-600 to-emerald-800',
]

export default function StudentDrives() {
  const [applying, setApplying] = useState('')
  const [appliedIds, setAppliedIds] = useState(() => new Set())
  const [toast, setToast] = useState(null)

  const showToast = (m, type = 'success') => {
    setToast({ m, type })
    setTimeout(() => setToast(null), 3500)
  }

  const fetchDrives = useCallback((page, size) => studentApi.getEligibleDrives(page, size), [])

  const {
    items: rawDrives = [],
    loading,
    error,
    hasMore,
    sentinelRef,
    reset,
  } = useInfiniteScroll(fetchDrives, 15)

  const drives = rawDrives.filter(d => !appliedIds.has(d.driveId))

  const handleApply = async (driveId) => {
    setApplying(driveId)
    try {
      await studentApi.applyDrive(driveId)
      setAppliedIds(prev => new Set([...prev, driveId]))
      showToast('Applied successfully!')
      reset()
    } catch (err) {
      showToast(err?.response?.data?.message || 'Failed to apply', 'error')
    } finally {
      setApplying('')
    }
  }

  return (
    <DashboardLayout>
      <div className="p-8 space-y-4">
        <h2 className="text-2xl font-semibold text-gray-800">Eligible Drives</h2>

        {error && <div className="p-4 bg-red-50 text-red-600 rounded-xl border border-red-200 text-sm">{error}</div>}

        {loading && drives.length === 0 ? (
          <div className="p-12 text-center text-gray-400">Loading drives...</div>
        ) : drives.length === 0 ? (
          <div className="p-12 text-center bg-white rounded-2xl border border-gray-200 text-gray-500">
            No eligible drives available at this moment.
          </div>
        ) : (
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {drives.map((drive) => {
              const index =
                drive?.driveId && drive.driveId.length >= 4
                  ? drive.driveId.charCodeAt(3)
                  : 0
              const grad = GRADS[Math.abs(index) % GRADS.length]
              const company = drive.driveInfo?.companyName || drive?.driveName || drive?.driveId || 'Company'
              const role = drive.driveInfo?.role || drive?.jobRole || '—'
              const pkg = drive.driveInfo?.packageAmount ?? drive?.packageOffered
              const location = drive.driveInfo?.jobLocation || drive?.jobLocation || 'N/A'
              const deadline = drive.driveInfo?.registrationEndDate || drive?.registrationEndDate || 'N/A'

              return (
                <div
                  key={drive.driveId}
                  className="bg-white rounded-2xl border border-gray-200 overflow-hidden shadow-sm hover:shadow-md transition"
                >
                  <div className={`bg-gradient-to-r ${grad} p-5 text-white`}>
                    <h3 className="text-lg font-bold">{company}</h3>
                    <p className="text-white/80 text-sm">{role}</p>
                  </div>

                  <div className="p-5 space-y-2 text-sm text-gray-600">
                    <p className="font-semibold text-emerald-600">{pkg != null ? `${pkg} LPA` : 'N/A'}</p>
                    <p className="text-xs text-gray-400 font-mono">Drive: {drive.driveId}</p>
                    <p className="text-xs text-gray-400">📍 {location}</p>
                    <p className="text-xs text-gray-400">📅 Ends: {deadline}</p>

                    <button
                      onClick={() => handleApply(drive.driveId)}
                      disabled={applying === drive.driveId}
                      className="w-full mt-3 px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-full text-xs transition disabled:opacity-50"
                    >
                      {applying === drive.driveId ? 'Applying...' : 'Apply Now'}
                    </button>
                  </div>
                </div>
              )
            })}
          </div>
        )}

        {hasMore && (
          <div ref={sentinelRef} className="h-10 flex items-center justify-center text-xs text-gray-400">
            {loading && <span>Loading more...</span>}
          </div>
        )}
      </div>

      {toast && (
        <div className={`fixed bottom-6 right-6 px-5 py-3 rounded-xl text-sm flex items-center gap-2.5 shadow-xl z-50 text-white ${toast.type === 'error' ? 'bg-red-700' : 'bg-gray-900'}`}>
          {toast.m}
        </div>
      )}
    </DashboardLayout>
  )
}
import { useState, useCallback, useEffect } from 'react'
import { Link } from 'react-router-dom'
import DashboardLayout from '../../layouts/DashboardLayout'
import { useFetch } from '../../hooks/useFetch'
import { studentApi } from '../../api/services'
import { useAuth } from '../../context/AuthContext'

function StatCard({ label, value, color, icon }) {
  const colors = {
    indigo: 'bg-indigo-50 text-indigo-600',
    emerald: 'bg-emerald-50 text-emerald-600',
    amber: 'bg-amber-50 text-amber-600',
    rose: 'bg-rose-50 text-rose-600',
  }
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5 flex items-center gap-4">
      <div className={`p-3 rounded-lg ${colors[color] || colors.indigo}`}>{icon}</div>
      <div>
        <p className="text-xs text-gray-400 font-medium">{label}</p>
        <p className="text-2xl font-semibold text-gray-800 mt-0.5">
          {value == null ? <span className="w-10 h-6 bg-gray-100 rounded animate-pulse inline-block" /> : value}
        </p>
      </div>
    </div>
  )
}

const STATUS_COLOR = {
  APPLIED: 'bg-indigo-400', INPROCESS: 'bg-amber-400',
  SELECTED: 'bg-emerald-500', REJECTED: 'bg-red-400',
}

export default function StudentDashboard() {
  const { user } = useAuth()

  // Profile — not paginated
  const { data: profile } = useFetch(studentApi.getProfile)

  // Applications — first page only for dashboard preview; totalElements gives the count
  const [appPage,       setAppPage]       = useState(null)  // Spring Page object
  const [eligiblePage,  setEligiblePage]  = useState(null)

  useEffect(() => {
    studentApi.getApplications(0, 10).then(res => {
      const body = res.data?.data ?? res.data
      setAppPage(body)
    }).catch(() => {})

    studentApi.getEligibleDrives(0, 1).then(res => {
      // size=1 — we only need totalElements, not the full list
      const body = res.data?.data ?? res.data
      setEligiblePage(body)
    }).catch(() => {})
  }, [])

  const applications   = appPage?.content ?? []
  const totalApplied   = appPage?.totalElements ?? null
  const eligibleCount  = eligiblePage?.totalElements ?? null

  // Derive status counts from first page (good enough for dashboard)
  const inProcess = applications.filter(a => a.status === 'INPROCESS').length
  const selected  = applications.filter(a => a.status === 'SELECTED').length
  const activeApps = applications.filter(a => a.status !== 'ELIGIBLE').slice(0, 4)

  return (
    <DashboardLayout>
      <div className="p-8 space-y-6">
        {/* Banner */}
        <div className="bg-gradient-to-r from-indigo-600 to-violet-700 rounded-2xl p-6 flex items-center justify-between overflow-hidden relative">
          <div className="absolute -right-8 -top-8 w-40 h-40 bg-white/5 rounded-full" />
          <div className="absolute -right-2 -bottom-10 w-56 h-56 bg-white/5 rounded-full" />
          <div className="relative z-10">
            <p className="text-indigo-200 text-xs font-semibold uppercase tracking-widest">Welcome back</p>
            <h2 className="text-white text-2xl font-bold mt-0.5">
              {user?.email?.split('@')[0]?.toUpperCase() || 'Student'}
            </h2>
            <p className="text-indigo-200 text-sm mt-1">{profile?.department || '—'} · Sem {profile?.currentSemester || '—'}</p>
          </div>
          <div className="relative z-10 text-right hidden md:block">
            <p className="text-indigo-200 text-xs">Current CGPA</p>
            <p className="text-white text-4xl font-bold mt-0.5">{profile?.currentCgpa || '—'}</p>
            <p className="text-indigo-200 text-xs mt-0.5">Batch of {profile?.passingYear || '—'}</p>
          </div>
        </div>

        {/* Stats — counts from totalElements (correct even across pages) */}
        <div className="grid grid-cols-2 xl:grid-cols-4 gap-4">
          <StatCard label="Eligible Drives" value={eligibleCount} color="indigo"
            icon={<svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/></svg>}
          />
          <StatCard label="Applied" value={totalApplied} color="emerald"
            icon={<svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>}
          />
          <StatCard label="In Process" value={appPage ? inProcess : null} color="amber"
            icon={<svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>}
          />
          <StatCard label="Selected" value={appPage ? selected : null} color="emerald"
            icon={<svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>}
          />
        </div>

        <div className="grid xl:grid-cols-2 gap-5">
          {/* Application Timeline (first page preview) */}
          <div className="bg-white rounded-2xl border border-gray-200 p-5">
            <div className="flex items-center justify-between mb-5">
              <h3 className="font-semibold text-gray-800 text-sm">Application Timeline</h3>
              <Link to="/student/applications" className="text-xs text-indigo-500 hover:underline">View all</Link>
            </div>
            {activeApps.length === 0 ? (
              <p className="text-gray-400 text-sm text-center py-6">No applications yet.</p>
            ) : (
              <div className="space-y-3">
                {activeApps.map((a, i) => {
                  const dot = STATUS_COLOR[a.status] || 'bg-gray-300'
                  return (
                    <div key={a.id || i} className="flex items-start gap-3">
                      <div className="flex flex-col items-center">
                        <div className={`w-3 h-3 rounded-full mt-0.5 shrink-0 ${dot}`} />
                        {i < activeApps.length - 1 && <div className="w-0.5 h-8 bg-gray-100 mt-1" />}
                      </div>
                      <div className="flex-1 pb-1">
                        <div className="flex items-center justify-between">
                          <p className="text-sm font-semibold text-gray-800">{a.driveInfo?.companyName || a.driveId}</p>
                          <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                            a.status === 'SELECTED'  ? 'bg-emerald-50 text-emerald-700' :
                            a.status === 'INPROCESS' ? 'bg-amber-50 text-amber-700' :
                            a.status === 'REJECTED'  ? 'bg-red-50 text-red-600' : 'bg-indigo-50 text-indigo-700'
                          }`}>{a.status}</span>
                        </div>
                        <p className="text-xs text-gray-400 mt-0.5">{a.driveInfo?.role || '—'} · Round {a.currentRound || 0}</p>
                      </div>
                    </div>
                  )
                })}
              </div>
            )}
          </div>

          {/* Quick Links */}
          <div className="grid grid-cols-2 gap-4 content-start">
            <Link to="/student/drives" className="bg-white rounded-2xl border border-gray-200 p-5 hover:shadow-md hover:border-indigo-200 transition group">
              <div className="w-10 h-10 bg-indigo-50 rounded-xl flex items-center justify-center text-indigo-600 mb-3 group-hover:bg-indigo-100 transition">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/></svg>
              </div>
              <h3 className="font-semibold text-gray-800 text-sm">Eligible Drives</h3>
              <p className="text-xs text-gray-400 mt-1">
                {eligibleCount != null ? `${eligibleCount} drives available` : '—'}
              </p>
            </Link>
            <Link to="/student/applications" className="bg-white rounded-2xl border border-gray-200 p-5 hover:shadow-md hover:border-indigo-200 transition group">
              <div className="w-10 h-10 bg-indigo-50 rounded-xl flex items-center justify-center text-indigo-600 mb-3 group-hover:bg-indigo-100 transition">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/></svg>
              </div>
              <h3 className="font-semibold text-gray-800 text-sm">My Applications</h3>
              <p className="text-xs text-gray-400 mt-1">
                {totalApplied != null ? `${totalApplied} total` : '—'}
              </p>
            </Link>
            <Link to="/student/profile" className="bg-white rounded-2xl border border-gray-200 p-5 hover:shadow-md hover:border-indigo-200 transition group col-span-2">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-indigo-50 rounded-xl flex items-center justify-center text-indigo-600 group-hover:bg-indigo-100 transition shrink-0">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
                </div>
                <div>
                  <h3 className="font-semibold text-gray-800 text-sm">My Profile</h3>
                  <p className="text-xs text-gray-400 mt-0.5">Update academic details and upload resume</p>
                </div>
              </div>
            </Link>
          </div>
        </div>
      </div>
    </DashboardLayout>
  )
}
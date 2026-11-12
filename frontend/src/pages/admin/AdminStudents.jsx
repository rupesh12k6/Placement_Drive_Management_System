import { useState, useCallback, useRef } from 'react'
import DashboardLayout from '../../layouts/DashboardLayout'
import { adminApi, authApi } from '../../api/services'
import { useInfiniteScroll } from '../../hooks/useInfiniteScroll'

// ── Sample CSV Template ───────────────────────────────────────────
const SAMPLE_CSV = `rollNo,name,surname,email,mobileNo,password
21B91A0501,Rahul,Sharma,21b91a0501@anits.edu.in,9876543210,pass1234
21B91A0502,Priya,Verma,21b91a0502@anits.edu.in,9876543211,pass1234
21B91A0503,Anil,Kumar,21b91a0503@anits.edu.in,9876543212,pass1234
21B91A0504,Sneha,Patel,21b91a0504@anits.edu.in,9876543213,pass1234`

function downloadCsvTemplate() {
  const blob = new Blob([SAMPLE_CSV], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'students_template.csv'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

function parseCsv(text, defaultPassword = 'pass1234') {
  const lines = text.split(/\r\n|\n/).map(l => l.trim()).filter(Boolean)
  if (lines.length < 2) {
    return { rows: [], error: 'CSV file must contain a header row and at least one student row.' }
  }

  const splitRow = (row) => {
    const result = []
    let current = ''
    let insideQuotes = false
    for (let i = 0; i < row.length; i++) {
      const char = row[i]
      if (char === '"') {
        insideQuotes = !insideQuotes
      } else if (char === ',' && !insideQuotes) {
        result.push(current.trim())
        current = ''
      } else {
        current += char
      }
    }
    result.push(current.trim())
    return result
  }

  const rawHeaders = splitRow(lines[0]).map(h => h.toLowerCase().replace(/[\s_\-]/g, ''))
  const headerMap = {}
  rawHeaders.forEach((h, idx) => {
    if (h.includes('roll')) headerMap.rollNo = idx
    else if (h === 'name' || h === 'firstname' || h === 'fname') headerMap.name = idx
    else if (h === 'surname' || h === 'lastname' || h === 'lname') headerMap.surname = idx
    else if (h.includes('email') || h.includes('mail')) headerMap.email = idx
    else if (h.includes('mobile') || h.includes('phone') || h.includes('contact')) headerMap.mobileNo = idx
    else if (h.includes('pass') || h.includes('pwd')) headerMap.password = idx
  })

  if (headerMap.rollNo === undefined || headerMap.name === undefined || headerMap.email === undefined) {
    return { rows: [], error: 'CSV must include rollNo, name, and email columns.' }
  }

  const parsed = []
  for (let i = 1; i < lines.length; i++) {
    const cols = splitRow(lines[i])
    if (!cols || cols.every(c => !c)) continue

    const rollNo = (cols[headerMap.rollNo] || '').toUpperCase()
    const name = cols[headerMap.name] || ''
    const surname = headerMap.surname !== undefined ? (cols[headerMap.surname] || '.') : '.'
    const email = (cols[headerMap.email] || '').toLowerCase()
    const mobileNo = headerMap.mobileNo !== undefined ? (cols[headerMap.mobileNo] || '') : ''
    const password = (headerMap.password !== undefined && cols[headerMap.password]) ? cols[headerMap.password] : defaultPassword

    const errors = []
    if (!rollNo) errors.push('Missing Roll No')
    else if (rollNo.length !== 12) errors.push('Roll No must be 12 chars')
    if (!name) errors.push('Missing Name')
    if (!email) errors.push('Missing Email')
    else if (!email.endsWith('@anits.edu.in')) errors.push('Must be @anits.edu.in')
    if (!/^[6-9]\d{9}$/.test(mobileNo)) errors.push('Invalid 10-digit mobile')
    if (password.length < 8) errors.push('Password < 8 chars')

    parsed.push({
      rollNo,
      name,
      surname,
      email,
      mobileNo,
      password,
      isValid: errors.length === 0,
      errors
    })
  }

  return { rows: parsed, error: null }
}

// ── PDF Preview Modal ─────────────────────────────────────────────
function ResumePreviewModal({ student, resumeUrl, onClose }) {
  const filename = `${student?.rollNo}_resume.pdf`

  const handleDownload = async () => {
    if (!resumeUrl) return
    try {
      const response = await fetch(resumeUrl)
      const blob = await response.blob()
      const blobUrl = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = blobUrl
      a.download = filename
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(blobUrl)
    } catch {
      window.open(resumeUrl, '_blank')
    }
  }

  return (
    <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-[70] p-4" onClick={onClose}>
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl flex flex-col" style={{ maxHeight: '92vh' }} onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 shrink-0">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold text-sm">
              {(student?.name || '?')[0]}
            </div>
            <div>
              <p className="font-semibold text-gray-800 text-sm">{student?.name} {student?.surname}</p>
              <p className="text-xs text-gray-400 font-mono">{student?.rollNo} · Resume Preview</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            {resumeUrl && (
              <button onClick={handleDownload}
                className="flex items-center gap-1.5 px-3 py-1.5 text-xs bg-emerald-50 text-emerald-600 rounded-full hover:bg-emerald-100 font-medium transition">
                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/>
                </svg>
                Download ({filename})
              </button>
            )}
            <button onClick={onClose} className="p-1.5 hover:bg-gray-100 rounded-lg text-gray-400 transition">
              <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12"/>
              </svg>
            </button>
          </div>
        </div>
        <div className="flex-1 overflow-hidden rounded-b-2xl" style={{ minHeight: '520px' }}>
          {resumeUrl ? (
            <iframe
              src={`${resumeUrl}#toolbar=1&navpanes=0&scrollbar=1`}
              className="w-full h-full"
              style={{ minHeight: '520px', border: 'none' }}
              title={`${student?.name} Resume`}
            />
          ) : (
            <div className="flex flex-col items-center justify-center h-64 text-center px-6">
              <p className="text-gray-500 font-medium text-sm">No resume uploaded</p>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default function AdminStudents() {
  const [search,       setSearch]       = useState('')
  const [viewStudent,  setViewStudent]  = useState(null)
  const [profileLoading, setProfileLoading] = useState(false)
  const [profile,      setProfile]      = useState(null)
  const [resumeModal,  setResumeModal]  = useState(null)

  // Infinite scroll — page size 15
  const fetchFn = useCallback(
    (page, size) => adminApi.getAllStudents(page, size),
    []
  )
  const { items: students, loading, error, hasMore, totalElements, sentinelRef, reset } =
    useInfiniteScroll(fetchFn, 15)

  // Toast notifications
  const [toast, setToast] = useState(null)
  const showToast = (message, type = 'success') => {
    setToast({ message, type })
    setTimeout(() => setToast(null), 4000)
  }

  // ── Single Student Add State ──
  const [showSingleModal, setShowSingleModal] = useState(false)
  const [singleForm, setSingleForm] = useState({
    rollNo: '',
    name: '',
    surname: '',
    email: '',
    mobileNo: '',
    password: 'pass1234'
  })
  const [singleSaving, setSingleSaving] = useState(false)
  const [singleError, setSingleError] = useState('')

  const handleSingleRegister = async (e) => {
    e.preventDefault()
    setSingleError('')
    if (!singleForm.rollNo || !singleForm.name || !singleForm.email) {
      setSingleError('Roll No, First Name, and College Email are required.')
      return
    }
    if (singleForm.password.length < 8) {
      setSingleError('Password must be at least 8 characters.')
      return
    }

    setSingleSaving(true)
    try {
      await authApi.registerStudent({
        ...singleForm,
        rollNo: singleForm.rollNo.trim().toUpperCase(),
        name: singleForm.name.trim(),
        surname: singleForm.surname.trim() || '.',
        email: singleForm.email.trim().toLowerCase(),
        mobileNo: singleForm.mobileNo.trim()
      })
      showToast(`Student ${singleForm.name} (${singleForm.rollNo.toUpperCase()}) registered successfully!`)
      setShowSingleModal(false)
      setSingleForm({ rollNo: '', name: '', surname: '', email: '', mobileNo: '', password: 'pass1234' })
      reset()
    } catch (err) {
      setSingleError(err?.response?.data?.message || err?.message || 'Failed to register student.')
    } finally {
      setSingleSaving(false)
    }
  }

  // ── Bulk CSV Upload State ──
  const [showBulkModal, setShowBulkModal] = useState(false)
  const [bulkFile, setBulkFile] = useState(null)
  const [parsedData, setParsedData] = useState(null)
  const [defaultPassword, setDefaultPassword] = useState('pass1234')
  const [isUploading, setIsUploading] = useState(false)
  const [uploadProgress, setUploadProgress] = useState({ current: 0, total: 0 })
  const [uploadResult, setUploadResult] = useState(null)
  const fileInputRef = useRef(null)

  const handleFileChange = (e) => {
    const file = e.target.files?.[0]
    if (!file) return
    setBulkFile(file)
    setUploadResult(null)

    const reader = new FileReader()
    reader.onload = (event) => {
      const text = event.target?.result
      if (typeof text === 'string') {
        const res = parseCsv(text, defaultPassword)
        setParsedData(res)
      }
    }
    reader.readAsText(file)
  }

  const handleBulkUpload = async () => {
    if (!parsedData || !parsedData.rows) return
    const validRows = parsedData.rows.filter(r => r.isValid)
    if (validRows.length === 0) return

    setIsUploading(true)
    setUploadProgress({ current: 0, total: validRows.length })

    let successCount = 0
    let failedCount = 0
    const failureDetails = []

    for (let i = 0; i < validRows.length; i++) {
      const student = validRows[i]
      try {
        await authApi.registerStudent({
          rollNo: student.rollNo,
          name: student.name,
          surname: student.surname,
          email: student.email,
          mobileNo: student.mobileNo,
          password: student.password || defaultPassword
        })
        successCount++
      } catch (err) {
        failedCount++
        const msg = err?.response?.data?.message || 'Error registering'
        failureDetails.push(`${student.rollNo} (${student.name}): ${msg}`)
      }
      setUploadProgress({ current: i + 1, total: validRows.length })
    }

    setIsUploading(false)
    setUploadResult({
      success: successCount,
      failed: failedCount,
      failures: failureDetails
    })

    if (successCount > 0) {
      showToast(`Successfully registered ${successCount} student${successCount > 1 ? 's' : ''}!`)
      reset()
    }
  }

  const resetBulkState = () => {
    setShowBulkModal(false)
    setBulkFile(null)
    setParsedData(null)
    setUploadResult(null)
    setIsUploading(false)
    if (fileInputRef.current) fileInputRef.current.value = ''
  }

  // Client-side search filter over accumulated items
  const filtered = search.trim()
    ? students.filter(s =>
        (s.name  || '').toLowerCase().includes(search.toLowerCase()) ||
        (s.rollNo || '').toLowerCase().includes(search.toLowerCase()) ||
        (s.email || '').toLowerCase().includes(search.toLowerCase())
      )
    : students

  const openProfile = async (s) => {
    setViewStudent(s); setProfile(null); setProfileLoading(true)
    try {
      const res = await adminApi.getStudentProfile(s.rollNo)
      setProfile(res.data?.data ?? res.data)
    } catch { setProfile(null) }
    finally { setProfileLoading(false) }
  }

  const openResume = (student, url) => {
    setViewStudent(null)
    setResumeModal({ student, url })
  }

  return (
    <DashboardLayout>
      <div className="p-8 space-y-5">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-gray-800 font-semibold text-lg">All Students</h2>
            <p className="text-gray-400 text-sm mt-0.5">
              {totalElements != null
                ? `${students.length} loaded of ${totalElements} total`
                : 'Loading…'}
              {search.trim() && ` · ${filtered.length} matching "${search}"`}
            </p>
          </div>
          <div className="flex items-center gap-2.5 flex-wrap">
            <div className="flex items-center gap-2 border border-gray-300 rounded-full px-4 py-2 bg-white w-52 sm:w-60 focus-within:ring-2 focus-within:ring-indigo-400">
              <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
              <input value={search} onChange={e => setSearch(e.target.value)}
                placeholder="Search name, roll no..."
                className="text-sm outline-none bg-transparent w-full text-gray-700 placeholder-gray-400" />
            </div>

            <button
              onClick={() => { setSingleError(''); setShowSingleModal(true) }}
              className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-semibold bg-white border border-gray-300 text-gray-700 hover:bg-gray-50 rounded-full transition shadow-sm active:scale-95"
            >
              <svg className="w-3.5 h-3.5 text-indigo-600" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4"/></svg>
              Add Student
            </button>

            <button
              onClick={() => { resetBulkState(); setShowBulkModal(true) }}
              className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-semibold bg-indigo-600 hover:bg-indigo-700 text-white rounded-full transition shadow-sm shadow-indigo-100 active:scale-95"
            >
              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"/></svg>
              Bulk Upload CSV
            </button>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-xl p-4 text-red-700 text-sm">{error}</div>
        )}

        {students.length === 0 && loading ? (
          <div className="flex items-center justify-center py-16">
            <svg className="w-7 h-7 animate-spin text-indigo-500" fill="none" viewBox="0 0 24 24">
              <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
              <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
            </svg>
          </div>
        ) : (
          <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
            <table className="w-full text-sm">
              <thead>
                <tr className="bg-gradient-to-r from-indigo-50 to-white text-xs text-gray-500 font-semibold uppercase tracking-wide">
                  {['Roll No', 'Name', 'Email', 'Mobile', 'Action'].map(h => (
                    <th key={h} className="px-4 py-3 text-left">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map((s, i) => (
                  <tr key={s.rollNo || i} className="border-t border-gray-100 hover:bg-gray-50 transition">
                    <td className="px-4 py-3 font-mono text-xs text-gray-500">{s.rollNo}</td>
                    <td className="px-4 py-3 font-medium text-gray-800">{s.name} {s.surname}</td>
                    <td className="px-4 py-3 text-gray-400 text-xs">{s.email}</td>
                    <td className="px-4 py-3 text-gray-500 text-xs">{s.mobileNo}</td>
                    <td className="px-4 py-3">
                      <button onClick={() => openProfile(s)}
                        className="flex items-center gap-1.5 px-3 py-1 text-xs bg-indigo-50 text-indigo-600 rounded-full hover:bg-indigo-100 font-medium transition">
                        <svg className="w-3 h-3" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                        View Profile
                      </button>
                    </td>
                  </tr>
                ))}
                {filtered.length === 0 && !loading && (
                  <tr>
                    <td colSpan={5} className="text-center py-12 text-gray-400 text-sm">
                      {search.trim() ? `No students matching "${search}".` : 'No students found.'}
                    </td>
                  </tr>
                )}
              </tbody>
            </table>

            {/* Infinite scroll sentinel */}
            <div ref={sentinelRef} className="h-px" />

            {/* Loading spinner for subsequent pages */}
            {loading && students.length > 0 && (
              <div className="flex items-center justify-center py-4">
                <svg className="w-5 h-5 animate-spin text-indigo-400" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/>
                </svg>
                <span className="ml-2 text-xs text-gray-400">Loading more…</span>
              </div>
            )}

            {!hasMore && students.length > 0 && (
              <p className="text-center py-3 text-xs text-gray-300">
                All {totalElements} students loaded
              </p>
            )}
          </div>
        )}
      </div>

      {/* Profile Modal */}
      {viewStudent && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" onClick={() => setViewStudent(null)}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[85vh] overflow-y-auto" onClick={e => e.stopPropagation()}>
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 sticky top-0 bg-white rounded-t-2xl">
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-bold text-sm">
                  {(viewStudent.name || '?')[0]}
                </div>
                <div>
                  <p className="font-semibold text-gray-800 text-sm">{viewStudent.name} {viewStudent.surname}</p>
                  <p className="text-xs text-gray-400 font-mono">{viewStudent.rollNo}</p>
                </div>
              </div>
              <button onClick={() => setViewStudent(null)} className="p-2 hover:bg-gray-100 rounded-lg text-gray-400">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
              </button>
            </div>
            <div className="p-6">
              {profileLoading ? (
                <div className="flex items-center justify-center py-10">
                  <svg className="w-6 h-6 animate-spin text-indigo-500" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"/><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"/></svg>
                </div>
              ) : !profile ? (
                <p className="text-center text-gray-400 text-sm py-8">Profile not uploaded yet.</p>
              ) : (
                <div className="space-y-5">
                  <div className="grid grid-cols-2 gap-3">
                    {[
                      ['Department',     profile.department],
                      ['Gender',         profile.gender],
                      ['CGPA',           profile.currentCgpa],
                      ['Semester',       profile.currentSemester],
                      ['10th %',         profile.tenthPercentage != null ? profile.tenthPercentage + '%' : '—'],
                      ['12th %',         profile.twelthPercentage != null ? profile.twelthPercentage + '%' : '—'],
                      ['Active Backlogs', profile.backlogCount],
                      ['Passing Year',   profile.passingYear],
                    ].map(([l, v]) => (
                      <div key={l} className="bg-gray-50 rounded-lg p-3">
                        <p className="text-xs text-gray-400">{l}</p>
                        <p className="font-semibold text-sm text-gray-800 mt-0.5">{v ?? '—'}</p>
                      </div>
                    ))}
                  </div>
                  <div className="flex gap-3 flex-wrap">
                    <div className={`flex items-center gap-2 px-3 py-2 rounded-lg text-xs font-medium ${profile.hasBacklogHistory ? 'bg-amber-50 text-amber-700' : 'bg-emerald-50 text-emerald-700'}`}>
                      {profile.hasBacklogHistory ? '⚠ Has backlog history' : '✓ No backlog history'}
                    </div>
                    {profile.resumeUrl && (
                      <button
                        onClick={() => openResume(viewStudent, profile.resumeUrl)}
                        className="flex items-center gap-1.5 px-3 py-2 rounded-lg text-xs font-medium bg-indigo-50 text-indigo-600 hover:bg-indigo-100 transition"
                      >
                        <svg className="w-3 h-3" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                        Preview Resume
                      </button>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Resume PDF Preview Modal */}
      {resumeModal && (
        <ResumePreviewModal
          student={resumeModal.student}
          resumeUrl={resumeModal.url}
          onClose={() => setResumeModal(null)}
        />
      )}

      {/* Toast Notification */}
      {toast && (
        <div className={`fixed bottom-6 right-6 px-5 py-3 rounded-xl text-sm flex items-center gap-2.5 shadow-xl z-50 ${toast.type === 'error' ? 'bg-red-600 text-white' : 'bg-gray-900 text-white'}`}>
          {toast.type === 'error'
            ? <svg className="w-4 h-4 shrink-0" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
            : <svg className="w-4 h-4 text-emerald-400 shrink-0" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7"/></svg>
          }
          {toast.message}
        </div>
      )}

      {/* Single Add Student Modal */}
      {showSingleModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4" onClick={() => setShowSingleModal(false)}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md max-h-[90vh] overflow-y-auto" onClick={e => e.stopPropagation()}>
            <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between">
              <div>
                <h3 className="font-semibold text-gray-800 text-base">Add New Student</h3>
                <p className="text-xs text-gray-400 mt-0.5">Register a single student account</p>
              </div>
              <button onClick={() => setShowSingleModal(false)} className="p-1.5 hover:bg-gray-100 rounded-lg text-gray-400">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
              </button>
            </div>
            <form onSubmit={handleSingleRegister} className="p-6 space-y-3.5">
              {singleError && (
                <div className="bg-red-50 border border-red-200 text-red-600 text-xs rounded-xl p-3">
                  {singleError}
                </div>
              )}

              <div>
                <label className="text-xs font-semibold text-gray-600">Roll Number *</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. 21B91A0501"
                  value={singleForm.rollNo}
                  onChange={e => setSingleForm(p => ({ ...p, rollNo: e.target.value.toUpperCase() }))}
                  className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-400 font-mono uppercase"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-gray-600">First Name *</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Rahul"
                    value={singleForm.name}
                    onChange={e => setSingleForm(p => ({ ...p, name: e.target.value }))}
                    className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-400"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-gray-600">Surname</label>
                  <input
                    type="text"
                    placeholder="e.g. Sharma"
                    value={singleForm.surname}
                    onChange={e => setSingleForm(p => ({ ...p, surname: e.target.value }))}
                    className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-400"
                  />
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-gray-600">College Email *</label>
                <input
                  type="email"
                  required
                  placeholder="e.g. 21b91a0501@anits.edu.in"
                  value={singleForm.email}
                  onChange={e => setSingleForm(p => ({ ...p, email: e.target.value.toLowerCase() }))}
                  className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-400"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-gray-600">Mobile Number</label>
                <input
                  type="tel"
                  placeholder="e.g. 9876543210 (10 digits)"
                  value={singleForm.mobileNo}
                  onChange={e => setSingleForm(p => ({ ...p, mobileNo: e.target.value }))}
                  className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-400"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-gray-600">Default Password * (min 8 chars)</label>
                <input
                  type="text"
                  required
                  value={singleForm.password}
                  onChange={e => setSingleForm(p => ({ ...p, password: e.target.value }))}
                  className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-indigo-400 font-mono"
                />
              </div>

              <div className="flex gap-2.5 pt-3">
                <button
                  type="button"
                  onClick={() => setShowSingleModal(false)}
                  className="flex-1 h-9 rounded-full border border-gray-300 text-xs font-medium text-gray-600 hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={singleSaving}
                  className="flex-1 h-9 rounded-full bg-indigo-600 text-white text-xs font-semibold hover:bg-indigo-700 transition disabled:opacity-50"
                >
                  {singleSaving ? 'Registering...' : 'Add Student'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Bulk CSV Upload Modal */}
      {showBulkModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={() => !isUploading && resetBulkState()}>
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col" onClick={e => e.stopPropagation()}>
            <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between shrink-0">
              <div>
                <h3 className="font-semibold text-gray-800 text-base">Bulk Upload Students via CSV</h3>
                <p className="text-xs text-gray-400 mt-0.5">Import multiple student accounts from a spreadsheet</p>
              </div>
              <button
                disabled={isUploading}
                onClick={resetBulkState}
                className="p-1.5 hover:bg-gray-100 rounded-lg text-gray-400 disabled:opacity-30"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12"/></svg>
              </button>
            </div>

            <div className="p-6 overflow-y-auto space-y-4">
              {/* Template info & file upload */}
              <div className="flex items-center justify-between bg-indigo-50/70 border border-indigo-100 rounded-xl p-3.5 text-xs">
                <div>
                  <p className="font-semibold text-indigo-900">Need the correct column format?</p>
                  <p className="text-indigo-700 text-[11px] mt-0.5">Columns: rollNo, name, surname, email, mobileNo, password</p>
                </div>
                <button
                  type="button"
                  onClick={downloadCsvTemplate}
                  className="flex items-center gap-1.5 px-3 py-1.5 bg-white border border-indigo-200 text-indigo-700 hover:bg-indigo-100 font-semibold rounded-lg shadow-xs shrink-0 transition"
                >
                  <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" strokeWidth={2} viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"/></svg>
                  Download Sample CSV
                </button>
              </div>

              {/* Upload Dropzone */}
              <div
                onClick={() => !isUploading && fileInputRef.current?.click()}
                className={`border-2 border-dashed rounded-2xl p-6 text-center cursor-pointer transition ${
                  bulkFile ? 'border-indigo-400 bg-indigo-50/20' : 'border-gray-200 hover:border-indigo-300 hover:bg-gray-50'
                } ${isUploading ? 'opacity-50 pointer-events-none' : ''}`}
              >
                <input
                  ref={fileInputRef}
                  type="file"
                  accept=".csv,text/csv"
                  onChange={handleFileChange}
                  className="hidden"
                />
                <div className="w-12 h-12 rounded-full bg-indigo-50 text-indigo-600 flex items-center justify-center mx-auto mb-2 text-xl">
                  📁
                </div>
                {bulkFile ? (
                  <div>
                    <p className="font-semibold text-gray-800 text-sm">{bulkFile.name}</p>
                    <p className="text-xs text-gray-400 mt-0.5">{(bulkFile.size / 1024).toFixed(1)} KB · Click to change file</p>
                  </div>
                ) : (
                  <div>
                    <p className="font-semibold text-gray-700 text-sm">Choose or drag a .CSV file here</p>
                    <p className="text-xs text-gray-400 mt-1">Accepts UTF-8 formatted CSV spreadsheets</p>
                  </div>
                )}
              </div>

              {/* Default password fallback */}
              <div className="flex items-center justify-between text-xs bg-gray-50 p-3 rounded-xl border border-gray-200">
                <span className="text-gray-600 font-medium">Default password if omitted in CSV:</span>
                <input
                  type="text"
                  value={defaultPassword}
                  onChange={e => setDefaultPassword(e.target.value)}
                  disabled={isUploading}
                  className="border border-gray-300 rounded px-2 py-1 text-xs font-mono bg-white w-28 text-center"
                />
              </div>

              {/* Parsed Error */}
              {parsedData?.error && (
                <div className="bg-red-50 border border-red-200 text-red-600 text-xs rounded-xl p-3">
                  {parsedData.error}
                </div>
              )}

              {/* Live Preview Table */}
              {parsedData?.rows && parsedData.rows.length > 0 && (
                <div className="space-y-2">
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-semibold text-gray-700">File Preview ({parsedData.rows.length} rows found)</span>
                    <div className="flex items-center gap-2">
                      <span className="px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200 font-medium">
                        ✓ {parsedData.rows.filter(r => r.isValid).length} Valid
                      </span>
                      {parsedData.rows.some(r => !r.isValid) && (
                        <span className="px-2 py-0.5 rounded-full bg-red-50 text-red-700 border border-red-200 font-medium">
                          ✕ {parsedData.rows.filter(r => !r.isValid).length} Invalid
                        </span>
                      )}
                    </div>
                  </div>

                  <div className="border border-gray-200 rounded-xl overflow-hidden max-h-48 overflow-y-auto text-xs">
                    <table className="w-full">
                      <thead className="bg-gray-50 text-gray-500 font-semibold sticky top-0 border-b border-gray-200">
                        <tr>
                          <th className="px-3 py-2 text-left">Roll No</th>
                          <th className="px-3 py-2 text-left">Name</th>
                          <th className="px-3 py-2 text-left">Email</th>
                          <th className="px-3 py-2 text-left">Mobile</th>
                          <th className="px-3 py-2 text-left">Status</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-100">
                        {parsedData.rows.slice(0, 10).map((r, i) => (
                          <tr key={i} className={r.isValid ? 'hover:bg-gray-50' : 'bg-red-50/50'}>
                            <td className="px-3 py-2 font-mono">{r.rollNo || '—'}</td>
                            <td className="px-3 py-2 font-medium">{r.name} {r.surname !== '.' ? r.surname : ''}</td>
                            <td className="px-3 py-2 text-gray-500">{r.email}</td>
                            <td className="px-3 py-2 text-gray-400">{r.mobileNo || '—'}</td>
                            <td className="px-3 py-2">
                              {r.isValid ? (
                                <span className="text-[11px] text-emerald-600 font-medium">Ready</span>
                              ) : (
                                <span className="text-[11px] text-red-600 font-medium" title={r.errors.join(', ')}>
                                  {r.errors[0]}
                                </span>
                              )}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                  {parsedData.rows.length > 10 && (
                    <p className="text-[11px] text-gray-400 text-center">... and {parsedData.rows.length - 10} more rows</p>
                  )}
                </div>
              )}

              {/* Upload Progress Bar */}
              {isUploading && (
                <div className="space-y-1.5 p-4 bg-indigo-50/50 rounded-xl border border-indigo-100">
                  <div className="flex justify-between text-xs text-indigo-900 font-medium">
                    <span>Registering students...</span>
                    <span>{uploadProgress.current} / {uploadProgress.total}</span>
                  </div>
                  <div className="w-full bg-indigo-200/60 rounded-full h-2 overflow-hidden">
                    <div
                      className="bg-indigo-600 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${(uploadProgress.current / uploadProgress.total) * 100}%` }}
                    />
                  </div>
                </div>
              )}

              {/* Upload Result Summary */}
              {uploadResult && (
                <div className="p-4 bg-gray-50 rounded-xl border border-gray-200 text-xs space-y-2">
                  <p className="font-semibold text-gray-800">Import Complete</p>
                  <div className="flex gap-4">
                    <span className="text-emerald-700 font-medium">✅ {uploadResult.success} registered successfully</span>
                    {uploadResult.failed > 0 && (
                      <span className="text-red-600 font-medium">❌ {uploadResult.failed} failed</span>
                    )}
                  </div>
                  {uploadResult.failures?.length > 0 && (
                    <div className="max-h-24 overflow-y-auto bg-white p-2 rounded border border-gray-200 text-[11px] text-red-600 space-y-0.5">
                      {uploadResult.failures.map((f, i) => (
                        <p key={i}>{f}</p>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>

            <div className="px-6 py-4 border-t border-gray-100 flex items-center justify-end gap-2.5 shrink-0 bg-gray-50 rounded-b-2xl">
              <button
                type="button"
                disabled={isUploading}
                onClick={resetBulkState}
                className="px-4 py-2 border border-gray-300 rounded-full text-xs font-medium text-gray-600 hover:bg-white disabled:opacity-40"
              >
                {uploadResult ? 'Done' : 'Cancel'}
              </button>

              {!uploadResult && (
                <button
                  type="button"
                  onClick={handleBulkUpload}
                  disabled={isUploading || !parsedData?.rows?.some(r => r.isValid)}
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-semibold rounded-full shadow-sm shadow-indigo-100 disabled:opacity-50 transition"
                >
                  {isUploading ? 'Importing...' : `Import ${parsedData?.rows?.filter(r => r.isValid).length || 0} Students`}
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </DashboardLayout>
  )
}
import api from './axiosInstance'

// ─────────────────────────────────────────────────────────────────
// Auth  —  /api/auth/*
// ─────────────────────────────────────────────────────────────────
export const authApi = {
  login: (data) => api.post('/auth/login', data),
  logout: (refreshToken) => api.post('/auth/logout', { refreshToken }),
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken }),
  registerStudent: (data) => api.post('/auth/register/student', data),
  registerAdmin: (data) => api.post('/auth/register/admin', data),
  registerCompany: (data) => api.post('/auth/register/company', data),
}

// ─────────────────────────────────────────────────────────────────
// Admin  —  /api/admin/*   (ROLE_ADMIN + ROLE_SUPER_ADMIN)
// ─────────────────────────────────────────────────────────────────
export const adminApi = {
  // GET /api/admin/allStudents?page=0&size=15
  // Returns: Page<StudentResponseDto>  →  { content, totalElements, totalPages, last }
  getAllStudents: (page = 0, size = 15) =>
    api.get('/admin/allStudents', { params: { page, size } }),

  // GET /api/admin/allStudentProfiles?page=0&size=15
  getAllStudentProfiles: (page = 0, size = 15) =>
    api.get('/admin/allStudentProfiles', { params: { page, size } }),

  // GET /api/admin/student/{rollNo}/profile
  getStudentProfile: (rollNo) => api.get(`/admin/student/${rollNo}/profile`),

  // GET /api/admin/allCompanies
  getAllCompanies: () => api.get('/admin/allCompanies'),

  // GET /api/admin/allDrives?page=0&size=15
  getAllDrives: (page = 0, size = 15) =>
    api.get('/admin/allDrives', { params: { page, size } }),

  // GET /api/admin/company/{companyId}/drives
  getCompanyDrives: (companyId) => api.get(`/admin/company/${companyId}/drives`),

  // GET /api/admin/getAllActiveDrives
  getAllActiveDrives: () => api.get('/admin/getAllActiveDrives'),

  // GET /api/admin/getAllApplications/{driveId}?page=0&size=10
  getApplications: (driveId, page = 0, size = 10) =>
    api.get(`/admin/getAllApplications/${driveId}`, { params: { page, size } }),

  // GET /api/admin/student/allApplications/{rollNo}?page=0&size=10
  getStudentApplications: (rollNo, page = 0, size = 10) =>
    api.get(`/admin/student/allApplications/${rollNo}`, { params: { page, size } }),

  // GET /api/admin/getAllDriveRounds/{driveId}?page=0&size=15
  getDriveRounds: (driveId, page = 0, size = 15) =>
    api.get(`/admin/getAllDriveRounds/${driveId}`, { params: { page, size } }),

  // GET /api/admin/getApplicantsForDriveRound/{driveId}/{roundNo}?page=0&size=15
  getApplicantsForRound: (driveId, roundNo, page = 0, size = 15) =>
    api.get(`/admin/getApplicantsForDriveRound/${driveId}/${roundNo}`, { params: { page, size } }),

  // POST /api/admin/company/addDrive
  addDrive: (data) => api.post('/admin/company/addDrive', data),

  // POST /api/admin/company/addDriveEligibility
  addEligibility: (data) => api.post('/admin/company/addDriveEligibility', data),

  // PUT /api/admin/updateDriveEligibility/{driveId}
  updateEligibility: (driveId, data) =>
    api.put(`/admin/updateDriveEligibility/${driveId}`, data),

  // PUT /api/admin/publishDrives/{driveId}
  publishDrive: (driveId) => api.put(`/admin/publishDrives/${driveId}`),

  // PUT /api/admin/closeDrive/{driveId}   ← was GET (wrong HTTP method)
  closeDrive: (driveId) => api.put(`/admin/closeDrive/${driveId}`),

  // DELETE /api/admin/deleteDrive/{driveId}   ← was GET (wrong HTTP method)
  deleteDrive: (driveId) => api.delete(`/admin/deleteDrive/${driveId}`),

  // PUT /api/admin/removeDrive/{driveId}
  removeDrive: (driveId) => api.put(`/admin/removeDrive/${driveId}`),

  // PUT /api/admin/extendDrive/{driveId}/{date}
  extendDrive: (driveId, date) => api.put(`/admin/extendDrive/${driveId}/${date}`),

  // GET /api/admin/student/{rollNo}/viewResume
  viewStudentResume: (rollNo) =>
    api.get(`/admin/student/${rollNo}/viewResume`, { responseType: 'blob' }),

  // ── Count endpoints (used by dashboards — avoids loading full lists) ──
  // GET /api/admin/students/count
  countStudents: () => api.get('/admin/students/count'),
  // GET /api/admin/companies/count
  countCompanies: () => api.get('/admin/companies/count'),
  // GET /api/admin/activeDrives/count
  countActiveDrives: () => api.get('/admin/activeDrives/count'),
  // GET /api/admin/admins/count  (ROLE_SUPER_ADMIN only)
  countAdmins: () => api.get('/admin/admins/count'),
}

// ─────────────────────────────────────────────────────────────────
// Company  —  /api/company/*   (ROLE_COMPANY)
// ─────────────────────────────────────────────────────────────────
export const companyApi = {
  // GET /api/company/getAllDrives?page=0&size=15
  getAllDrives: (page = 0, size = 15) =>
    api.get('/company/getAllDrives', { params: { page, size } }),

  // GET /api/company/getRounds/{driveId}?page=0&size=15
  getRounds: (driveId, page = 0, size = 15) =>
    api.get(`/company/getRounds/${driveId}`, { params: { page, size } }),

  // GET /api/company/allApplications/{driveId}?page=0&size=10
  getAllApplications: (driveId, page = 0, size = 10) =>
    api.get(`/company/allApplications/${driveId}`, { params: { page, size } }),

  // GET /api/company/allApplications/{driveId}/{roundNumber}?page=0&size=15
  getApplicationsByRound: (driveId, roundNo, page = 0, size = 15) =>
    api.get(`/company/allApplications/${driveId}/${roundNo}`, { params: { page, size } }),

  // POST /api/company/publishDriveRound/{driveId}
  publishRound: (driveId, data) => api.post(`/company/publishDriveRound/${driveId}`, data),

  // POST /api/company/publishScore/{driveId}/{rollNo}/{roundNo}/{score}
  publishScore: (driveId, rollNo, roundNo, score) =>
    api.post(`/company/publishScore/${driveId}/${rollNo}/${roundNo}/${score}`),

  // POST /api/company/publishFeedback/{driveId}/{rollNo}/{roundNo}
  publishFeedback: (driveId, rollNo, roundNo, feedback) =>
    api.post(`/company/publishFeedback/${driveId}/${rollNo}/${roundNo}`, { feedback }),

  // POST /api/company/filterByTopK/{driveId}/{roundNo}/{topK}
  filterTopK: (driveId, roundNo, topK) =>
    api.post(`/company/filterByTopK/${driveId}/${roundNo}/${topK}`),

  // POST /api/company/filterByCutOff/{driveId}/{roundNo}/{cutOff}
  filterByCutoff: (driveId, roundNo, cutoff) =>
    api.post(`/company/filterByCutOff/${driveId}/${roundNo}/${cutoff}`),

  // POST /api/company/shortlist/{driveId}/{roundNo}/{rollNo}/{status}
  shortlistStudent: (driveId, roundNo, rollNo, status) =>
    api.post(`/company/shortlist/${driveId}/${roundNo}/${rollNo}/${status}`),

  // GET /api/company/countFilterByCutOff/{driveId}/{roundNo}/{cutOff}
  countByCutoff: (driveId, roundNo, cutoff) =>
    api.get(`/company/countFilterByCutOff/${driveId}/${roundNo}/${cutoff}`),

  // GET /api/company/viewResume/{rollNo}
  viewStudentResume: (rollNo) =>
    api.get(`/company/viewResume/${rollNo}`, { responseType: 'blob' }),

  // PUT /api/company/closeDrive/{driveId}
  closeDrive: (driveId) => api.put(`/company/closeDrive/${driveId}`),

  // ── Count endpoints ──
  // GET /api/company/activeDrives/count
  countActiveDrives: () => api.get('/company/activeDrives/count'),
  // GET /api/company/Drives/count
  countDrives: () => api.get('/company/Drives/count'),
}

// ─────────────────────────────────────────────────────────────────
// Super Admin  —  /api/super-admin/*   (ROLE_SUPER_ADMIN)
// ─────────────────────────────────────────────────────────────────
export const superAdminApi = {
  // GET /api/super-admin/allAdmins
  getAllAdmins: () => api.get('/super-admin/allAdmins'),
  // DELETE /api/super-admin/delete-admin/{id}
  deleteAdmin: (id) => api.delete(`/super-admin/delete-admin/${id}`),
}

// ─────────────────────────────────────────────────────────────────
// Student  —  /api/student/*   (ROLE_STUDENT)
// ─────────────────────────────────────────────────────────────────
export const studentApi = {
  // GET /api/student/profile
  getProfile: () => api.get('/student/profile'),

  // POST /api/student/profile/add
  addProfile: (data) => api.post('/student/profile/add', data),

  // PUT /api/student/profile/update
  updateProfile: (data) => api.put('/student/profile/update', data),

  // GET /api/student/profile/allApplications?page=0&size=10
  getApplications: (page = 0, size = 10) =>
    api.get('/student/profile/allApplications', { params: { page, size } }),

  // GET /api/student/profile/allEligibleApplications?page=0&size=15
  getEligibleDrives: (page = 0, size = 15) =>
    api.get('/student/profile/allEligibleApplications', { params: { page, size } }),

  // PUT /api/student/profile/applyDrive/{driveId}
  applyDrive: (driveId) => api.put(`/student/profile/applyDrive/${driveId}`),

  // GET /api/student/profile/allRounds/{driveId}
  getRoundsForDrive: (driveId) => api.get(`/student/profile/allRounds/${driveId}`),

  // POST /api/student/profile/uploadResume
  uploadResume: (formData) =>
    api.post('/student/profile/uploadResume', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),

  // GET /api/student/profile/viewResume  ← fixed path (was /profile/profile/viewResume)
  viewStudentResume: () =>
    api.get('/student/profile/viewResume', { responseType: 'blob' }),
}
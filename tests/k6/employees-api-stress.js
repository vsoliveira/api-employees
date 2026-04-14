import http from 'k6/http';
import { check, sleep } from 'k6';

const baseUrl = (__ENV.BASE_URL || 'http://localhost:8080/api').replace(/\/$/, '');
const apiKey = __ENV.API_KEY || 'dev-api-key';
const pageSize = Number(__ENV.PAGE_SIZE || 100);
const browseRate = Number(__ENV.BROWSE_RATE || 60);
const browsePreAllocatedVus = Number(__ENV.BROWSE_PRE_ALLOCATED_VUS || 20);
const browseMaxVus = Number(__ENV.BROWSE_MAX_VUS || 120);
const browseStageOneDuration = __ENV.BROWSE_STAGE_ONE_DURATION || '30s';
const browseStageTwoDuration = __ENV.BROWSE_STAGE_TWO_DURATION || '2m';
const browseStageThreeDuration = __ENV.BROWSE_STAGE_THREE_DURATION || '30s';
const writeVus = Number(__ENV.WRITE_VUS || 4);
const writeDuration = __ENV.WRITE_DURATION || '2m';
const writeGracefulStop = __ENV.WRITE_GRACEFUL_STOP || '10s';

const departmentCatalog = [
  'Finance',
  'Accounting',
  'Treasury',
  'Internal Audit',
  'Legal',
  'Compliance',
  'Risk Management',
  'Human Resources',
  'People Operations',
  'Talent Acquisition',
  'Learning and Development',
  'Engineering',
  'Platform Engineering',
  'QA Engineering',
  'Product Management',
  'Product Design',
  'Data Engineering',
  'Data Science',
  'Business Intelligence',
  'Information Security',
  'IT Support',
  'Cloud Operations',
  'Architecture',
  'Sales',
  'Sales Operations',
  'Customer Success',
  'Customer Support',
  'Marketing',
  'Brand Strategy',
  'Growth Marketing',
  'Communications',
  'Procurement',
  'Supply Chain',
  'Logistics',
  'Facilities',
  'Workplace Experience',
  'Administration',
  'Research',
  'Innovation Lab',
  'Partnerships',
  'Strategy',
  'Revenue Operations',
  'PMO',
  'Operations',
  'Field Services',
  'Regional Management',
  'Sustainability',
  'Investor Relations',
  'Corporate Affairs',
  'Executive Office',
];

export const options = {
  thresholds: {
    http_req_failed: ['rate<0.02'],
    'http_req_duration{scenario:browseEmployees}': ['p(95)<750'],
    'http_req_duration{scenario:createAndDeleteEmployees}': ['p(95)<1000'],
    checks: ['rate>0.99'],
  },
  scenarios: {
    browseEmployees: {
      executor: 'ramping-arrival-rate',
      exec: 'browseEmployees',
      startRate: Math.max(1, Math.floor(browseRate / 3)),
      timeUnit: '1s',
      preAllocatedVUs: browsePreAllocatedVus,
      maxVUs: browseMaxVus,
      stages: [
        { target: browseRate, duration: browseStageOneDuration },
        { target: browseRate, duration: browseStageTwoDuration },
        { target: 0, duration: browseStageThreeDuration },
      ],
      tags: { scenario: 'browseEmployees' },
    },
    createAndDeleteEmployees: {
      executor: 'constant-vus',
      exec: 'createAndDeleteEmployees',
      vus: writeVus,
      duration: writeDuration,
      gracefulStop: writeGracefulStop,
      tags: { scenario: 'createAndDeleteEmployees' },
    },
  },
};

export function setup() {
  const response = http.get(
    `${baseUrl}/v1/employees?page=0&size=${pageSize}&sortBy=name&direction=asc`,
    { headers: buildHeaders() },
  );

  const setupSucceeded = check(response, {
    'setup list request returns 200': (result) => result.status === 200,
    'setup response includes total pages': (result) => Number(result.json('totalPages')) >= 1,
  });

  if (!setupSucceeded) {
    throw new Error(`Setup failed with status ${response.status}: ${response.body}`);
  }

  return {
    totalPages: Number(response.json('totalPages')),
  };
}

export function browseEmployees(data) {
  const totalPages = Math.max(1, data.totalPages || 1);
  const page = Math.floor(Math.random() * totalPages);
  const sortBy = Math.random() > 0.5 ? 'name' : 'department';
  const direction = Math.random() > 0.5 ? 'asc' : 'desc';

  const response = http.get(
    `${baseUrl}/v1/employees?page=${page}&size=${pageSize}&sortBy=${sortBy}&direction=${direction}`,
    { headers: buildHeaders() },
  );

  check(response, {
    'browse returns 200': (result) => result.status === 200,
    'browse payload contains page content': (result) => Array.isArray(result.json('content')),
    'browse payload respects size bound': (result) => result.json('content').length <= pageSize,
  });

  sleep(0.2);
}

export function createAndDeleteEmployees() {
  const identifier = `${__VU}-${__ITER}-${Date.now()}`;
  const payload = JSON.stringify({
    name: `Load Test User ${identifier}`,
    email: `load.test.${identifier}@example.com`,
    department: departmentCatalog[(__ITER + __VU) % departmentCatalog.length],
  });

  const createResponse = http.post(`${baseUrl}/v1/employees`, payload, {
    headers: buildHeaders({ 'Content-Type': 'application/json' }),
  });

  const createSucceeded = check(createResponse, {
    'create returns 201': (result) => result.status === 201,
    'create returns id': (result) => typeof result.json('id') === 'string' && result.json('id').length > 0,
  });

  if (!createSucceeded) {
    sleep(0.5);
    return;
  }

  const employeeId = createResponse.json('id');
  const deleteResponse = http.del(`${baseUrl}/v1/employees/${employeeId}`, null, {
    headers: buildHeaders(),
  });

  check(deleteResponse, {
    'delete returns 204': (result) => result.status === 204,
  });

  sleep(0.5);
}

function buildHeaders(extraHeaders = {}) {
  return {
    'X-API-Key': apiKey,
    Accept: 'application/json',
    ...extraHeaders,
  };
}
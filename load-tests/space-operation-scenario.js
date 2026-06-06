import http from 'k6/http';
import { check, group, sleep } from 'k6';
import exec from 'k6/execution';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const VUS = Number(__ENV.VUS || 20);

export const options = {
    stages: [
        { duration: '15s', target: VUS },
        { duration: '60s', target: VUS },
        { duration: '15s', target: 0 },
    ],
    thresholds: {
        checks: ['rate>0.95'],
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<2000'],
    },
};

const jsonHeaders = {
    headers: {
        'Content-Type': 'application/json',
    },
};

export function setup() {
    const overview = http.get(`${BASE_URL}/api/overview`);
    check(overview, {
        'service is available': (response) => response.status === 200,
    });
}

export default function () {
    const iteration = exec.scenario.iterationInTest;
    const suffix = `${__VU}-${iteration}-${Date.now()}`;
    const constellationName = `load-constellation-${suffix}`;
    const communicationSatelliteName = `load-comm-${suffix}`;
    const imagingSatelliteName = `load-image-${suffix}`;

    let createdConstellationId;

    group('read initial system overview', () => {
        const response = http.get(`${BASE_URL}/api/overview`);
        check(response, {
            'initial overview is returned': (res) => res.status === 200,
        });
    });

    group('create constellation with satellites', () => {
        const payload = JSON.stringify({
            constellationName,
            satelliteParams: [
                {
                    type: 'COMMUNICATION',
                    name: communicationSatelliteName,
                    batteryLevel: 0.95,
                    bandwidth: 120.0,
                },
                {
                    type: 'IMAGE',
                    name: imagingSatelliteName,
                    batteryLevel: 0.9,
                    resolution: 0.5,
                },
            ],
        });

        const response = http.post(`${BASE_URL}/api/add-satellites`, payload, jsonHeaders);
        check(response, {
            'satellites are added': (res) => res.status === 200,
            'constellation id is returned': (res) => Boolean(readJsonField(res, 'id')),
        });

        createdConstellationId = readJsonField(response, 'id');
    });

    group('read constellation status', () => {
        const response = http.get(`${BASE_URL}/api/constellations/${constellationName}/status`);
        check(response, {
            'status is returned': (res) => res.status === 200,
        });
    });

    group('activate constellation', () => {
        const response = http.post(`${BASE_URL}/api/constellations/${constellationName}/activate`);
        check(response, {
            'constellation is activated': (res) => res.status === 200,
        });
    });

    group('execute constellation mission', () => {
        const payload = JSON.stringify({
            targetType: 'CONSTELLATION',
            constellationName,
            satelliteName: null,
        });

        const response = http.post(`${BASE_URL}/api/missions`, payload, jsonHeaders);
        check(response, {
            'mission is accepted': (res) => res.status === 200,
        });
    });

    group('read system overview', () => {
        const response = http.get(`${BASE_URL}/api/overview`);
        check(response, {
            'overview is returned': (res) => res.status === 200,
        });
    });

    group('decommission satellite', () => {
        const response = http.del(`${BASE_URL}/api/constellations/${constellationName}/satellites/${communicationSatelliteName}`);
        check(response, {
            'satellite is decommissioned': (res) => res.status === 200,
        });
    });

    group('cleanup constellation', () => {
        if (!createdConstellationId) {
            return;
        }

        const response = http.del(`${BASE_URL}/api/db/constellations/${createdConstellationId}`);
        check(response, {
            'constellation is removed': (res) => res.status === 204,
        });
    });

    sleep(1);
}

function readJsonField(response, fieldName) {
    try {
        return response.json(fieldName);
    } catch (error) {
        return null;
    }
}

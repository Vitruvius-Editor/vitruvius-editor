import Docker from 'dockerode';
import { exec } from 'child_process';
import { promisify } from 'util';
const docker = new Docker();
const execPromise = promisify(exec);

export const beforeEach = async () => {
    await execPromise("sudo docker-compose -f compose-tests.yml up -d --force-recreate");
    await waitForHealth("http://localhost:8080/actuator/health");
};

export const afterEach = async () => {
    await execPromise("sudo docker-compose -f compose-tests.yml down -t 0");
};


async function waitForHealth(url: string, timeout: number = 30000, interval: number = 5000) {
    const startTime = Date.now();

    while (Date.now() - startTime < timeout) {
        try {
            const response = await fetch(url);
            if (response.ok) {
                const data = await response.json();
                if (data.status === 'UP') {
                    console.log('Backend is healthy!');
                    return;
                }
            }
        } catch (error) {
            // Ignore errors and continue polling
            console.log('Waiting for backend to be healthy...');
        }

        await new Promise(resolve => setTimeout(resolve, interval));
    }

    throw new Error(`Backend did not become healthy within ${timeout / 1000} seconds.`);
}

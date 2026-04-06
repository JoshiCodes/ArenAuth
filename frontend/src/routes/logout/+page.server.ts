import { INTERNAL_BACKEND_URL } from '$lib/server_vars';
import { applyBackendCookie } from '$lib/server/cookies';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ request, cookies }) => {
    const cookie = request.headers.get('cookie') ?? '';

    try {
        const res = await fetch(INTERNAL_BACKEND_URL + '/api/v1/internal/auth/logout', {
            method: 'POST',
            headers: {
                cookie
            }
        });

        const setCookie = res.headers.get('set-cookie');
        if (setCookie) {
            applyBackendCookie(setCookie, cookies);
        }
    } catch (e) {
        console.error('Logout backend failed', e);
    }

    return {};
};
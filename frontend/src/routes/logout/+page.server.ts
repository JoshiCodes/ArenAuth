import { redirect } from '@sveltejs/kit';
import {INTERNAL_BACKEND_URL} from "$lib/server_vars";

export const load = async ({ request }) => {
    const cookie = request.headers.get('cookie') ?? '';

    await globalThis.fetch(INTERNAL_BACKEND_URL + '/api/v1/internal/auth/logout', {
        method: 'POST',
        headers: {
            cookie
        }
    });

    throw redirect(303, '/');
};
import { redirect } from '@sveltejs/kit';
import {INTERNAL_BACKEND_URL} from "$lib/server_vars";

export const load = async ({ fetch, url, depends, request }) => {
    depends('app:me');

    const cookie = request.headers.get('cookie') ?? '';

    const res = await globalThis.fetch(INTERNAL_BACKEND_URL + '/api/v1/internal/me', {
        headers: {
            cookie
        }
    });

    if (!res.ok) {
        const returnTo = encodeURIComponent(url.pathname + url.search);
        throw redirect(303, `/login?returnTo=${returnTo}`);
    }

    return { me: await res.json() };
};

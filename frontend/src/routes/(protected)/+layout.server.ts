import { redirect } from '@sveltejs/kit';
import {INTERNAL_BACKEND_URL} from "$lib/server_vars";

export const load = async ({ fetch, url, depends, cookies }) => {
    depends('app:me');

    const cookieHeader = cookies
        .getAll()
        .map((c) => `${c.name}=${c.value}`)
        .join('; ');

    const res = await fetch(INTERNAL_BACKEND_URL + '/api/v1/internal/me', {
        credentials: 'include',
        headers: {
            cookie: cookieHeader
        }
    });

    if (!res.ok) {
        const returnTo = encodeURIComponent(url.pathname + url.search);
        throw redirect(303, `/login?returnTo=${returnTo}`);
    }

    return { me: await res.json() };
};

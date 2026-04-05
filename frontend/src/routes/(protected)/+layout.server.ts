import { redirect } from '@sveltejs/kit';
import {INTERNAL_BACKEND_URL} from "$lib/server_vars";

export const load = async ({ fetch, url, depends, request }) => {
    depends('app:me');

    const cookie = request.headers.get('cookie') ?? '';
    console.log("(ASD) Cookie: ", cookie)

    const res = await fetch(INTERNAL_BACKEND_URL + '/api/v1/internal/me', {
        credentials: 'include',
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

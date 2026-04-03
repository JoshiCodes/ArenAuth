import { redirect } from '@sveltejs/kit';
import {PUBLIC_BACKEND_URL} from "$env/static/public";

export const load = async ({ fetch, url, depends }) => {
    depends('app:me');

    const res = await fetch(PUBLIC_BACKEND_URL + '/api/v1/internal/me', { credentials: 'include' });

    if (!res.ok) {
        const returnTo = encodeURIComponent(url.pathname + url.search);
        throw redirect(303, `/login?returnTo=${returnTo}`);
    }

    return { me: await res.json() };
};
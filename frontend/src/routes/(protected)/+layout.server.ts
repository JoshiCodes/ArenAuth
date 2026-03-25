import { redirect } from '@sveltejs/kit';

export const load = async ({ fetch, url, depends }) => {
    depends('app:me');

    const res = await fetch('/api/internal/me', { credentials: 'include' });

    if (!res.ok) {
        const returnTo = encodeURIComponent(url.pathname + url.search);
        throw redirect(303, `/login?returnTo=${returnTo}`);
    }

    return { me: await res.json() };
};
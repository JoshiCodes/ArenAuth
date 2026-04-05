import type { HandleFetch } from '@sveltejs/kit';

export const handleFetch: HandleFetch = async ({ request, fetch }) => {
    const url = new URL(request.url);

    if (url.hostname === 'backend' && url.port === '8080') {
        request.headers.delete('origin');
        request.headers.delete('referer');
    }
    console.log('OUTGOING TO BACKEND', Object.fromEntries(request.headers.entries()));

    return fetch(request);
};
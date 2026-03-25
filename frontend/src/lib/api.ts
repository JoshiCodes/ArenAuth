import { goto } from '$app/navigation';

export async function apiFetch(input: RequestInfo | URL, init: RequestInit = {}) {
    const res = await fetch(input, { ...init, credentials: 'same-origin' });

    if (res.status === 401) {
        await goto('/login');
    }

    return res;
}
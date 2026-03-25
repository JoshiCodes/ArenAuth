import { redirect } from '@sveltejs/kit';

export const load = async ({ fetch }) => {
    await fetch('/api/internal/auth/logout', {
        method: 'POST',
        credentials: 'include'
    });

    throw redirect(303, '/');
};
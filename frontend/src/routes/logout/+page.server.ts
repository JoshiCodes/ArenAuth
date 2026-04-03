import { redirect } from '@sveltejs/kit';
import { PUBLIC_BACKEND_URL } from '$env/static/public'

export const load = async ({ fetch }) => {
    await fetch(PUBLIC_BACKEND_URL + '/api/v1/internal/auth/logout', {
        method: 'POST',
        credentials: 'include'
    });

    throw redirect(303, '/');
};
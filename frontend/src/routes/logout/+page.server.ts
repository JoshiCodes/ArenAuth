import { redirect } from '@sveltejs/kit';
import {BACKEND_URL} from "$lib/vars";

export const load = async ({ fetch }) => {
    await fetch(BACKEND_URL + '/api/v1/internal/auth/logout', {
        method: 'POST',
        credentials: 'include'
    });

    throw redirect(303, '/');
};
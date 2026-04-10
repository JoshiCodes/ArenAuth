import {BACKEND_URL} from '$lib/vars';
import {fail, redirect} from '@sveltejs/kit';
import type {Actions} from './$types';
import {applyBackendCookie} from "$lib/server/cookies";

function sanitizeReturnTo(value: string | null): string {
    if (!value || !value.startsWith('/') || value.startsWith('//') || value.includes('\\')) {
        return '/dashboard';
    }
    return value;
}

export const actions: Actions = {
    default: async({cookies, request, url}) => {
        const data = await request.formData();
        const username = data.get('username');
        const password = data.get('password');

        if(!username || !password || typeof username !== "string" || typeof password !== "string") {
            return fail(400, { success: false, error: 'Please fill out all fields.' })
        }

        try {
            const res = await fetch(`${BACKEND_URL}/api/v1/internal/auth/login`, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            const responseData = await res.json();

            if(!res.ok) {
                return fail(res.status, { error: responseData.error || 'Unknown error.' });
            }

            const sessionCookie = res.headers.get('set-cookie');

            if(sessionCookie) {
                applyBackendCookie(sessionCookie, cookies);
            }

        } catch (error: any) {

            console.error('Error connecting to the backend:', error);
            return fail(500, { error: 'Failed to connect to the server.' });
        }

        const redirectTarget = sanitizeReturnTo((url.searchParams.get('returnTo') as string | null) ?? "/dashboard");
        throw redirect(303, redirectTarget);

    }
};
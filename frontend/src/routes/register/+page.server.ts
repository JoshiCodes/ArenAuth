import { BACKEND_URL } from '$lib/vars';
import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';
import {TURNSTILE_ENABLED} from "$lib/captcha";
import {validateCaptcha} from "$lib/server/captcha_server";
import {applyBackendCookie} from "$lib/server/cookies";
import {env} from "$env/dynamic/public";

function sanitizeReturnTo(value: string | null): string {
    if (!value || !value.startsWith('/') || value.startsWith('//') || value.includes('\\')) {
        return '/dashboard';
    }
    return value;
}

export const load = async ({ fetch, url, depends, cookies }) => {

    if(env.PUBLIC_DISABLE_SIGNUP) {
        throw redirect(303, `/?error=signup_disabled`);
    }

}

export const actions: Actions = {
    default: async({cookies, request}) => {

        const data = await request.formData();

        if(TURNSTILE_ENABLED) {
            const captcha = await validateCaptcha(data);
            if(captcha !== true) {
                return fail(400, {error: 'CAPTCHA validation failed' + (captcha.error ? " (" + captcha.error + ")" : "")});
            }
        }

        const username = data.get('username') as string;
        const email = data.get('email') as string;
        const password = data.get('password') as string;
        const passwordConfirm = data.get('password_confirm') as string;

        if(!username || !email || !password || !passwordConfirm) {
            console.log('Missing fields:', {username, email, password, passwordConfirm});
            return fail(400, {error: 'All fields are required'});
        }

        if(password != passwordConfirm) {
            return fail(400, {error: 'Passwords do not match'});
        }

        try {
            const res = await fetch(`${BACKEND_URL}/api/v1/internal/auth/register`, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, email, password, passwordConfirm })
            });

            const responseData = await res.json();

            if(!res.ok) {
                return fail(res.status, { error: responseData.error || 'Unknown error.' });
            }

            const sessionCookie = res.headers.get('set-cookie');

            if(sessionCookie) {
                applyBackendCookie(sessionCookie, cookies);
            }

            // Redirect to returnTo
            // or to /dashboard (if cookie) or to /login (if no cookie)
            const redirectTarget = sanitizeReturnTo((data.get('returnTo') as string | null) ?? (sessionCookie ? "/dashboard" : "/login"));
            redirect(303, redirectTarget);

        } catch (error: any) {

            if (error?.status === 303) {
                throw error;
            }

            console.error('Error connecting to the backend:', error);
            return fail(500, { error: 'Failed to connect to the server.' });
        }

    }
};
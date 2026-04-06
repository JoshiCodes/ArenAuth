import {env} from "$env/dynamic/private";
import {PUBLIC_USE_TURNSTILE_CAPTCHA} from "$env/static/public";

interface TokenValidateResponse {
    'error-codes': string[];
    success: boolean;
    action: string;
    cdata: string;
}

export async function validateToken(token: string, secret: string) {
    const response = await fetch(
        'https://challenges.cloudflare.com/turnstile/v0/siteverify',
        {
            method: 'POST',
            headers: {
                'content-type': 'application/json',
            },
            body: JSON.stringify({
                response: token,
                secret: secret,
            }),
        },
    );

    const data: TokenValidateResponse = await response.json();

    return {
        // Return the status
        success: data.success,

        // Return the first error if it exists
        error: data['error-codes']?.length ? data['error-codes'][0] : null,
    };
}

export async function validateCaptcha(data: FormData) {

    if(!PUBLIC_USE_TURNSTILE_CAPTCHA) return true; // Always true - no verification.

    const token = data.get('cf-turnstile-response') as string;
    if(!token) {
        return { error: 'No Token' }
    }

    const SECRET = env.SECRET_TURNSTILE_KEY;

    const { success, error } = await validateToken(token, SECRET);

    if(!success) {
        return { error: error || 'Invalid CAPTCHA' }
    }

    return true;

}
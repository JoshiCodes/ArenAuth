import type {CookieSerializeOptions} from "cookie";

export function parseSetCookie(cookieString: string): { name: string; value: string; options: CookieSerializeOptions } {
    const parts = cookieString.split(';').map(part => part.trim());
    const [nameValue, ...optionsParts] = parts;
    const [name, ...valueParts] = nameValue.split('=');
    const value = valueParts.join('=');

    const options: CookieSerializeOptions = {};

    for (const option of optionsParts) {
        const [key, ...valParts] = option.split('=');
        const val = valParts.join('=');

        switch (key.toLowerCase()) {
            case 'expires':
                options.expires = new Date(val);
                break;
            case 'max-age':
                options.maxAge = parseInt(val, 10);
                break;
            case 'path':
                options.path = val;
                break;
            case 'domain':
                options.domain = val;
                break;
            case 'samesite':
                options.sameSite = val.toLowerCase() as 'lax' | 'strict' | 'none';
                break;
            case 'secure':
                options.secure = true;
                break;
            case 'httponly':
                options.httpOnly = true;
                break;
        }
    }

    return { name, value, options };
}

export function applyBackendCookie(
    rawSetCookie: string,
    svelteCookies: import('@sveltejs/kit').Cookies
) {
    const { name, value, options } = parseSetCookie(rawSetCookie);

    svelteCookies.set(name, value, {
        path: options.path || '/',
        ...options
    });
}
import {PUBLIC_BACKEND_URL} from "$env/static/public";

export async function apiCall(
    endpoint: string,
    options: RequestInit = {}
) {
    const url = `${PUBLIC_BACKEND_URL}${endpoint}`

    return fetch(url, {
        ...options,
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        }
    })
}
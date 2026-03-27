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

export async function fetchAvailableScopes() {

    let availableScopes: [{name: string, description: string}]|[] = [];

    const res = await apiCall("/oauth2/scopes");
    if(res.ok) {
        const json = await res.json();
        availableScopes = json || [];
    }

    return availableScopes;
}
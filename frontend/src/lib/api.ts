import { env } from "$env/dynamic/public";

export async function apiCall(
    endpoint: string,
    options: RequestInit = {},
    contentType: string|null = "application/json"
) {
    const url = `${env.PUBLIC_BACKEND_URL}${endpoint}`

    return fetch(url, {
        ...options,
        credentials: 'include',
        headers: {
            ...(contentType != null ? {"Content-Type": contentType} : {}),
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
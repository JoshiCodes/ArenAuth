import {BACKEND_URL} from "$lib/vars";

export const prerender = false;
export const ssr = false;

export const load = async ({ fetch, depends }) => {
	depends('app:me');

	const res = await fetch(BACKEND_URL + '/api/v1/internal/me', { credentials: 'include' });
	if (!res.ok) {
		return { me: null };
	}

	return { me: await res.json() };
};

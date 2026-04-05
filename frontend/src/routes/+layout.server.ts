import {INTERNAL_BACKEND_URL} from "$lib/server_vars";
import type {LayoutServerLoad} from "../../.svelte-kit/types/src/routes/(protected)/$types";

export const prerender = false;
export const ssr = true;

export const load = async ({ fetch, depends }) => {
	depends('app:me');

	const res = await fetch(INTERNAL_BACKEND_URL + '/api/v1/internal/me', { credentials: 'include' });
	if (!res.ok) {
		return { me: null };
	}

	return { me: await res.json() };
};

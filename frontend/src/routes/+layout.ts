export const prerender = false;
export const ssr = false;

export const load = async ({ fetch, depends }) => {
	depends('app:me');

	const res = await fetch('/api/internal/me', { credentials: 'include' });
	if (!res.ok) {
		return { me: null };
	}

	return { me: await res.json() };
};

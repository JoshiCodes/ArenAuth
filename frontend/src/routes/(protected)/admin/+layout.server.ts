import {redirect} from '@sveltejs/kit';

export const load = async ({ parent }) => {
    const { me } = await parent();

    if (!me?.roles?.includes('admin')) {
        throw redirect(303, `/login?returnTo=admin`);
    }

    return {
        me
    };
};

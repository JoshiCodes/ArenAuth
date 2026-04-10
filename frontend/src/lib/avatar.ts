import {env} from "$env/dynamic/public";

export function buildAvatarUrl(avatarHash: string|null, name: string|null, type: 'user'|'project', size: number= 512) {
    if(avatarHash) {
        return env.PUBLIC_BACKEND_URL + '/api/v1/avatar/' + type + '/' + avatarHash + '?size=' + size;
    }

    if(!name) name = "Unknown";
    return env.PUBLIC_BACKEND_URL + '/api/v1/avatar/default/' + name + '?size=' + size;
}

export function userAvatarUrl(avatarHash: string|null, name: string|null, size: number = 512): string {
        return buildAvatarUrl(avatarHash, name, 'user', size);
}

export function projectAvatarUrl(avatarHash: string|null, name: string|null, size: number = 512): string {
    return buildAvatarUrl(avatarHash, name, 'project', size);
}
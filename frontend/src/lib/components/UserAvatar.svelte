<script lang="ts">
    import {page} from "$app/state";
    import {userAvatarUrl} from "$lib/avatar";

    interface Props {
        user?: {
            name: string;
            avatarId: string;
        }
        size?: number;
        class?: string;
    }

    let { user, size = 512, class: className = '' }: Props = $props();

    const me = $derived((page.data.me as Me | null | undefined) ?? null);

    const avatarId = $derived(user?.avatarId ?? me?.avatarId ?? null);
    const name = $derived(user?.name ?? me?.username ?? 'User');

    const url = $derived(userAvatarUrl(avatarId, name, size));

</script>

<img src={url} alt={name} class={className} />
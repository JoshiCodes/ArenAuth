<script lang="ts">
    import { page } from "$app/state";
    import ThemeToggle from "$lib/components/ThemeToggle.svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import Link from "$lib/components/ui/Link.svelte";
    import { env } from "$env/dynamic/public";
    import {BACKEND_URL} from "$lib/vars";
    import {goto} from "$app/navigation";

    const PUBLIC_FALLBACK_IMG_URL = env.PUBLIC_FALLBACK_IMG_URL;

    $: me = (page.data.me as Me | null | undefined) ?? null;

    $: iconUrl = (me && me.avatarId) ? BACKEND_URL + "/api/v1/avatar/project/" + me.avatarId + "?size=512" : PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", encodeURIComponent(me ? me.username : "Unknown User"));

    async function handleLogout() {
        await goto("/logout", { replaceState: true })
    }

</script>

<nav class="fixed inset-x-0 top-0 z-50 w-full px-4 py-4 flex items-center">
    <div class="text-2xl font-bold text-primary"><a href="/">ArenAuth</a></div>
    <div class="ml-auto flex items-center gap-x-3 md:gap-x-6">
        {#if me}
            <ThemeToggle />
            <div class="hidden sm:flex items-center gap-2 text-sm text-zinc-800 dark:text-zinc-100">
                <Link href="/dashboard/profile" class="">
                    <img src={iconUrl} alt={me.username} class="h-[32px] rounded-full shadow-lg" height="32px" width="32px">
                </Link>
            </div>
            <div class="flex items-center gap-x-2">
                <a href="/dashboard">
                    <Button variant="secondary">
                        Dashboard
                    </Button>
                </a>
                <Button variant="ghost" onClick={handleLogout}>
                    Logout
                </Button>
            </div>
        {:else}
            <ThemeToggle />
            <a href="/login">
                <Button>
                    Login
                </Button>
            </a>
        {/if}
    </div>
</nav>

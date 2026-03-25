<script lang="ts">
    import { page } from "$app/stores";
    import { UserRound } from "lucide-svelte";
    import ThemeToggle from "$lib/components/ThemeToggle.svelte";
    import Button from "$lib/components/ui/Button.svelte";

    type Me = {
        username: string;
        userId: string;
        roles: string[];
    };

    $: me = ($page.data.me as Me | null | undefined) ?? null;
</script>

<nav class="fixed inset-x-0 top-0 z-50 w-full px-4 py-4 flex items-center">
    <div class="text-2xl font-bold text-primary"><a href="/">ArenAuth</a></div>
    <div class="ml-auto flex items-center gap-x-3 md:gap-x-6">
        {#if me}
            <div class="hidden sm:flex items-center gap-2 text-sm text-zinc-800 dark:text-zinc-100">
                <UserRound class="h-5 w-5" />
                <span>{me.username}</span>
            </div>
            <a href="/logout">
                <Button variant="secondary">
                    Logout
                </Button>
            </a>
        {:else}
            <a href="/login">
                <Button>
                    Login
                </Button>
            </a>
        {/if}
        <ThemeToggle />
    </div>
</nav>

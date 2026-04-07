<script lang="ts">
    import { page } from "$app/state";

    type Me = {
        username: string;
        roles: string[];
    };

    const me = $derived((page.data.me as Me | null | undefined) ?? null);

    function isActive(href: string) {
        const path = page.url.pathname;
        if (href === '/dashboard') {
            return path === '/dashboard';
        }
        return path.startsWith(href);
    }
</script>

<nav class="fixed top-0 left-0 w-64 h-screen bg-white/50 dark:bg-zinc-950/50 backdrop-blur-xl border-r border-zinc-200/50 dark:border-zinc-800/50 text-zinc-950 dark:text-white z-40">
    <div class="pt-24 flex flex-col gap-y-1 px-3">
        <a href="/dashboard" class="px-4 py-2.5 rounded-xl transition-all duration-200 hover:bg-zinc-200/50 dark:hover:bg-zinc-800/50 flex items-center gap-x-3 group
            {isActive('/dashboard') ? 'bg-zinc-200 dark:bg-zinc-800 text-violet-600 dark:text-violet-400' : 'text-zinc-600 dark:text-zinc-400'}">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5 transition-transform group-hover:scale-110">
                <path d="M11.47 3.841a.75.75 0 0 1 1.06 0l8.69 8.69a.75.75 0 1 0 1.06-1.061l-8.689-8.69a2.25 2.25 0 0 0-3.182 0l-8.69 8.69a.75.75 0 1 0 1.061 1.06l8.69-8.689Z" />
                <path d="m12 5.432 8.159 8.159c.03.03.06.058.091.086v6.198c0 1.035-.84 1.875-1.875 1.875H15a.75.75 0 0 1-.75-.75v-4.5a.75.75 0 0 0-.75-.75h-3a.75.75 0 0 0-.75.75V21a.75.75 0 0 1-.75.75H5.625a1.875 1.875 0 0 1-1.875-1.875v-6.198a2.29 2.29 0 0 0 .091-.086L12 5.432Z" />
            </svg>
            <span class="font-medium">Home</span>
        </a>
        <a href="/dashboard/projects" class="px-4 py-2.5 rounded-xl transition-all duration-200 hover:bg-zinc-200/50 dark:hover:bg-zinc-800/50 flex items-center gap-x-3 group
            {isActive('/dashboard/projects') ? 'bg-zinc-200 dark:bg-zinc-800 text-violet-600 dark:text-violet-400' : 'text-zinc-600 dark:text-zinc-400'}">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5 transition-transform group-hover:scale-110">
                <path d="M21 6.375c0 2.692-4.03 4.875-9 4.875S3 9.067 3 6.375 7.03 1.5 12 1.5s9 2.183 9 4.875Z" />
                <path d="M12 12.75c2.685 0 5.19-.586 7.078-1.609a8.283 8.283 0 0 0 1.897-1.384c.016.121.025.244.025.368C21 12.817 16.97 15 12 15s-9-2.183-9-4.875c0-.124.009-.247.025-.368a8.285 8.285 0 0 0 1.897 1.384C6.809 12.164 9.315 12.75 12 12.75Z" />
                <path d="M12 16.5c2.685 0 5.19-.586 7.078-1.609a8.282 8.282 0 0 0 1.897-1.384c.016.121.025.244.025.368 0 2.692-4.03 4.875-9 4.875s-9-2.183-9-4.875c0-.124.009-.247.025-.368a8.284 8.284 0 0 0 1.897 1.384C6.809 15.914 9.315 16.5 12 16.5Z" />
                <path d="M12 20.25c2.685 0 5.19-.586 7.078-1.609a8.282 8.282 0 0 0 1.897-1.384c.016.121.025.244.025.368 0 2.692-4.03 4.875-9 4.875s-9-2.183-9-4.875c0-.124.009-.247.025-.368a8.284 8.284 0 0 0 1.897 1.384C6.809 19.664 9.315 20.25 12 20.25Z" />
            </svg>
            <span class="font-medium">Projects</span>
        </a>
    </div>

    {#if me?.roles?.includes("admin")}
        <div class="mt-6 pt-6 border-t border-zinc-200/50 dark:border-zinc-800/50 px-3">
            <span class="px-4 text-[10px] font-bold uppercase tracking-wider text-zinc-500 mb-2 block">Admin</span>
            <a href="/admin" class="px-4 py-2.5 rounded-xl transition-all duration-200 hover:bg-zinc-200/50 dark:hover:bg-zinc-800/50 flex items-center gap-x-3 group text-zinc-600 dark:text-zinc-400">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5 transition-transform group-hover:scale-110">
                    <path d="M18.75 12.75h1.5a.75.75 0 0 0 0-1.5h-1.5a.75.75 0 0 0 0 1.5ZM12 6a.75.75 0 0 1 .75-.75h7.5a.75.75 0 0 1 0 1.5h-7.5A.75.75 0 0 1 12 6ZM12 18a.75.75 0 0 1 .75-.75h7.5a.75.75 0 0 1 0 1.5h-7.5A.75.75 0 0 1 12 18ZM3.75 6.75h1.5a.75.75 0 1 0 0-1.5h-1.5a.75.75 0 0 0 0 1.5ZM5.25 18.75h-1.5a.75.75 0 0 1 0-1.5h1.5a.75.75 0 0 1 0 1.5ZM3 12a.75.75 0 0 1 .75-.75h7.5a.75.75 0 0 1 0 1.5h-7.5A.75.75 0 0 1 3 12ZM9 3.75a2.25 2.25 0 1 0 0 4.5 2.25 2.25 0 0 0 0-4.5ZM12.75 12a2.25 2.25 0 1 1 4.5 0 2.25 2.25 0 0 1-4.5 0ZM9 15.75a2.25 2.25 0 1 0 0 4.5 2.25 2.25 0 0 0 0-4.5Z" />
                </svg>
                <span class="font-medium">Admin Panel</span>
            </a>
        </div>
    {/if}
</nav>
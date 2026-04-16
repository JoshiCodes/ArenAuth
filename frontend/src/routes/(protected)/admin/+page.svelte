<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";
    import AdminSidebar from "$lib/components/admin/AdminSidebar.svelte";
    import {onMount} from "svelte";
    import {toastStore} from "$lib/components/toasts/toastStore";
    import {apiCall} from "$lib/api";

    let sidebarOpen = $state(false);


    // Placeholder data
    const stats = $state([
        { label: "Total Users", value: "0", icon: "users", color: "text-blue-500", bg: "bg-blue-100 dark:bg-blue-900/30" },
        { label: "Active Projects", value: "0", icon: "folder", color: "text-green-500", bg: "bg-green-100 dark:bg-green-900/30" },
        { label: "Active OAuth Tokens", value: "0", icon: "login", color: "text-orange-500", bg: "bg-orange-100 dark:bg-orange-900/30" },
        { label: "New Users (24h)", value: "12", icon: "user-plus", color: "text-purple-500", bg: "bg-purple-100 dark:bg-purple-900/30" },
    ]);

    onMount(async () => {
        const res = await apiCall('/api/v1/internal/admin/stats');

        if(!res.ok) {
            console.error("Failed to fetch admin stats");
            console.error(await res.text());
            toastStore.add("Failed to fetch admin stats", { type: "error" });
            return;
        }

        const data = await res.json();
        console.log("Admin stats:", data);

        stats[0].value = data.users ?? 0;
        stats[1].value = data.projects ?? 0;
        stats[2].value = data.tokens ?? 0;
        stats[3].value = "?";

    });

    const recentActivity = [
        { user: "Alice Smith", action: "Created project 'My Store'", time: "2 hours ago" },
        { user: "Bob Jones", action: "Updated profile", time: "4 hours ago" },
        { user: "Charlie Brown", action: "Deleted project 'Old Test'", time: "Yesterday" },
    ];

</script>

<BackgroundGrid />

<Navbar bind:sidebarOpen />
<AdminSidebar bind:open={sidebarOpen} />

<DashboardComponent>
    <div class="max-w-6xl mx-auto space-y-10">
        <header class="relative p-8 md:p-12 rounded-3xl overflow-hidden text-zinc-900 dark:text-white">
            <div class="absolute inset-0 bg-linear-to-br from-red-200/70 to-orange-200/70 dark:from-red-500/25 dark:to-orange-500/25"></div>
            <div class="relative z-10">
                <h1 class="text-4xl md:text-5xl font-extrabold tracking-tight mb-4">
                    Admin <span class="text-transparent bg-clip-text bg-linear-to-r from-red-600 to-orange-600 dark:from-red-400 dark:to-orange-400">Panel</span>
                </h1>
                <p class="text-lg text-zinc-600 dark:text-zinc-400 max-w-2xl">
                    Manage the ArenAuth instance, users, and global settings.
                </p>
            </div>
        </header>

        <section class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {#each stats as stat}
                <div class="p-6 rounded-2xl bg-white/50 dark:bg-zinc-900/50 border border-zinc-200 dark:border-zinc-800 backdrop-blur-sm shadow-sm">
                    <div class="flex items-center justify-between mb-4">
                        <div class="p-2 rounded-lg {stat.bg} {stat.color}">
                            {#if stat.icon === 'users'}
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M15 19.128a9.38 9.38 0 0 0 2.625.372 9.337 9.337 0 0 0 4.121-.952 4.125 4.125 0 0 0-7.533-2.493M15 19.128v-.003c0-1.113-.285-2.16-.786-3.07M15 19.128v.106A12.318 12.318 0 0 1 8.624 21c-2.331 0-4.512-.645-6.374-1.766l-.001-.109a6.375 6.375 0 0 1 11.964-3.07M12 6.375a3.375 3.375 0 1 1-6.75 0 3.375 3.375 0 0 1 6.75 0Zm8.25 2.25a2.625 2.625 0 1 1-5.25 0 2.625 2.625 0 0 1 5.25 0Z" />
                                </svg>
                            {:else if stat.icon === 'folder'}
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="m7.875 14.25 1.214 1.942a2.25 2.25 0 0 0 1.908 1.058h2.006c.776 0 1.497-.4 1.908-1.058l1.214-1.942M2.41 9h4.636a2.25 2.25 0 0 1 1.872 1.002l.164.246a2.25 2.25 0 0 0 1.872 1.002h2.092a2.25 2.25 0 0 0 1.872-1.002l.164-.246A2.25 2.25 0 0 1 16.954 9h4.636M2.41 9a2.25 2.25 0 0 0-.16.832V12a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 12V9.832c0-.287-.055-.57-.16-.832M2.41 9a2.25 2.25 0 0 1 .382-.632l3.285-3.832a2.25 2.25 0 0 1 1.708-.786h8.43c.657 0 1.281.287 1.709.786l3.284 3.832c.163.19.291.404.382.632M4.5 20.25h15A2.25 2.25 0 0 0 21.75 18v-2.625c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125V18a2.25 2.25 0 0 0 2.25 2.25Z" />
                                </svg>
                            {:else if stat.icon === 'login'}
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 9V5.25A2.25 2.25 0 0 0 13.5 3h-6a2.25 2.25 0 0 0-2.25 2.25v13.5A2.25 2.25 0 0 0 7.5 21h6a2.25 2.25 0 0 0 2.25-2.25V15m3 0 3-3m0 0-3-3m3 3H9" />
                                </svg>
                            {:else}
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
                                </svg>
                            {/if}
                        </div>
                    </div>
                    <div class="text-2xl font-bold text-zinc-900 dark:text-white">{stat.value}</div>
                    <div class="text-sm text-zinc-500 dark:text-zinc-400">{stat.label}</div>
                </div>
            {/each}
        </section>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <section class="p-6 rounded-2xl bg-white/50 dark:bg-zinc-900/50 border border-zinc-200 dark:border-zinc-800 backdrop-blur-sm shadow-sm">
                <h3 class="text-xl font-bold mb-6 text-zinc-900 dark:text-white">Recent Activity</h3>
                <div class="space-y-4">
                    {#each recentActivity as activity}
                        <div class="flex items-start gap-4">
                            <div class="w-2 h-2 rounded-full bg-red-500 mt-2"></div>
                            <div>
                                <p class="text-zinc-900 dark:text-zinc-100 font-medium">{activity.user}</p>
                                <p class="text-sm text-zinc-600 dark:text-zinc-400">{activity.action}</p>
                                <p class="text-xs text-zinc-400 dark:text-zinc-500 mt-1">{activity.time}</p>
                            </div>
                        </div>
                    {/each}
                </div>
            </section>

            <section class="p-6 rounded-2xl bg-white/50 dark:bg-zinc-900/50 border border-zinc-200 dark:border-zinc-800 backdrop-blur-sm shadow-sm">
                <h3 class="text-xl font-bold mb-6 text-zinc-900 dark:text-white">Quick Links</h3>
                <div class="grid grid-cols-2 gap-4">
                    <a href="/admin/users/all" class="p-4 rounded-xl bg-zinc-100 dark:bg-zinc-800 hover:bg-red-100 dark:hover:bg-red-900/20 text-center transition-colors">
                        <p class="font-semibold text-zinc-900 dark:text-white">All Users</p>
                    </a>
                    <a href="/admin/projects/all" class="p-4 rounded-xl bg-zinc-100 dark:bg-zinc-800 hover:bg-red-100 dark:hover:bg-red-900/20 text-center transition-colors">
                        <p class="font-semibold text-zinc-900 dark:text-white">All Projects</p>
                    </a>
                </div>
            </section>
        </div>

    </div>
</DashboardComponent>

<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import BackgroundBlobs from "$lib/components/BackgroundBlobs.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";
    import AdminSidebar from "$lib/components/admin/AdminSidebar.svelte";
    import Card from "$lib/components/ui/Card.svelte";
    import {page} from "$app/state";

    const userId = $derived(String(page.params.user));
    let { data } = $props();

    let sidebarOpen = $state(false);

    // Mock-Daten für das Design
    const user: FullUser = {
        id: userId,
        name: "Test User",
        email: "test@example.com",
        avatarId: null,
        roles: ["USER", "ADMIN"],
        projects: ["Project Alpha", "Project Beta"]
    };

    const getRoleColor = (role: string) => {
        switch (role.toUpperCase()) {
            case 'ADMIN': return 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 border-red-200 dark:border-red-800';
            case 'USER': return 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400 border-blue-200 dark:border-blue-800';
            default: return 'bg-zinc-100 text-zinc-700 dark:bg-zinc-800 dark:text-zinc-400 border-zinc-200 dark:border-zinc-700';
        }
    };
</script>

<BackgroundBlobs classes="bg-red-500 dark:bg-red-700/50" />
<BackgroundGrid />

<Navbar bind:sidebarOpen />
<AdminSidebar bind:open={sidebarOpen} />

<DashboardComponent>
    <div class="max-w-6xl mx-auto space-y-6">
        <!-- Header -->
        <header class="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
                <nav class="flex items-center gap-2 text-sm text-zinc-500 dark:text-zinc-400 mb-2">
                    <a href="/admin/users/all" class="hover:text-red-600 dark:hover:text-red-400 transition-colors">Users</a>
                    <span>/</span>
                    <span class="text-zinc-900 dark:text-white font-medium">{user.name}</span>
                </nav>
                <h1 class="text-3xl font-extrabold tracking-tight text-zinc-900 dark:text-white">
                    User <span class="text-red-600 dark:text-red-400">Details</span>
                </h1>
                <p class="text-zinc-500 dark:text-zinc-400 mt-1">
                    Manage and view detailed information for this user.
                </p>
            </div>
            <div class="flex items-center gap-3">
                <button class="px-4 py-2 rounded-xl bg-zinc-100 dark:bg-zinc-800 text-zinc-900 dark:text-white font-medium hover:bg-zinc-200 dark:hover:bg-zinc-700 transition-colors border border-zinc-200 dark:border-zinc-700">
                    Deactivate
                </button>
                <button class="px-4 py-2 rounded-xl bg-red-600 text-white font-medium hover:bg-red-700 transition-colors shadow-lg shadow-red-500/20">
                    Delete User
                </button>
            </div>
        </header>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <!-- Left Column: Profile Info -->
            <div class="lg:col-span-1 space-y-6">
                <Card>
                    <div class="flex flex-col items-center text-center">
                        <div class="w-24 h-24 rounded-2xl bg-gradient-to-br from-red-500 to-orange-500 flex items-center justify-center text-white text-3xl font-bold mb-4 shadow-xl">
                            {user.name.charAt(0)}
                        </div>
                        <h2 class="text-xl font-bold text-zinc-900 dark:text-white">{user.name}</h2>
                        <p class="text-zinc-500 dark:text-zinc-400 text-sm mb-4">{user.id}</p>
                        
                        <div class="w-full pt-4 border-t border-zinc-100 dark:border-zinc-800 space-y-3">
                            <div class="flex items-center justify-between text-sm">
                                <span class="text-zinc-500 dark:text-zinc-400">Email</span>
                                <span class="text-zinc-900 dark:text-white font-medium">{user.email || 'N/A'}</span>
                            </div>
                            <div class="flex items-center justify-between text-sm">
                                <span class="text-zinc-500 dark:text-zinc-400">Status</span>
                                <span class="flex items-center gap-1.5 text-emerald-600 dark:text-emerald-400 font-medium">
                                    <span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                                    Active
                                </span>
                            </div>
                        </div>
                    </div>
                </Card>

                <Card>
                    <h3 class="font-bold text-zinc-900 dark:text-white mb-4">Roles</h3>
                    <div class="flex flex-wrap gap-2">
                        {#each user.roles as role}
                            <span class="px-2.5 py-1 rounded-lg text-xs font-bold border {getRoleColor(role)}">
                                {role}
                            </span>
                        {/each}
                        <button class="px-2.5 py-1 rounded-lg text-xs font-bold bg-zinc-100 dark:bg-zinc-800 text-zinc-600 dark:text-zinc-400 border border-zinc-200 dark:border-zinc-700 hover:bg-zinc-200 dark:hover:bg-zinc-600 transition-colors">
                            + Add Role
                        </button>
                    </div>
                </Card>
            </div>

            <!-- Right Column: Details & Projects -->
            <div class="lg:col-span-2 space-y-6">
                <Card>
                    <h3 class="font-bold text-zinc-900 dark:text-white mb-6 flex items-center gap-2">
                        <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                        General Information
                    </h3>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div class="space-y-1">
                            <label class="text-xs font-bold uppercase tracking-wider text-zinc-400 dark:text-zinc-500" for="username">Username</label>
                            <input id="username" type="text" value={user.name} class="w-full bg-zinc-50 dark:bg-zinc-950 border border-zinc-200 dark:border-zinc-800 rounded-xl px-4 py-2 text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-red-500/50 transition-all" readonly />
                        </div>
                        <div class="space-y-1">
                            <label class="text-xs font-bold uppercase tracking-wider text-zinc-400 dark:text-zinc-500" for="email">Email Address</label>
                            <input id="email" type="email" value={user.email} class="w-full bg-zinc-50 dark:bg-zinc-950 border border-zinc-200 dark:border-zinc-800 rounded-xl px-4 py-2 text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-red-500/50 transition-all" readonly />
                        </div>
                    </div>
                    <div class="mt-6">
                         <button class="px-6 py-2 rounded-xl bg-zinc-900 dark:bg-white text-white dark:text-zinc-900 font-bold hover:opacity-90 transition-opacity">
                            Save Changes
                        </button>
                    </div>
                </Card>

                <Card>
                    <div class="flex items-center justify-between mb-6">
                        <h3 class="font-bold text-zinc-900 dark:text-white flex items-center gap-2">
                            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5 text-red-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
                            Associated Projects
                        </h3>
                        <span class="text-xs font-medium px-2 py-0.5 rounded-full bg-zinc-100 dark:bg-zinc-800 text-zinc-600 dark:text-zinc-400">
                            {user.projects?.length || 0} Projects
                        </span>
                    </div>
                    
                    {#if user.projects && user.projects.length > 0}
                        <div class="divide-y divide-zinc-100 dark:divide-zinc-800">
                            {#each user.projects as project}
                                <div class="py-3 flex items-center justify-between group">
                                    <div class="flex items-center gap-3">
                                        <div class="w-10 h-10 rounded-lg bg-zinc-100 dark:bg-zinc-800 flex items-center justify-center text-zinc-500 dark:text-zinc-400 group-hover:bg-red-50 dark:group-hover:bg-red-900/20 group-hover:text-red-600 dark:group-hover:text-red-400 transition-colors">
                                            <svg xmlns="http://www.w3.org/2000/svg" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
                                        </div>
                                        <div>
                                            <p class="text-zinc-900 dark:text-white font-medium">{project}</p>
                                            <p class="text-xs text-zinc-500 dark:text-zinc-400">Owner</p>
                                        </div>
                                    </div>
                                    <button class="p-2 rounded-lg hover:bg-zinc-100 dark:hover:bg-zinc-800 text-zinc-400 hover:text-zinc-900 dark:hover:text-white transition-colors">
                                        <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
                                    </button>
                                </div>
                            {/each}
                        </div>
                    {:else}
                        <div class="text-center py-8">
                            <p class="text-zinc-500 dark:text-zinc-400 italic">No projects associated with this user.</p>
                        </div>
                    {/if}
                </Card>
            </div>
        </div>
    </div>
</DashboardComponent>

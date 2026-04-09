<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import BackgroundBlobs from "$lib/components/BackgroundBlobs.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";
    import AdminSidebar from "$lib/components/admin/AdminSidebar.svelte";
    import {slide} from 'svelte/transition';

    let sidebarOpen = $state(false);
    let searchOpen = $state(false);
    let searchQuery = $state("");

    // Placeholder data
    let projects = $state([
        { id: 1, name: "Alpha Project", owner: "Alice Smith", members: 5, status: "Active" },
        { id: 2, name: "Beta App", owner: "Bob Jones", members: 2, status: "Active" },
        { id: 3, name: "Gamma Service", owner: "Charlie Brown", members: 12, status: "Maintenance" },
        { id: 4, name: "Delta Website", owner: "David Wilson", members: 1, status: "Active" },
        { id: 5, name: "Epsilon Tool", owner: "Eve Adams", members: 8, status: "Archived" },
    ]);

    let filteredProjects = $derived(
        projects.filter(p => 
            p.name.toLowerCase().includes(searchQuery.toLowerCase()) || 
            p.owner.toLowerCase().includes(searchQuery.toLowerCase())
        )
    );

    function toggleSearch() {
        searchOpen = !searchOpen;
    }

    function editProject(id: number) {
        console.log("Editing project", id);
        // Logic for editing would go here
    }
</script>

<BackgroundBlobs classes="bg-red-500 dark:bg-red-700/50" />
<BackgroundGrid />

<Navbar bind:sidebarOpen />
<AdminSidebar bind:open={sidebarOpen} />

<DashboardComponent>
    <div class="max-w-6xl mx-auto space-y-6">
        <header class="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
                <h1 class="text-3xl font-extrabold tracking-tight text-zinc-900 dark:text-white">
                    All <span class="text-red-600 dark:text-red-400">Projects</span>
                </h1>
                <p class="text-zinc-600 dark:text-zinc-400">
                    Manage and search all projects across the platform.
                </p>
            </div>
            <button 
                onclick={toggleSearch}
                class="flex items-center gap-2 px-4 py-2 rounded-xl bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 hover:border-red-500 transition-colors shadow-sm"
            >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z" />
                </svg>
                <span>Search Projects</span>
            </button>
        </header>

        {#if searchOpen}
            <div transition:slide class="p-4 rounded-2xl bg-white/50 dark:bg-zinc-900/50 border border-zinc-200 dark:border-zinc-800 backdrop-blur-sm">
                <input 
                    type="text" 
                    bind:value={searchQuery}
                    placeholder="Search by project name or owner..." 
                    class="w-full px-4 py-2 rounded-xl bg-white dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 focus:outline-hidden focus:ring-2 focus:ring-red-500 transition-all"
                />
            </div>
        {/if}

        <div class="overflow-hidden rounded-2xl border border-zinc-200 dark:border-zinc-800 bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm shadow-sm">
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <thead>
                        <tr class="border-b border-zinc-200 dark:border-zinc-800 bg-zinc-50/50 dark:bg-zinc-800/50">
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white">Project Name</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white">Owner</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white text-center">Members</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white">Status</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-zinc-200 dark:divide-zinc-800">
                        {#each filteredProjects as project}
                            <tr class="hover:bg-zinc-100/50 dark:hover:bg-zinc-800/50 transition-colors">
                                <td class="px-6 py-4 text-sm text-zinc-900 dark:text-zinc-100 font-bold">{project.name}</td>
                                <td class="px-6 py-4 text-sm text-zinc-600 dark:text-zinc-400">{project.owner}</td>
                                <td class="px-6 py-4 text-sm text-center text-zinc-600 dark:text-zinc-400">{project.members}</td>
                                <td class="px-6 py-4 text-sm">
                                    <span class="px-2 py-1 rounded-md text-xs font-semibold 
                                        {project.status === 'Active' ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' : 
                                         project.status === 'Maintenance' ? 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400' :
                                         'bg-zinc-100 text-zinc-700 dark:bg-zinc-800 dark:text-zinc-400'}">
                                        {project.status}
                                    </span>
                                </td>
                                <td class="px-6 py-4 text-right">
                                    <button 
                                        onclick={() => editProject(project.id)}
                                        class="p-2 rounded-lg hover:bg-zinc-200 dark:hover:bg-zinc-700 text-zinc-600 dark:text-zinc-400 transition-colors"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                                            <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
                                        </svg>
                                    </button>
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>
            {#if filteredProjects.length === 0}
                <div class="p-12 text-center">
                    <p class="text-zinc-500 dark:text-zinc-400">No projects found matching your search.</p>
                </div>
            {/if}
        </div>
    </div>
</DashboardComponent>

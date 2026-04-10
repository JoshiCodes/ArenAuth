<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import BackgroundBlobs from "$lib/components/BackgroundBlobs.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";
    import AdminSidebar from "$lib/components/admin/AdminSidebar.svelte";
    import {slide} from 'svelte/transition';
    import {onMount} from "svelte";
    import {apiCall} from "$lib/api";
    import {toastStore} from "$lib/components/toasts/toastStore";
    import {goto} from "$app/navigation";

    let { data } = $props();

    let sidebarOpen = $state(false);
    let searchOpen = $state(false);
    let searchQuery = $state("");

    let pageSize = $state(25);

    let users = $state<FullUser[]>([]);
    let totalCount = $state(0);
    let currentPage = $state(0);
    let totalPages = $state(0);
    let currentPageSize = $state(0);

    let filteredUsers = $derived(
        users.filter(u =>
            u.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
            (u.email && u.email.toLowerCase().includes(searchQuery.toLowerCase()))
        )
    );

    async function loadUsers(page: number, size: number) {
        const res = await apiCall(`/api/v1/internal/admin/users?page=${page}&size=${size}`);

        if(res.ok) {
            const json = await res.json();
            if(json && json.users) {
                users = json.users;
                currentPage = json.page;
                totalCount = json.total_count;
                totalPages = json.total_pages;
                currentPageSize = json.size;
                console.log("Users loaded:", JSON.stringify(users, null, 2));
            } else {
                console.error("Invalid response format:", json);
                toastStore.add("Failed to load users. Invalid response format.", {
                    type: "error"
                });
            }
        } else {
            console.error(await res.text())
            console.error("Failed to fetch users:", res.statusText);
            toastStore.add("Failed to load users. Please try again later.", {
                type: "error"
            });
        }
    }

    onMount(async () => {
        await loadUsers(currentPage, pageSize);
    })

    function updatePageSize(size: number) {
        pageSize = size;
        currentPage = 0; // Reset to first page
        loadUsers(currentPage, pageSize);
    }

    function goToPage(page: number) {
        if(page < 0 || page >= totalPages) return;
        currentPage = page;
        loadUsers(currentPage, pageSize);
    }

    function toggleSearch() {
        searchOpen = !searchOpen;
    }

    function deleteUser(id: string) {
        console.log("Deleting user", id);
        toastStore.add("User deletion is not implemented yet.", {
            type: "warning"
        });
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
                    All <span class="text-red-600 dark:text-red-400">Users</span>
                </h1>
                <p class="text-zinc-600 dark:text-zinc-400">
                    Manage and search all users in the system.
                </p>
            </div>
            <button 
                onclick={toggleSearch}
                class="flex items-center gap-2 px-4 py-2 rounded-xl bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 hover:border-red-500 transition-colors shadow-sm"
            >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z" />
                </svg>
                <span>Search Users</span>
            </button>
        </header>

        {#if searchOpen}
            <div transition:slide class="p-4 rounded-2xl bg-white/50 dark:bg-zinc-900/50 border border-zinc-200 dark:border-zinc-800 backdrop-blur-sm">
                <input 
                    type="text" 
                    bind:value={searchQuery}
                    placeholder="Search by name or email..." 
                    class="w-full px-4 py-2 rounded-xl bg-white dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 focus:outline-hidden focus:ring-2 focus:ring-red-500 transition-all"
                />
            </div>
        {/if}

        <div class="overflow-hidden rounded-2xl border border-zinc-200 dark:border-zinc-800 bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm shadow-sm">
            <div class="overflow-x-auto">
                <table class="w-full text-left border-collapse">
                    <thead>
                        <tr class="border-b border-zinc-200 dark:border-zinc-800 bg-zinc-50/50 dark:bg-zinc-800/50">
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white">Name</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white">Email</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white">Role</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white">Projects</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white">Notes</th>
                            <th class="px-6 py-4 text-sm font-semibold text-zinc-900 dark:text-white text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-zinc-200 dark:divide-zinc-800">
                        {#each filteredUsers as user}
                            <tr class="hover:bg-zinc-100/50 dark:hover:bg-zinc-800/50 transition-colors">
                                <td class="px-6 py-4 text-sm text-zinc-900 dark:text-zinc-100 font-medium">{user.name}</td>
                                <td class="px-6 py-4 text-sm text-zinc-600 dark:text-zinc-400">{user.email}</td>
                                <td class="px-6 py-4 text-sm">
                                    <span class="px-2 py-1 rounded-md text-xs font-semibold {user.roles.includes('admin') ? 'bg-red-200 text-red-700 dark:bg-red-900/30 dark:text-red-400' : 'bg-zinc-200 text-zinc-700 dark:bg-zinc-800 dark:text-zinc-400'}">
                                        {user.roles[0]}
                                    </span>
                                </td>
                                <td class="px-6 py-4 text-sm">
                                    {user.projects?.length} {user.projects?.length === 1 ? 'project' : 'projects'}
                                </td>
                                <td class="px-6 py-4 text-sm">
                                    <div>
                                        {#if data.me && data.me.userId === user.id }
                                        <span class="px-2 py-1 rounded-md text-xs font-semibold bg-sky-100 text-sky-700 dark:bg-sky-800 dark:text-sky-400">
                                            You
                                        </span>
                                        {/if}
                                    </div>
                                </td>
                                <td class="px-6 py-4 text-right">
                                    <button
                                            onclick={() => goto("/admin/users/" + user.id)}
                                            class="p-2 rounded-lg hover:bg-zinc-200 dark:hover:bg-zinc-700 text-zinc-600 dark:text-zinc-400 transition-colors"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                                            <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L10.582 16.07a4.5 4.5 0 0 1-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 0 1 1.13-1.897l8.932-8.931Zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0 1 15.75 21H5.25A2.25 2.25 0 0 1 3 18.75V8.25A2.25 2.25 0 0 1 5.25 6H10" />
                                        </svg>
                                        <span class="sr-only">Edit</span>
                                    </button>
                                    <button
                                            onclick={() => deleteUser(user.id)}
                                            class="p-2 rounded-lg hover:bg-red-200 dark:hover:bg-red-400/50 text-red-600 dark:text-red-400 transition-colors"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                                            <path stroke-linecap="round" stroke-linejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" />
                                        </svg>
                                        <span class="sr-only">Delete</span>
                                    </button>
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>
            {#if filteredUsers.length === 0}
                <div class="p-12 text-center">
                    <p class="text-zinc-500 dark:text-zinc-400">No users found matching your search.</p>
                </div>
            {/if}

            <div class="flex flex-col sm:flex-row items-center justify-between gap-4 px-6 py-4 border-t border-zinc-200 dark:border-zinc-800 bg-zinc-50/50 dark:bg-zinc-800/50">
                <div class="flex items-center gap-4 text-sm text-zinc-600 dark:text-zinc-400">
                    <div class="flex items-center gap-2">
                        <span>Show</span>
                        <select 
                            value={pageSize}
                            onchange={(e) => updatePageSize(Number(e.currentTarget.value))}
                            class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-700 rounded-lg px-2 py-1 focus:outline-hidden focus:ring-2 focus:ring-red-500"
                        >
                            <option value={25}>25</option>
                            <option value={50}>50</option>
                            <option value={100}>100</option>
                        </select>
                        <span>entries</span>
                    </div>
                    <span class="hidden sm:inline text-zinc-300 dark:text-zinc-700">|</span>
                    <p>
                        Showing <span class="font-medium text-zinc-900 dark:text-white">{currentPage * pageSize + 1}</span> to 
                        <span class="font-medium text-zinc-900 dark:text-white">{Math.min((currentPage + 1) * pageSize, totalCount)}</span> of 
                        <span class="font-medium text-zinc-900 dark:text-white">{totalCount}</span> entries
                    </p>
                </div>

                <div class="flex items-center gap-2">
                    <button 
                        onclick={() => goToPage(currentPage - 1)}
                        disabled={currentPage === 0}
                        class="p-2 rounded-lg border border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 hover:bg-zinc-50 dark:hover:bg-zinc-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                            <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 19.5 8.25 12l7.5-7.5" />
                        </svg>
                    </button>
                    <div class="px-4 py-1.5 rounded-lg border border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 text-sm font-medium">
                        Page <span class="text-red-600 dark:text-red-400">{currentPage + 1}</span> of {totalPages}
                    </div>
                    <button 
                        onclick={() => goToPage(currentPage + 1)}
                        disabled={currentPage >= totalPages - 1}
                        class="p-2 rounded-lg border border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900 hover:bg-zinc-50 dark:hover:bg-zinc-800 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                            <path stroke-linecap="round" stroke-linejoin="round" d="m8.25 4.5 7.5 7.5-7.5 7.5" />
                        </svg>
                    </button>
                </div>
            </div>
        </div>
    </div>
</DashboardComponent>

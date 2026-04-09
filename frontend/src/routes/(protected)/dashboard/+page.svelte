<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import BackgroundBlobs from "$lib/components/BackgroundBlobs.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import {onMount} from "svelte";
    import {apiCall} from "$lib/api";
    import ProjectCard from "$lib/components/dashboard/ProjectCard.svelte";
    import ProjectCardSkeleton from "$lib/components/dashboard/ProjectCardSkeleton.svelte";
    import InfoCard from "$lib/components/ui/InfoCard.svelte";

    let { data } = $props();

    let sidebarOpen = $state(false);

    let projects = $state([]);
    let isLoadingProjects = $state(true);

    onMount(() => {
        apiCall("/api/v1/internal/projects")
            .then(async (res) => {
                if (res.ok) {
                    projects = await res.json();
                }
                isLoadingProjects = false;
            });
    });
</script>

<BackgroundBlobs />
<BackgroundGrid />

<Navbar bind:sidebarOpen />
<DashboardSidebar bind:open={sidebarOpen} />

<DashboardComponent>
    <div class="max-w-6xl mx-auto space-y-10">
        <!-- Welcome Section -->
        <header class="relative p-8 md:p-12 rounded-3xl overflow-hidden">
            <div class="absolute inset-0 bg-gradient-to-br from-violet-200/70 to-fuchsia-200/70 dark:from-violet-500/25 dark:to-fuchsia-500/25"></div>
            <div class="relative z-10">
                <h1 class="text-4xl md:text-5xl font-extrabold tracking-tight text-zinc-900 dark:text-white mb-4">
                    Welcome back, <span class="text-transparent bg-clip-text bg-linear-to-r from-violet-600 to-fuchsia-600 dark:from-violet-400 dark:to-fuchsia-400">{data.me.username}</span>!
                </h1>
                <p class="text-lg text-zinc-600 dark:text-zinc-400 max-w-2xl">
                    Great to see you again. Here's a quick overview of your authentication universe. Manage your projects, monitor security, and scale your applications.
                </p>
                
                <div class="mt-8 flex flex-wrap gap-4">
                    <a href="/dashboard/projects/new">
                        <Button variant="primary">
                            Create New Project
                        </Button>
                    </a>
                    <a href="/dashboard/profile">
                        <Button variant="ghost">
                            Account Settings
                        </Button>
                    </a>
                </div>
            </div>
        </header>

        <!-- Quick Stats / Info Cards -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <InfoCard
                title="Active Projects"
                value={isLoadingProjects ? '...' : String(projects.length)}
                description="Ready for authentication"
            >
                {#snippet icon()}
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M6 6.75h12m-12 0a2.25 2.25 0 0 0-2.25 2.25v7.5a2.25 2.25 0 0 0 2.25 2.25h12a2.25 2.25 0 0 0 2.25-2.25v-7.5a2.25 2.25 0 0 0-2.25-2.25m-12 0V4.5A2.25 2.25 0 0 1 8.25 2.25h7.5A2.25 2.25 0 0 1 18 4.5v2.25m-9 0h6" />
                    </svg>
                {/snippet}
            </InfoCard>

            <InfoCard
                title="Authorized Projects"
                value={isLoadingProjects ? '...' : String(projects.length) /* TODO */}
                valueClass="text-sky-500"
                description="Projects with active tokens"
                iconBgClass="bg-sky-500/10"
                iconClass="text-sky-600 dark:text-sky-400"
            >
                {#snippet icon()}
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M9 12.75 11.25 15 15 9.75m-3-7.036A11.959 11.959 0 0 1 3.598 6 11.99 11.99 0 0 0 3 9.749c0 5.592 3.824 10.29 9 11.623 5.176-1.332 9-6.03 9-11.622 0-1.31-.21-2.571-.598-3.751h-.152c-3.196 0-6.1-1.248-8.25-3.285Z" />
                    </svg>
                {/snippet}
            </InfoCard>
        </div>

        <!-- Recent Projects Section -->
        <section>
            <div class="flex items-center justify-between mb-6">
                <h2 class="text-2xl font-bold text-zinc-900 dark:text-white">Your Recent Projects</h2>
                {#if projects.length > 0}
                    <a href="/dashboard/projects" class="text-sm font-medium text-violet-600 dark:text-violet-400 hover:underline">
                        View All
                    </a>
                {/if}
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                {#if isLoadingProjects}
                    <ProjectCardSkeleton />
                    <ProjectCardSkeleton />
                    <ProjectCardSkeleton />
                    <ProjectCardSkeleton />
                {:else}
                    {#if projects.length > 0}
                        {#each projects.slice(0, 4) as project}
                            <ProjectCard {project} />
                        {/each}
                    {:else}
                        <div class="col-span-full p-12 text-center rounded-2xl border-2 border-dashed border-zinc-200 dark:border-zinc-800">
                            <p class="text-zinc-500 mb-4">You haven't created any projects yet.</p>
                            <a href="/dashboard/projects/new">
                                <Button variant="secondary" size="sm">Get Started</Button>
                            </a>
                        </div>
                    {/if}
                {/if}
            </div>
        </section>

        <!-- Documentation Card -->
        <section class="p-8 rounded-2xl bg-gray-300/25 dark:bg-zinc-950 text-white relative overflow-hidden group">
            <div class="text-zinc-950 dark:text-gray-100 absolute top-0 right-0 p-8 opacity-10 group-hover:scale-110 transition-transform duration-500">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1" stroke="currentColor" class="size-48">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M12 6.042A8.967 8.967 0 0 0 6 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 0 1 6 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 0 1 6-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0 0 18 18a8.967 8.967 0 0 0-6 2.292m0-14.25v14.25" />
                </svg>
            </div>
            <div class="relative z-10">
                <h2 class="text-2xl font-bold mb-2 text-zinc-950 dark:text-gray-50">Need help integrating?</h2>
                <p class="text-zinc-800 dark:text-zinc-400 max-w-xl mb-6">
                    Our documentation covers everything from basic OAuth2 setup to advanced security configurations. Explore examples for Svelte, React, Vue, and more.
                </p>
                <a href="/docs">
                    <Button variant="primary">
                        Explore Documentation
                    </Button>
                </a>
            </div>
        </section>
    </div>
</DashboardComponent>
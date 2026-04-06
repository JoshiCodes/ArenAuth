<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import ProjectCard from "$lib/components/dashboard/ProjectCard.svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import Link from "$lib/components/ui/Link.svelte";
    import {onMount} from "svelte";
    import ProjectCardSkeleton from "$lib/components/dashboard/ProjectCardSkeleton.svelte";
    import {BACKEND_URL} from "$lib/vars";
    import BackgroundBlob from "$lib/components/BackgroundBlob.svelte";

    let loading = true;
    let projects: { name: string, id: string, avatarId: string }[] = [];

    onMount(() => {

        fetch(BACKEND_URL + "/api/v1/internal/projects", {credentials: 'include'})
            .then(res => res.json())
            .then(data => {
                console.log(data);
                projects = data;
                loading = false;
            });

    })

</script>

<Navbar />
<DashboardSidebar />

<BackgroundBlob class="top-32 right-6 w-125 h-32 rounded-lg" />

<DashboardComponent>
    <div class="mt-6 w-full h-full">
        <div class="mr-4 md:mr-6 flex justify-between content-center items-center">
            <div>
                <h1 class="text-4xl font-bold text-zinc-700 dark:text-zinc-50">Your Projects</h1>
                <p class="text-zinc-800/70 dark:text-zinc-200/80">
                    View and edit all of your projects.
                    To use them in your application, view our <Link href="#">Documentation</Link>.
                </p>
            </div>
            <a href="/dashboard/projects/new">
                <Button size="sm" variant="secondary">
                    New project
                </Button>
            </a>
        </div>
        <div class="mt-4 w-full h-full grid md:grid-cols-5 gap-2 md:gap-6">
            {#if loading}
                <ProjectCardSkeleton />
                <ProjectCardSkeleton />
                <ProjectCardSkeleton />
                <ProjectCardSkeleton />
                <ProjectCardSkeleton />
            {:else}
                {#if projects.length > 0}
                    {#each projects as project}
                        <ProjectCard project={project} />
                    {/each}
                {:else}
                    <div class="text-center col-span-full row-span-full mt-4 md:mt-16 w-full h-full flex justify-center content-center items-center">
                        <p class="text-lg text-zinc-800/70 dark:text-zinc-200/70">
                            You don't have any projects yet. <br>
                            How about <Link href="/dashboard/projects/new">creating one</Link>?
                        </p>
                    </div>
                {/if}
            {/if}
        </div>
    </div>
</DashboardComponent>
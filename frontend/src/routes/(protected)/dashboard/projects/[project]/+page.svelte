<script lang="ts">
    import { page } from "$app/stores";
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import Link from "$lib/components/ui/Link.svelte";

    const projectId = String($page.params.project);
    const projectName = projectId;
</script>

<Navbar />
<DashboardSidebar />

<DashboardComponent>
    <div class="mt-6 w-full h-full">
        <div class="mr-4 md:mr-6">
            <p class="text-sm text-zinc-700/70 dark:text-zinc-300/70">
                <Link href="/dashboard/projects">Back to projects</Link>
            </p>

            <h1 class="mt-2 text-4xl font-bold text-zinc-700 dark:text-zinc-50">{projectName}</h1>

            <p class="mt-2 text-zinc-800/70 dark:text-zinc-200/80">
                This is the project detail page for now without a backend request.
            </p>

            <div class="mt-6 inline-flex items-center rounded-full border border-zinc-300 bg-zinc-100 px-3 py-1 text-sm text-zinc-800 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-200">
                Project ID: <span class="ml-2 font-mono">{projectId}</span>
            </div>
        </div>
    </div>
</DashboardComponent>


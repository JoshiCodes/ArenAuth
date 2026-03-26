<script lang="ts">
    import { page } from "$app/stores";
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import Link from "$lib/components/ui/Link.svelte";
    import {onMount} from "svelte";
    import {apiCall} from "$lib/api";
    import {PUBLIC_FALLBACK_IMG_URL} from "$env/static/public";
    import Button from "$lib/components/ui/Button.svelte";
    import BarInput from "$lib/components/ui/BarInput.svelte";

    $: projectId = String($page.params.project);
    $: data = { id: '', name: "Unknown", description: "", imageBlob: "" };

    $: error = '';
    $: isLoading = true;

    onMount(() => {
        apiCall(`/api/internal/projects/${projectId}`)
            .then(async (res) => {
                if (res.ok) {
                    const json = await res.json();

                    if(!json.id || !json.name) {
                        console.log(JSON.stringify(json));
                        error = "Invalid response from Server.";
                        return;
                    }
                    data = {
                        id: json.id,
                        name: json.name,
                        description: json.description || "",
                        imageBlob: json.imageBlob || null
                    };

                } else {
                    error = `Failed to fetch project data: ${res.status} ${res.statusText}`;
                }
                isLoading = false;
            });
    })


</script>

<Navbar />
<DashboardSidebar />

<DashboardComponent>
    <div class="mt-6 w-full h-full">
        <div class="mr-4 md:mr-6">
            <p class="text-sm text-zinc-700/70 dark:text-zinc-300/70">
                <Link href="/dashboard/projects">Back to projects</Link>
            </p>

            <h1 class="mt-2 text-4xl font-bold text-zinc-700 dark:text-zinc-50">{data.name}</h1>
            <div class="mt-6 inline-flex items-center rounded-full border border-zinc-300 bg-zinc-100 px-3 py-1 text-sm text-zinc-800 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-200">
                Project ID: <span class="ml-2 font-mono">{projectId}</span>
            </div>

            {#if isLoading}
                <p class="mt-4 text-zinc-600 dark:text-zinc-400">Loading project data...</p>
            {:else if error}
                <p class="mt-4 text-red-600 dark:text-red-400">{error}</p>
            {:else}
                <div class="mt-6 grid md:grid-cols-2">
                    <div class="">
                        <h2 class="text-2xl font-semibold text-zinc-700 dark:text-zinc-50">Description</h2>
                        <p class="mt-2 text-zinc-700 dark:text-zinc-300">{data.description || "No description provided."}</p>
                    </div>
                    <div>
                        <img src={data.imageBlob || PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", data.name)} alt="Project Image" class="w-1/2 h-auto rounded-lg object-cover shadow-lg" />
                    </div>
                    <div class="w-full">
                        <div class="flex-row justify-center content-center items-center">
                            <BarInput id="clientToken" label="Client Secret" class="col-span-3" />
                            <Button class="col-span-1">
                                Generate New
                            </Button>
                        </div>
                    </div>

                </div>
            {/if}

        </div>
    </div>
</DashboardComponent>


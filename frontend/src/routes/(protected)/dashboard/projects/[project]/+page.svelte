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
    import Modal from "$lib/components/Modal.svelte";

    $: projectId = String($page.params.project);
    $: data = { id: '', name: "Unknown", description: "", imageBlob: "" };

    $: error = '';
    $: isLoading = true;

    $: secret = '';

    $: resetModalShown = false;

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

    function resetToken() {
        resetModalShown = false;
        apiCall(`/api/internal/projects/${projectId}/rotateSecret`, {method: 'POST'}).then(async (res) => {
            if (res.ok) {
                const json = await res.json();
                secret = json.newSecret;
            } else {
                alert(`Failed to reset Client Secret: ${res.status} ${res.statusText}`);
            }
        });
    }

</script>

<Navbar />
<DashboardSidebar />

<Modal id="reset-token-modal" title="Reset Client Secret" bind:visible={resetModalShown}>
    <p>
        Are you sure you want to create a new Client Secret? <br>
        This action cannot be undone, and the old Client Secret will no longer work.
    </p>
    <div class="mt-2 md:mt-4">
        <Button variant="danger" onClick={resetToken}>
            Reset
        </Button>
        <Button variant="primary" onClick={() => resetModalShown = false}>
            Cancel
        </Button>
    </div>
</Modal>

<DashboardComponent>
    <div class="mt-6 w-full h-full">
        <div class="mr-4 md:mr-6">
            <p class="text-sm text-zinc-700/70 dark:text-zinc-300/70">
                <Link href="/dashboard/projects">Back to projects</Link>
            </p>

            <h1 class="mt-2 text-4xl font-bold text-zinc-700 dark:text-zinc-50">{data.name}</h1>

            {#if isLoading}
                <p class="mt-4 text-zinc-600 dark:text-zinc-400">Loading project data...</p>
            {:else if error}
                <p class="mt-4 text-red-600 dark:text-red-400">{error}</p>
            {:else}
                <div class="mt-6 grid md:grid-cols-2 md:grid-rows-3">
                    <div class="flex flex-col justify-start gap-y-2 md:gap-y-6">
                        <div class="">
                            <h2 class="text-2xl font-semibold text-zinc-700 dark:text-zinc-50">Description</h2>
                            <p class="mt-2 text-zinc-700 dark:text-zinc-300">{data.description || "No description provided."}</p>
                        </div>
                        <div class="w-full flex flex-row gap-x-4">
                            <div class="">
                                <BarInput disabled id="clientId" label="Client ID" class="w-1/2" placeholder={projectId} copyButton copyValue={projectId} />
                            </div>
                            <div class="">
                                <BarInput disabled id="clientToken" label="Client Secret" class="w-1/2" value={secret ? secret : ''} placeholder={secret ? "" : "Hidden for security"} copyButton copyButtonDisabled={!secret}></BarInput>
                                <Button variant="ghost" size="sm" class="mt-2" onClick={() => resetModalShown = true}>
                                    Reset Secret
                                </Button>
                            </div>
                        </div>
                    </div>
                    <div class="col-span-1 flex justify-center">
                        <img src={data.imageBlob || PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", data.name)} alt="Project Image" class="w-1/3 h-auto rounded-lg object-cover shadow-lg" />
                    </div>
                </div>
            {/if}

        </div>
    </div>
</DashboardComponent>


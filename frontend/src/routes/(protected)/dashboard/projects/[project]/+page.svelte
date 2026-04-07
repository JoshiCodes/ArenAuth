<script lang="ts">
    import { page } from "$app/state";
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import Link from "$lib/components/ui/Link.svelte";
    import { onMount } from "svelte";
    import { apiCall } from "$lib/api";
    import Button from "$lib/components/ui/Button.svelte";
    import BarInput from "$lib/components/ui/forms/BarInput.svelte";
    import Modal from "$lib/components/Modal.svelte";
    import CheckInput from "$lib/components/ui/forms/CheckInput.svelte";
    import BarSelect from "$lib/components/ui/forms/BarSelect.svelte";
    import { toastStore } from "$lib/components/toasts/toastStore";
    import { BACKEND_URL } from "$lib/vars";
    import { env } from "$env/dynamic/public";
    import BackgroundBlobs from "$lib/components/BackgroundBlobs.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";

    const PUBLIC_FALLBACK_IMG_URL = env.PUBLIC_FALLBACK_IMG_URL;

    let fileInput: HTMLInputElement | undefined = $state();

    const projectId = $derived(String(page.params.project));

    let data = $state({
        id: '',
        name: "Unknown",
        description: "",
        avatarId: null as string | null,
        redirect_uris: [] as string[]
    });

    let iconUrl = $state("");

    // Update iconUrl whenever data.avatarId or data.name changes
    $effect(() => {
        iconUrl = data.avatarId
            ? BACKEND_URL + "/api/v1/avatar/project/" + data.avatarId + "?size=1024"
            : PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", encodeURIComponent(data.name));
    });

    let availableScopes: {name: string, description: string}[] = $state([]);
    let isLoading = $state(true);
    let secret = $state('');
    let resetModalShown = $state(false);
    let newRedirectUri = $state('');
    let builderSelectedUri = $state('');
    let builtOauthUrl = $state('');

    onMount(() => {
        apiCall(`/api/v1/internal/projects/${projectId}`)
            .then(async (res) => {
                if (res.ok) {
                    const json = await res.json();
                    if(!json.id || !json.name) {
                        toastStore.add("Invalid response from Server.", {type: "error"})
                        return;
                    }
                    data = {
                        id: json.id,
                        name: json.name,
                        description: json.description || "",
                        avatarId: json.avatarId || null,
                        redirect_uris: json.redirectUris || []
                    };
                } else {
                    toastStore.add(`Failed to fetch project data: ${res.status} ${res.statusText}`, {type: "error"});
                }
                isLoading = false;
            });

        apiCall("/oauth2/scopes").then(async (res) => {
            if(res.ok) {
                const json = await res.json();
                availableScopes = json || [];
            }
        });
    });

    function resetToken() {
        resetModalShown = false;
        apiCall(`/api/v1/internal/projects/${projectId}/rotateSecret`, {method: 'POST'}).then(async (res) => {
            if (res.ok) {
                const json = await res.json();
                secret = json.newSecret;
                toastStore.add("Client Secret rotated successfully!", {type: "success"});
            } else {
                const json = await res.json();
                const errorMsg = json?.error || `${res.status} ${res.statusText}`;
                toastStore.add("Failed to reset Client Secret: " + errorMsg, {type: "error"});
            }
        });
    }

    function addRedirectUri() {
        if (!newRedirectUri) return;
        apiCall(`/api/v1/internal/projects/${projectId}/redirectUri`, {
            method: 'POST',
            body: JSON.stringify({ redirectUri: newRedirectUri })
        }).then(async (res) => {
            if (res.ok) {
                const json = await res.json();
                if(json && json.success) {
                    data.redirect_uris = [...data.redirect_uris, newRedirectUri];
                    newRedirectUri = '';
                    toastStore.add("Redirect URI added.", {type: "success"});
                } else {
                    toastStore.add(`Failed to add Redirect URI: Invalid response from server.`, {type: 'error'});
                }
            } else {
                toastStore.add(`Failed to add Redirect URI: Server error.`, {type: 'error'});
            }
        });
    }

    function deleteRedirectUri(uri: string) {
        apiCall(`/api/v1/internal/projects/${projectId}/redirectUri`, {
            method: 'DELETE',
            body: JSON.stringify({ redirectUri: uri })
        }).then(async (res) => {
            if (res.ok) {
                const json = await res.json();
                if(json && json.success) {
                    data.redirect_uris = data.redirect_uris.filter(r => r !== uri);
                    toastStore.add("Redirect URI deleted.", {type: "success"});
                } else {
                    toastStore.add(`Failed to delete Redirect URI: Invalid response from server.`, {type: 'error'});
                }
            } else {
                const json = await res.json();
                const errorMsg = json?.error || "Server error";
                toastStore.add("Failed to delete Redirect URI: " + errorMsg, {type: "error"});
            }
        });
    }

    function updateOauthBuilder() {
        if(!builderSelectedUri) {
            builtOauthUrl = '';
            return;
        }
        const selectedScopes = availableScopes.filter(scope => {
            const checkbox = document.getElementById("scope-" + scope.name) as HTMLInputElement;
            return checkbox && checkbox.checked;
        }).map(s => s.name);

        const baseUrl = `${BACKEND_URL}/oauth2/authorize?client_id=${projectId}&redirect_uri=${encodeURIComponent(builderSelectedUri)}&response_type=code`;
        const scopeParam = selectedScopes.length > 0 ? `&scope=${encodeURIComponent(selectedScopes.join(' '))}` : '';
        builtOauthUrl = baseUrl + scopeParam;
    }

    function handleFileSelect(event: Event) {
        const target = event.target as HTMLInputElement;
        const files = target.files;
        if(!files || files.length === 0) return;
        const file = files[0];

        if(!file.type.startsWith("image/")) {
            toastStore.add("Please select a valid image file.", {type: "error"});
            return;
        }

        if(file.size > 5 * 1024 * 1024) {
            toastStore.add("Image size must be less than 5MB.", {type: "error"});
            return;
        }

        const fd = new FormData();
        fd.append("file", file);
        apiCall("/api/v1/avatar/upload/project/" + projectId, {
            method: 'POST',
            body: fd
        }, null).then(async (res) => {
            if(res.ok) {
                const json = await res.json();
                if(json && json.avatarId) {
                    data.avatarId = json.avatarId;
                    toastStore.add('Project image updated!', {type: 'success'});
                }
            } else {
                toastStore.add("Failed to upload image.", {type: "error"});
            }
        });

        const reader = new FileReader();
        reader.onload = (e) => {
            iconUrl = e.target?.result as string;
        };
        reader.readAsDataURL(file);
    }
</script>

<BackgroundBlobs />
<BackgroundGrid />
<Navbar />
<DashboardSidebar />

<Modal id="reset-token-modal" title="Reset Client Secret" bind:visible={resetModalShown}>
    <p class="text-zinc-600 dark:text-zinc-400">
        Are you sure you want to create a new Client Secret? <br>
        This action cannot be undone, and the old Client Secret will no longer work.
    </p>
    <div class="mt-6 flex justify-end gap-3">
        <Button variant="ghost" onClick={() => resetModalShown = false}>
            Cancel
        </Button>
        <Button variant="danger" onClick={resetToken}>
            Reset Secret
        </Button>
    </div>
</Modal>

<DashboardComponent>
    <div class="max-w-[90%]">
        <header class="mb-8">
            <Link href="/dashboard/projects" class="text-sm flex items-center gap-1 mb-4 opacity-70 hover:opacity-100 transition-opacity">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-4">
                    <path fill-rule="evenodd" d="M17 10a.75.75 0 0 1-.75.75H5.612l4.158 3.96a.75.75 0 1 1-1.04 1.08l-5.5-5.25a.75.75 0 0 1 0-1.08l5.5-5.25a.75.75 0 1 1 1.04 1.08L5.612 9.25H16.25A.75.75 0 0 1 17 10Z" clip-rule="evenodd" />
                </svg>
                Back to projects
            </Link>
            <h1 class="text-4xl font-extrabold tracking-tight text-zinc-900 dark:text-white">{data.name}</h1>
        </header>

        {#if isLoading}
            <div class="flex items-center gap-3 text-zinc-500 animate-pulse">
                <div class="w-5 h-5 border-2 border-zinc-500 border-t-transparent rounded-full animate-spin"></div>
                Loading project data...
            </div>
        {:else}
            <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                <!-- Main Settings -->
                <div class="lg:col-span-2 space-y-8">
                    <!-- Project Info -->
                    <div class="p-6 md:p-8 rounded-2xl bg-white/70 dark:bg-zinc-900/70 backdrop-blur-xl border border-zinc-200/50 dark:border-zinc-800/50 shadow-xl">
                        <h2 class="text-xl font-bold text-zinc-900 dark:text-white mb-6">General Settings</h2>
                        <div class="space-y-6">
                            <div>
                                <label class="block text-sm font-medium text-zinc-500 dark:text-zinc-400 mb-1">Description</label>
                                <p class="text-zinc-900 dark:text-zinc-100">{data.description || "No description provided."}</p>
                            </div>

                            <div class="grid grid-cols-1 md:grid-cols-2 gap-6 pt-4">
                                <BarInput disabled id="clientId" label="Client ID" placeholder={projectId} copyButton copyValue={projectId} />
                                <div class="space-y-2">
                                    <BarInput disabled id="clientToken" label="Client Secret" value={secret ? secret : ''} placeholder={secret ? "" : "Hidden for security"} copyButton copyButtonDisabled={!secret} />
                                    <Button variant="ghost" size="sm" class="w-full text-violet-600 dark:text-violet-400 border-none hover:bg-violet-500/10" onClick={() => resetModalShown = true}>
                                        Rotate Secret
                                    </Button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Redirect URIs -->
                    <div class="p-6 md:p-8 rounded-2xl bg-white/70 dark:bg-zinc-900/70 backdrop-blur-xl border border-zinc-200/50 dark:border-zinc-800/50 shadow-xl">
                        <h2 class="text-xl font-bold text-zinc-900 dark:text-white mb-6">Redirect URIs</h2>
                        <div class="space-y-4">
                            {#if data.redirect_uris.length > 0}
                                {#each data.redirect_uris as uri, index}
                                    <div class="flex items-end gap-3 group">
                                        <div class="flex-1">
                                            <BarInput disabled copyButton id={"redirect_uri_" + index} label={null} value={uri} />
                                        </div>
                                        <button onclick={() => deleteRedirectUri(uri)} class="mb-2 text-zinc-400 hover:text-red-500 transition-colors" title="Delete URI">
                                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                                                <path stroke-linecap="round" stroke-linejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" />
                                            </svg>
                                        </button>
                                    </div>
                                {/each}
                            {:else}
                                <p class="text-zinc-500 italic py-2">No redirect URIs set.</p>
                            {/if}

                            <div class="pt-6 border-t border-zinc-200 dark:border-zinc-800">
                                <div class="flex flex-col sm:flex-row items-end gap-3">
                                    <div class="flex-1 w-full">
                                        <BarInput id="new_redirect_uri" label="Add Redirect URI" bind:value={newRedirectUri} placeholder="https://myapp.com/callback" />
                                    </div>
                                    <div>
                                        <Button variant="primary" class="" onClick={addRedirectUri}>
                                            Add URI
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- URL Builder -->
                    <div class="p-6 md:p-8 rounded-2xl bg-white/70 dark:bg-zinc-900/70 backdrop-blur-xl border border-zinc-200/50 dark:border-zinc-800/50 shadow-xl">
                        <h2 class="text-xl font-bold text-zinc-900 dark:text-white mb-6">OAuth URL Builder</h2>
                        <div class="space-y-6">
                            <div>
                                <h3 class="text-sm font-semibold text-zinc-500 dark:text-zinc-400 uppercase tracking-wider mb-4">Select Scopes</h3>
                                <div class="grid grid-cols-2 md:grid-cols-3 gap-3">
                                    {#if availableScopes.length > 0}
                                        {#each availableScopes as scope}
                                            <div class="bg-white/50 dark:bg-zinc-900/50 px-3 py-1 rounded-lg border border-zinc-200/50 dark:border-zinc-800/50">
                                                <CheckInput id={"scope-" + scope.name} label={scope.name} onChange={updateOauthBuilder} />
                                            </div>
                                        {/each}
                                    {:else}
                                        <p class="text-xs text-zinc-500">Loading scopes...</p>
                                    {/if}
                                </div>
                            </div>

                            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <BarSelect id="redirect_uri_select" label="Select Redirect URI" bind:value={builderSelectedUri} onChange={updateOauthBuilder}>
                                    <option value="" disabled selected>Select an URI</option>
                                    {#each data.redirect_uris as uri}
                                        <option value={uri}>{uri}</option>
                                    {/each}
                                </BarSelect>
                                <BarInput id="generated_oauth_url" label="Generated URL" bind:value={builtOauthUrl} disabled copyButton />
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Avatar Column -->
                <div class="lg:col-span-1">
                    <div class="sticky top-28 p-6 rounded-2xl bg-white/70 dark:bg-zinc-900/70 backdrop-blur-xl border border-zinc-200/50 dark:border-zinc-800/50 shadow-xl flex flex-col items-center text-center">
                        <h3 class="text-lg font-semibold text-zinc-900 dark:text-white mb-6 w-full text-left">Project Image</h3>

                        <div role="button" tabindex="0" onkeyup={() => {}} class="relative group cursor-pointer" onclick={() => fileInput?.click()}>
                            <div class="absolute inset-0 rounded-2xl bg-violet-500/10 blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                            <img
                                    src={iconUrl}
                                    alt={data.name}
                                    class="h-64 relative w-full aspect-square rounded-2xl object-cover border-4 border-white dark:border-zinc-800 shadow-2xl transition-transform duration-300 group-hover:scale-[1.02]"
                            />
                            <div class="absolute inset-0 rounded-2xl bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center">
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="size-8 text-white">
                                    <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L6.832 19.82a4.5 4.5 0 0 1-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 0 1 1.13-1.897L16.863 4.487Zm0 0L19.5 7.125" />
                                </svg>
                            </div>
                        </div>

                        <div class="mt-8 space-y-3 w-full">
                            <Button variant="ghost" size="sm" fullWidth onClick={() => fileInput?.click()}>
                                Upload New Image
                            </Button>
                            <p class="text-xs text-zinc-500 leading-relaxed">
                                Recommended: 1024x1024px <br>
                                Max size: 5MB
                            </p>
                        </div>

                        <input
                                type="file"
                                bind:this={fileInput}
                                accept="image/*"
                                onchange={handleFileSelect}
                                class="hidden"
                        />
                    </div>
                </div>
            </div>
        {/if}
    </div>
</DashboardComponent>
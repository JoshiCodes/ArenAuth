<script lang="ts">
    import { page } from "$app/state";
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import Link from "$lib/components/ui/Link.svelte";
    import {onMount} from "svelte";
    import {apiCall} from "$lib/api";
    import Button from "$lib/components/ui/Button.svelte";
    import BarInput from "$lib/components/ui/forms/BarInput.svelte";
    import Modal from "$lib/components/Modal.svelte";
    import CheckInput from "$lib/components/ui/forms/CheckInput.svelte";
    import BarSelect from "$lib/components/ui/forms/BarSelect.svelte";
    import {toastStore, type ToastType} from "$lib/components/toasts/toastStore";
    import {BACKEND_URL} from "$lib/vars";
    import { env } from "$env/dynamic/public";
    import BackgroundBlob from "$lib/components/BackgroundBlob.svelte";

    const PUBLIC_FALLBACK_IMG_URL = env.PUBLIC_FALLBACK_IMG_URL;

    let fileInput: HTMLInputElement;

    $: projectId = String(page.params.project);
    $: data = {
        id: '',
        name: "Unknown",
        description: "",
        avatarId: "",
        redirect_uris: ['']
    };

    $: iconUrl = data.avatarId ? BACKEND_URL + "/api/v1/avatar/project/" + data.avatarId + "?size=1024" : PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", encodeURIComponent(data.name));

    let availableScopes: [{name: string, description: string}]|[] = [];

    $: isLoading = true;

    $: secret = '';

    $: resetModalShown = false;

    let newRedirectUri = '';

    let builderSelectedUri = '';
    $: builtOauthUrl = '';

    onMount(() => {
        apiCall(`/api/v1/internal/projects/${projectId}`)
            .then(async (res) => {
                if (res.ok) {
                    const json = await res.json();

                    if(!json.id || !json.name) {
                        console.log(JSON.stringify(json));
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
    })

    function resetToken() {
        resetModalShown = false;
        apiCall(`/api/v1/internal/projects/${projectId}/rotateSecret`, {method: 'POST'}).then(async (res) => {
            if (res.ok) {
                const json = await res.json();
                secret = json.newSecret;
            } else {
                const json = await res.json();
                if(json && json.error) {
                    toastStore.add("Failed to reset Client Secret: " + json.error, {type: "error"});
                } else {
                    toastStore.add("Failed to reset Client Secret: " + res.status + " " + res.statusText, {type: "error"});
                }
            }
        });
    }

    function addRedirectUri() {
        apiCall(`/api/v1/internal/projects/${projectId}/redirectUri`,
            {
                method: 'POST',
                body: JSON.stringify({
                    redirectUri: newRedirectUri
                })
            }).then(async (res) => {
            if (res.ok) {
                const json = await res.json();
                if(json && json.success) {
                    data.redirect_uris = [...data.redirect_uris, newRedirectUri]
                    newRedirectUri = '';
                } else {
                    toastStore.add(`Failed to add Redirect URI: Invalid response from server.`, {type: 'error'});
                }
            } else {
                const json = await res.json();
                if(json && json.error) {
                    toastStore.add(`Failed to add Redirect URI: Invalid response from server.`, {type: 'error'});
                }
            }
        });
    }

    function deleteRedirectUri(uri: string) {
        apiCall(`/api/v1/internal/projects/${projectId}/redirectUri`,
            {
                method: 'DELETE',
                body: JSON.stringify({
                    redirectUri: uri
                })
            }).then(async (res) => {
            if (res.ok) {
                const json = await res.json();
                if(json && json.success) {
                    data.redirect_uris = data.redirect_uris.filter(r => r !== uri);
                } else {
                    alert(`Failed to delete Redirect URI: Invalid response from server.`);
                }
            } else {
                const json = await res.json();
                if(json && json.error) {
                    toastStore.add("Failed to delete Redirect URI: " + json.error, {type: "error"});
                }
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
            toastStore.add("Please select a valid image file. (PNP,JPG, WebP)", {type: "error"});
            return;
        }

        if(file.size > 5 * 1024 * 1024) { // 5MB limit
            toastStore.add("Image size must be less than 5MB.", {type: "error"});
            return;
        }

        const fd = new FormData();
        fd.append("file", file);
        apiCall("/api/v1/avatar/upload/project/" + projectId, {
            method: 'POST',
            body: fd
        }, null
        )
            .then(async (res) => {
                console.log(res)
                if(res.ok) {
                    const json = await res.json();
                    if(json && json.avatarId) {
                        data.avatarId = json.avatarId;
                        toastStore.add('Project image updated successfully!', {type: 'success'});
                    } else {
                        toastStore.add("Failed to upload image: Invalid response from server.", {type: "error"});
                    }
                } else {
                    const json = await res.json();
                    if(json && json.error) {
                        toastStore.add("Failed to upload image: " + json.error, {type: "error"});
                    } else {
                        toastStore.add("Failed to upload image: " + res.status + " " + res.statusText, {type: "error"});
                    }
                }
            });

        const reader = new FileReader();
        reader.onload = (e) => {
            iconUrl = e.target?.result as string;
        };
        reader.readAsDataURL(file);
    }

</script>

<Navbar />
<DashboardSidebar />
<BackgroundBlob class="top-32 right-6 w-125 h-32 rounded-lg" />

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
            {:else}
                <div class="mt-6 grid md:grid-cols-3 md:grid-rows-3 gap-x-4 md:gap-x-12">

                    <div class="col-span-2">
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
                        <div class="mt-2 md:mt-6">
                            <span class="text-2xl font-semibold text-zinc-700 dark:text-zinc-50">Redirect URIs</span>
                            <div>
                                {#if data.redirect_uris.length > 0}
                                    {#each data.redirect_uris as uri, index}
                                        <div class="flex content-center gap-2 w-4/5">
                                            <div class="flex-1 w-[90%]">
                                                <BarInput disabled copyButton id="redirect_uri_" + index label={null} value={uri} />
                                            </div>
                                            <div class="flex items-end pb-2">
                                                <button title="Delete" onclick={() => {deleteRedirectUri(uri)}} class="flex-shrink-0 transition-colors duration-200 hover:text-red-500 dark:hover:text-red-700">
                                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                                                        <path stroke-linecap="round" stroke-linejoin="round" d="m14.74 9-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 0 1-2.244 2.077H8.084a2.25 2.25 0 0 1-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 0 0-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 0 1 3.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 0 0-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 0 0-7.5 0" />
                                                    </svg>
                                                </button>
                                            </div>
                                        </div>
                                    {/each}
                                {:else }
                                    <span class="my-2 md:my-4 text-zinc-600 dark:text-zinc-200/70">No redirect URIs set.</span>
                                {/if}
                                <div class="mt-2 w-1/2">
                                    <BarInput id="new_redirect_uri" label="Add Redirect URI" bind:value={newRedirectUri} />
                                    <Button variant="ghost" size="sm" class="mt-3" onClick={addRedirectUri}>
                                        Add URI
                                    </Button>
                                </div>
                            </div>
                        </div>

                        <div class="mt-2 md:mt-6">
                            <span class="text-2xl font-semibold text-zinc-700 dark:text-zinc-50">URL Builder</span>
                            <div class="bg-zinc-300/30 dark:bg-zinc-700 rounded-lg mt-2 px-2 py-4">
                                <span class="text-xl font-semibold text-zinc-700 dark:text-zinc-50">Scopes</span>
                                <div class="dark:bg-zinc-800 px-2 py-2 grid grid-cols-2 md:grid-cols-3 gap-4 rounded-lg my-2">
                                    {#if availableScopes.length > 0}
                                        {#each availableScopes as scope}
                                            <CheckInput id={"scope-" + scope.name} label={scope.name} onChange="{updateOauthBuilder}" />
                                        {/each}
                                    {:else}
                                        <span>Failed to fetch available scopes.</span>
                                    {/if}
                                </div>
                                <span class="mt-2 md:mt-4 text-xl font-semibold text-zinc-700 dark:text-zinc-50">Select Redirect URI</span>
                                <BarSelect id="redirect_uri_select" label={null} class="mt-2 w-full" bind:value={builderSelectedUri} onChange={updateOauthBuilder}>
                                    {#each data.redirect_uris as uri}
                                        <option value={uri}>{uri}</option>
                                    {/each}
                                </BarSelect>
                                <span class="mt-2 md:mt-4 text-xl font-semibold text-zinc-700 dark:text-zinc-50">Generated OAuth URL</span>
                                <BarInput id="generated_oauth_url" bind:value={builtOauthUrl} label={null} disabled copyButton />
                            </div>
                        </div>

                    </div>

                        <div class="col-span-1 row-span-1 flex justify-center">
                            <div class="w-full">
                                <div class="relative inline-block w-2/3 group">
                                    <img src={iconUrl} alt={"Project Image"} class="w-full rounded-lg object-cover shadow-lg group-hover:opacity-50 transition-opacity duration-200" />
                                    <button onclick={() => {fileInput?.click()}} class="absolute inset-0 rounded-lg flex items-center justify-center bg-black/30 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                                        <span
                                                class="p-3 bg-white dark:bg-zinc-600 hover:bg-gray-100 dark:hover:bg-zinc-500 text-gray-700 dark:text-gray-200 rounded-full shadow-lg  transition-colors duration-200"
                                        >
                                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
  <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L6.832 19.82a4.5 4.5 0 0 1-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 0 1 1.13-1.897L16.863 4.487Zm0 0L19.5 7.125" />
</svg>

                                        </span>
                                        <span class="sr-only">Edit Avatar</span>
                                    </button>
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
    </div>
</DashboardComponent>


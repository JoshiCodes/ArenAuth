<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import Link from "$lib/components/ui/Link.svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import {goto} from "$app/navigation";
    import {onMount} from "svelte";
    import {apiCall, fetchAvailableScopes} from "$lib/api";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";
    import {projectAvatarUrl} from "$lib/avatar";

    let { data } = $props();

    let req = $state<string|null>(null);
    let projectName = $state(null);
    let avatarId = $state<string|null>(null);
    let scopes = $state<string[]>([]);
    let redirectUri = $state("");
    let projectCreated = $state("");
    let availableScopes = $state<{name: string, description: string}[]>([]);
    let isLoading = $state(true);

    let projectImg: string|null = $state(null);

    onMount(async () => {
        const urlParams = new URLSearchParams(window.location.search);
        req = urlParams.get("req");

        if (!req) {
            goto("/dashboard?error=invalid_oauth_request");
            return;
        }

        try {
            const res = await apiCall("/oauth2/consent?req=" + req, {
                method: "GET",
                credentials: "include"
            });
            const consentData = await res.json();

            if(!consentData.project || !consentData.project.name || !consentData.project.createdAt || !consentData.scope || !consentData.redirectUri) {
                goto("/dashboard?error=invalid_oauth_request");
                return;
            }

            projectName = consentData.project.name;
            scopes = (consentData.scope as string).split(" ");
            redirectUri = consentData.redirectUri;
            
            projectCreated = new Date(consentData.project.createdAt).toLocaleDateString(undefined, {
                year: "numeric",
                month: "long",
                day: "numeric"
            });
            avatarId = consentData.project.avatarId;
            isLoading = false;
            projectImg = projectAvatarUrl(avatarId, projectName);
        } catch (error) {
            console.error(error);
            goto("/dashboard?error=invalid_oauth_request");
        }
        
        availableScopes = await fetchAvailableScopes();
    });

    function continueAuth(authorised: boolean) {
        apiCall("/oauth2/authorize", {
            method: "POST",
            body: JSON.stringify({
                req: req,
                approved: authorised,
            })
        }).then(res => res.json()).then(authData => {
            if(!authData.redirectUri) {
                let param = '';
                if(authData.error) {
                    param = "&error=" + authData.error;
                }
                goto("/dashboard?error=oauth_authorization_failed" + param);
                return;
            }
            window.location.href = authData.redirectUri;
        }).catch(err => {
            console.error(err);
            goto("/dashboard?error=oauth_authorization_failed");
        });
    }

</script>

<Navbar />
<BackgroundGrid />

<div class="min-h-[calc(100vh-64px)] flex items-center justify-center mt-6 md:mt-12 p-4">
    <div class="w-full max-w-xl bg-white/70 dark:bg-zinc-900/70 backdrop-blur-xl border border-zinc-200/50 dark:border-zinc-800/50 rounded-3xl shadow-2xl overflow-hidden animate-in fade-in zoom-in duration-300">
        
        <!-- Header / Identity -->
        <div class="p-8 pb-4 text-center">
            <div class="relative inline-block group mb-6">
                <div class="absolute inset-0 rounded-2xl bg-violet-500/20 blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
                <img 
                    src={projectImg} 
                    alt={projectName} 
                    class="relative w-24 h-24 md:w-32 md:h-32 rounded-2xl object-cover shadow-xl border-4 border-white dark:border-zinc-800 transition-transform duration-500 group-hover:scale-105" 
                />
            </div>
            
            <h1 class="text-2xl md:text-3xl font-extrabold text-zinc-900 dark:text-white tracking-tight mb-2">
                Authorize <span class="text-violet-600 dark:text-violet-400">{projectName}</span>
            </h1>
            <p class="text-zinc-600 dark:text-zinc-400">
                wants to access your <span class="font-bold text-zinc-900 dark:text-white">Aren</span> account
            </p>
            
            <div class="mt-4 flex items-center justify-center gap-2 text-sm">
                <span class="text-zinc-500">Signed in as</span>
                <span class="font-semibold text-zinc-900 dark:text-white">{data.me.username}</span>
                <span class="text-zinc-300 dark:text-zinc-700">•</span>
                <Link href="/logout" class="text-violet-600 dark:text-violet-400 hover:underline">Not you?</Link>
            </div>
        </div>

        {#if isLoading}
            <div class="p-12 flex flex-col items-center justify-center gap-4 text-zinc-500">
                <div class="w-10 h-10 border-4 border-violet-500/30 border-t-violet-600 rounded-full animate-spin"></div>
                <p class="animate-pulse">Loading request details...</p>
            </div>
        {:else}
            <!-- Content Area -->
            <div class="px-8 py-4 space-y-6">
                <!-- Scopes Section -->
                <div class="bg-zinc-50/50 dark:bg-zinc-950/30 rounded-2xl p-6 border border-zinc-200/50 dark:border-zinc-800/50">
                    <h2 class="text-xs font-bold uppercase tracking-widest text-zinc-500 mb-4">The developer will be able to:</h2>
                    <div class="space-y-4">
                        {#each scopes as scope}
                            <div class="flex items-start gap-3 group">
                                <div class="mt-0.5 p-1 rounded-full bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 shrink-0">
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-4">
                                        <path fill-rule="evenodd" d="M16.704 4.153a.75.75 0 0 1 .143 1.052l-8 10.5a.75.75 0 0 1-1.127.075l-4.5-4.5a.75.75 0 0 1 1.06-1.06l3.894 3.893 7.48-9.817a.75.75 0 0 1 1.05-.143Z" clip-rule="evenodd" />
                                    </svg>
                                </div>
                                <div class="flex flex-col">
                                    <span class="text-sm font-semibold text-zinc-900 dark:text-zinc-100">{scope}</span>
                                    <span class="text-xs text-zinc-500 dark:text-zinc-400 italic">
                                        {availableScopes.find(s => s.name === scope)?.description || 'No description available for this scope.'}
                                    </span>
                                </div>
                            </div>
                        {/each}
                    </div>
                </div>

                <!-- Footer Metadata -->
                <div class="px-2 space-y-3">
                    <div class="flex items-start gap-2 text-sm text-zinc-500 dark:text-zinc-400">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-4 mt-0.5 shrink-0 opacity-60">
                            <path fill-rule="evenodd" d="M12.232 4.232a2.5 2.5 0 0 1 3.536 3.536l-1.225 1.224a.75.75 0 0 0 1.061 1.06l1.224-1.224a4 4 0 0 0-5.656-5.656l-3 3a4 4 0 0 0 .225 5.865.75.75 0 0 0 .977-1.138 2.5 2.5 0 0 1-.142-3.667l3-3Z" clip-rule="evenodd" />
                            <path fill-rule="evenodd" d="M7.768 15.768a2.5 2.5 0 0 1-3.536-3.536l1.225-1.224a.75.75 0 0 0-1.061-1.06l-1.224 1.224a4 4 0 0 0 5.656 5.656l3-3a4 4 0 0 0-.225-5.865.75.75 0 0 0-.977 1.138 2.5 2.5 0 0 1 .142 3.667l-3 3Z" clip-rule="evenodd" />
                        </svg>
                        <span class="leading-tight">
                            After authorization, you will be redirected to: <br>
                            <span class="font-mono text-xs bg-zinc-100 dark:bg-zinc-800 px-1 rounded select-all break-all">{redirectUri}</span>
                        </span>
                    </div>
                    
                    <div class="flex items-center gap-2 text-sm text-zinc-500 dark:text-zinc-400">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" class="size-4 opacity-60">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 1 0 0-16 8 8 0 0 0 0 16Zm.75-13a.75.75 0 0 0-1.5 0v5c0 .414.336.75.75.75h4a.75.75 0 0 0 0-1.5h-3.25V5Z" clip-rule="evenodd" />
                        </svg>
                        <span>Project active since {projectCreated}</span>
                    </div>
                </div>
            </div>

            <!-- Actions -->
            <div class="p-8 pt-4 grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Button variant="ghost" class="w-full h-12 rounded-2xl" onClick={() => continueAuth(false)}>
                    Cancel
                </Button>
                <Button variant="primary" class="w-full h-12 rounded-2xl shadow-lg shadow-violet-500/20" onClick={() => continueAuth(true)}>
                    Authorize
                </Button>
            </div>
        {/if}
        
        <!-- Safety Footer -->
        <div class="px-8 py-4 bg-zinc-100/50 dark:bg-zinc-800/30 border-t border-zinc-200/50 dark:border-zinc-800/50 text-sm text-center text-zinc-500 italic">
            Make sure you trust the developer of <b>{projectName}</b> before authorizing access to your data.
        </div>
    </div>
</div>
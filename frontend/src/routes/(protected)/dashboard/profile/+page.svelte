<script lang="ts">
    import {page} from "$app/state";
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import FloatingInput from "$lib/components/ui/forms/FloatingInput.svelte";
    import {onMount} from "svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import BottomNotification from "$lib/components/ui/BottomNotification.svelte";
    import {BACKEND_URL} from "$lib/vars";
    import {env} from "$env/dynamic/public";
    import BackgroundBlobs from "$lib/components/BackgroundBlobs.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";

    const PUBLIC_FALLBACK_IMG_URL = env.PUBLIC_FALLBACK_IMG_URL;

    type Me = {
        username: string;
        userId: string;
        roles: string[];
        avatarId: string | null;
    };

    const me = $derived((page.data.me as Me | null | undefined) ?? null);
    const defaultIconUrl = $derived(PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", encodeURIComponent(me ? me.username : "Unknown User")));
    
    let realIconUrl = $state("");
    let iconUrl = $state("");
    let username = $state("");
    let isChanged = $state(false);
    let sidebarOpen = $state(false);

    onMount(() => {
        realIconUrl = (me && me.avatarId) ? BACKEND_URL + "/api/v1/avatar/project/" + me.avatarId + "?size=512" : defaultIconUrl;
        iconUrl = realIconUrl;
        username = me?.username ?? "";
    });

    let fileInput: HTMLInputElement;

    function handleFileSelect(event: Event) {
        const target = event.target as HTMLInputElement;
        if (target.files && target.files.length > 0) {
            const file = target.files[0];
            const reader = new FileReader();
            reader.onload = (e) => {
                iconUrl = e.target?.result as string;
                handleChange();
            };
            reader.readAsDataURL(file);
        }
    }

    function handleChange() {
        if (iconUrl !== realIconUrl) {
            isChanged = true;
            return;
        }
        if (me && username !== me.username) {
            isChanged = true;
            return;
        }
        isChanged = false;
    }

    function resetForm() {
        username = me?.username ?? "";
        iconUrl = realIconUrl;
        isChanged = false;
    }

    function clearAvatar() {
        iconUrl = defaultIconUrl;
        handleChange();
    }
</script>

<BackgroundBlobs />
<BackgroundGrid />

<Navbar bind:sidebarOpen />
<DashboardSidebar bind:open={sidebarOpen} />

<DashboardComponent>
    <div class="max-w-[90%]">
        <header class="mb-8">
            <h1 class="text-3xl font-extrabold tracking-tight text-zinc-900 dark:text-white">User Settings</h1>
            <p class="text-zinc-500 dark:text-zinc-400 mt-2">Manage your profile and account preferences.</p>
        </header>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- Form Section -->
            <div class="lg:col-span-2 order-2 lg:order-1">
                <div class="p-6 md:p-8 rounded-2xl bg-white/70 dark:bg-zinc-900/70 backdrop-blur-xl border border-zinc-200/50 dark:border-zinc-800/50 shadow-xl">
                    <div class="space-y-6">
                        <div>
                            <h3 class="text-lg font-semibold text-zinc-900 dark:text-white mb-4">Profile Information</h3>
                            <div class="space-y-4">
                                <FloatingInput id="username" label="Username" bind:value={username} onkeyup={handleChange} />
                                <div class="opacity-60">
                                    <FloatingInput id="email" label="E-Mail" value={me?.username + "@example.com"} disabled />
                                    <p class="text-xs text-zinc-500 mt-1 italic">Email cannot be changed currently.</p>
                                </div>
                            </div>
                        </div>

                        <div class="pt-4 border-t border-zinc-200 dark:border-zinc-800">
                            <h3 class="text-lg font-semibold text-zinc-900 dark:text-white mb-4">Security</h3>
                            <Button variant="ghost" size="sm" class="text-violet-600 dark:text-violet-400">
                                Change Password
                            </Button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Avatar Section -->
            <div class="lg:col-span-1 order-1 lg:order-2">
                <div class="p-6 rounded-2xl bg-white/70 dark:bg-zinc-900/70 backdrop-blur-xl border border-zinc-200/50 dark:border-zinc-800/50 shadow-xl flex flex-col items-center text-center">
                    <h3 class="text-lg font-semibold text-zinc-900 dark:text-white mb-6 w-full text-left lg:text-center">Your Avatar</h3>
                    
                    <div role="button" tabindex="0" onkeyup={() => {}} class="relative group cursor-pointer" onclick={() => fileInput?.click()}>
                        <div class="absolute inset-0 rounded-full bg-violet-500/10 blur-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                        <img 
                            src={iconUrl} 
                            alt={me?.username} 
                            class="relative w-32 h-32 md:w-40 md:h-40 rounded-full object-cover border-4 border-white dark:border-zinc-800 shadow-2xl transition-transform duration-300 group-hover:scale-[1.02]" 
                        />
                        <div class="absolute inset-0 rounded-full bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="size-8 text-white">
                                <path stroke-linecap="round" stroke-linejoin="round" d="m16.862 4.487 1.687-1.688a1.875 1.875 0 1 1 2.652 2.652L6.832 19.82a4.5 4.5 0 0 1-1.897 1.13l-2.685.8.8-2.685a4.5 4.5 0 0 1 1.13-1.897L16.863 4.487Zm0 0L19.5 7.125" />
                            </svg>
                        </div>
                        <span class="sr-only">Change Avatar</span>
                    </div>

                    <div class="mt-6 space-y-3 w-full">
                        <Button variant="ghost" size="sm" fullWidth onClick={() => fileInput?.click()}>
                            Upload Image
                        </Button>
                        <Button 
                            variant="ghost" 
                            size="sm" 
                            fullWidth 
                            class="text-red-500 hover:text-red-600 hover:bg-red-50/50 dark:hover:bg-red-900/20"
                            disabled={iconUrl === defaultIconUrl} 
                            onClick={clearAvatar}
                        >
                            Remove Avatar
                        </Button>
                    </div>

                    <input
                        type="file"
                        bind:this={fileInput}
                        accept="image/*"
                        onchange={handleFileSelect}
                        class="hidden"
                    />
                    
                    <p class="mt-4 text-xs text-zinc-500 leading-relaxed">
                        JPG, GIF or PNG. <br>Max size of 2MB.
                    </p>
                </div>
            </div>
        </div>
    </div>
</DashboardComponent>

<BottomNotification visible={isChanged}>
    <div class="flex items-center gap-4 px-2 w-full">
        <div class="flex items-center gap-3">
            <div class="w-2 h-2 rounded-full bg-violet-500 animate-pulse"></div>
            <span class="text-zinc-900 dark:text-white font-medium">You have unsaved changes!</span>
        </div>
        <div class="flex items-center gap-2 ml-auto">
            <Button variant="ghost" size="sm" onClick={resetForm} class="border-none hover:bg-zinc-300/50 dark:hover:bg-zinc-800/50">
                Discard
            </Button>
            <Button variant="primary" size="sm" class="shadow-lg shadow-violet-500/20 px-6">
                Save Changes
            </Button>
        </div>
    </div>
</BottomNotification>

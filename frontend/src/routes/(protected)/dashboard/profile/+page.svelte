<script lang="ts">
    import { page } from "$app/state";
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import FloatingInput from "$lib/components/ui/forms/FloatingInput.svelte";
    import {PencilIcon} from "lucide-svelte";
    import {onMount} from "svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import BottomNotification from "$lib/components/ui/BottomNotification.svelte";
    import {BACKEND_URL} from "$lib/vars";
    import { env } from "$env/dynamic/public";

    const PUBLIC_FALLBACK_IMG_URL = env.PUBLIC_FALLBACK_IMG_URL;

    type Me = {
        username: string;
        userId: string;
        roles: string[];
        avatarId: string | null;
    };

    $: me = (page.data.me as Me | null | undefined) ?? null;

    $: defaultIconUrl = PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", encodeURIComponent(me ? me.username : "Unknown Project"));
    let realIconUrl: string;
    let iconUrl: string;
    let username: string;

    let isChanged = false;

    onMount(() => {
        realIconUrl = (me && me.avatarId) ? BACKEND_URL + "/api/v1/avatar/project/" + me.avatarId + "?size=512" : defaultIconUrl;
        iconUrl = realIconUrl;
        username = me?.username ?? "";
    });

    let fileInput: HTMLInputElement;

    function handleFileSelect(event: Event) {
        const target = event.target as HTMLInputElement;
        if(target.files && target.files.length > 0) {
            const file = target.files[0];

            const reader = new FileReader();
            reader.onload = (e) => {
                iconUrl = e.target?.result as string;
            };
            reader.readAsDataURL(file);
        }

        setTimeout(() => handleChange(), 5)
    }

    function handleChange() {
        console.log("Handling change:", {username, iconUrl});
        console.log(isChanged)
        if(iconUrl != realIconUrl) {
            isChanged = true;
            return;
        }
        if(me && username != me.username) {
            console.log("Username changed:", username);
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

</script>

<Navbar />
<DashboardSidebar />

<DashboardComponent>
    <h1 class="font-bold text-3xl">User Settings</h1>
    <div class="mt-6 md:mt-12">
        <div class="grid grid-cols-3 gap-4">
            <div class="col-span-2 mr-6 md:mr-12">
                <FloatingInput id="username" label="Username" bind:value={username} onKeyChange={handleChange} />
            </div>
            <div class="col-span-1 row-span-1 flex justify-center">
                <div class="w-full flex flex-col justify-center items-center gap-4">
                    <div class="relative inline-block w-2/3 group">
                        <img src={iconUrl} alt={"Project Image"} class="w-full rounded-full object-cover shadow-lg group-hover:opacity-50 transition-opacity duration-200" />
                        <button onclick={() => {fileInput?.click()}} class="absolute inset-0 rounded-full flex items-center justify-center bg-black/30 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                                        <span
                                                class="p-3 bg-white dark:bg-zinc-600 hover:bg-gray-100 dark:hover:bg-zinc-500 text-gray-700 dark:text-gray-200 rounded-full shadow-lg  transition-colors duration-200"
                                        >
                                            <PencilIcon size={24} class="" />
                                        </span>
                        </button>
                    </div>
                    <div class="flex flex-col gap-y-2">
                        <span class="font-semibold text-xl">Your Avatar</span>
                        <Button variant="ghost" size="sm" disabled={iconUrl === defaultIconUrl} onClick={() => {iconUrl = realIconUrl; handleChange()}}>
                            Clear
                        </Button>
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
    </div>
</DashboardComponent>

<BottomNotification visible={isChanged}>
    <span class="ml-3 text-lg my-auto">You have unsaved changes!</span>
    <div>
        <Button variant="ghost" size="md" onClick={resetForm}>
            Cancel
        </Button>
        <Button variant="primary" size="md">
            Save
        </Button>
    </div>
</BottomNotification>

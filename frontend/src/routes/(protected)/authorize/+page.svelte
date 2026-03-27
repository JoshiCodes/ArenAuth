<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import Link from "$lib/components/ui/Link.svelte";
    import {PUBLIC_FALLBACK_IMG_URL} from "$env/static/public";
    import Button from "$lib/components/ui/Button.svelte";
    import {goto} from "$app/navigation";


    export let data;

    function continueAuth(authorised: boolean) {

    }

</script>

<Navbar />

<div class="w-screen h-screen flex justify-center content-center items-center">
    <div class="px-2 py-4 mx-2 dark:bg-zinc-950 rounded-lg">
        <div class="flex flex-col justify-center content-center items-center px-4">
            <div class="mt-2">
                <img src={PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", "PROJECT")} alt={"Project Logo"} class="w-full rounded-lg object-cover shadow-lg" />
            </div>
            <span class="text-xl font-bold mt-2">PROJECT</span>
            <span class="mb-2">wants to access your <b>Aren</b> account.</span>
            <span class="text-sm dark:text-zinc-100/50">Signed in as <span class="font-semibold">{data.me.username}</span>. <Link href="/logout">Not you?</Link></span>
        </div>
        <div class="dark:bg-zinc-800 mx-4 pt-2 md:pt-4 rounded-lg mt-4">
            <div class="w-4/5 mx-auto mt-2 dark:text-zinc-400">
                <span>This will allow the developer of <b>PROJECT</b> to:</span>
                <div class="mt-4 flex flex-col gap-y-2">
                    <div class="flex flex-row justify-start content-center items-center px-4 gap-x-2">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-6">
                            <path fill-rule="evenodd" d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm13.36-1.814a.75.75 0 1 0-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 0 0-1.06 1.06l2.25 2.25a.75.75 0 0 0 1.14-.094l3.75-5.25Z" clip-rule="evenodd" />
                        </svg>
                        <span>Access your username and avatar</span>
                    </div>
                    <div class="flex flex-row justify-start content-center items-center px-4 gap-x-2">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-6">
                            <path fill-rule="evenodd" d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm13.36-1.814a.75.75 0 1 0-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 0 0-1.06 1.06l2.25 2.25a.75.75 0 0 0 1.14-.094l3.75-5.25Z" clip-rule="evenodd" />
                        </svg>
                        <span>View your email</span>
                    </div>
                </div>
            </div>
            <hr class="mx-4 md:mx-8 my-4 text-zinc-300 dark:text-zinc-700" />
            <div class="w-4/5 mx-auto my-4 pb-2 md:pb-4 dark:text-zinc-400 text-sm">
                <div class="mt-4 flex flex-col gap-y-2">
                    <div class="flex flex-row justify-start content-center items-center px-4 gap-x-2">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5">
                            <path fill-rule="evenodd" d="M19.902 4.098a3.75 3.75 0 0 0-5.304 0l-4.5 4.5a3.75 3.75 0 0 0 1.035 6.037.75.75 0 0 1-.646 1.353 5.25 5.25 0 0 1-1.449-8.45l4.5-4.5a5.25 5.25 0 1 1 7.424 7.424l-1.757 1.757a.75.75 0 1 1-1.06-1.06l1.757-1.757a3.75 3.75 0 0 0 0-5.304Zm-7.389 4.267a.75.75 0 0 1 1-.353 5.25 5.25 0 0 1 1.449 8.45l-4.5 4.5a5.25 5.25 0 1 1-7.424-7.424l1.757-1.757a.75.75 0 1 1 1.06 1.06l-1.757 1.757a3.75 3.75 0 1 0 5.304 5.304l4.5-4.5a3.75 3.75 0 0 0-1.035-6.037.75.75 0 0 1-.354-1Z" clip-rule="evenodd" />
                        </svg>
                        <span>After you authorise, you will be redirected <b>outside Aren</b> to <b>http://localhost</b></span>
                    </div>
                </div>
                <div class="mt-4 flex flex-col gap-y-2">
                    <div class="flex flex-row justify-start content-center items-center px-4 gap-x-2">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5">
                            <path fill-rule="evenodd" d="M12 2.25c-5.385 0-9.75 4.365-9.75 9.75s4.365 9.75 9.75 9.75 9.75-4.365 9.75-9.75S17.385 2.25 12 2.25ZM12.75 6a.75.75 0 0 0-1.5 0v6c0 .414.336.75.75.75h4.5a.75.75 0 0 0 0-1.5h-3.75V6Z" clip-rule="evenodd" />
                        </svg>
                        <span>Active since <b>1998</b></span>
                    </div>
                </div>
            </div>
        </div>
        <div class="flex flex-row justify-center content-center items-center px-4 gap-x-2">
            <Button variant="ghost" class="w-1/3" onClick={() => continueAuth(false)}>
                Cancel
            </Button>
            <Button class="w-1/3"  onClick={() => continueAuth(false)}>
                Authorise
            </Button>
        </div>
    </div>
</div>
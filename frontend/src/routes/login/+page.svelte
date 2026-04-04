<script lang="ts">

    import {goto} from "$app/navigation";
    import { page } from "$app/state";
    import Navbar from "$lib/components/Navbar.svelte";
    import BackgroundBlob from "$lib/components/BackgroundBlob.svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import FloatingInput from "$lib/components/ui/forms/FloatingInput.svelte";
    import Link from "$lib/components/ui/Link.svelte";
    import {BACKEND_URL} from "$lib/vars";

    let username = "";
    let password = "";
    let error: string | null = null;
    let isLoading = false;

    function sanitizeReturnTo(value: string | null): string {
        if (!value || !value.startsWith("/") || value.startsWith("//") || value.includes("\\")) {
            return "/dashboard";
        }
        return value;
    }

    $: redirectTarget = sanitizeReturnTo(page.url.searchParams.get("returnTo"));

    async function handleLogin() {
        error = null;
        isLoading = true;

        try {
            const response = await fetch(BACKEND_URL + "/api/v1/internal/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, password }),
                credentials: 'include'
            });
            console.log(1, response)
            const data = await response.json();
            console.log(2, data)
            if (!response.ok) {
                error = data.error || "Login failed.";
                isLoading = false;
                return;
            }

            console.log(3)

            // redirect
            await goto(redirectTarget);
        } catch (e: any) {
            error = e.message;
        } finally {
            isLoading = false;
        }
    }
</script>

<BackgroundBlob />
<Navbar />

<div class="min-h-screen w-full flex justify-center items-center">
    <div class="w-1/4 h-1/3 text-center bg-gray-300 dark:bg-zinc-900 rounded-lg shadow-lg px-5 py-4 md:py-8">
        <h1 class="text-4xl font-mono font-bold">Login</h1>
        {#if error}
            <div class="mt-4 text-red-500 font-mono">{error}</div>
        {/if}
        <form class="mx-5 md:mx-8 my-4 md:my-7">
            <div class="flex flex-col gap-4 md:gap-6 mt-4 md:mt-8 text-left">
                <FloatingInput label="Username" id="username" bind:value={username} />
                <FloatingInput label="Password" id="password" type="password" bind:value={password} />
                <div class="mt-3 md:mt-5">
                    <Button type="submit" fullWidth onClick={handleLogin} loading={isLoading} disabled={isLoading}>
                        Login
                    </Button>
                </div>
            </div>
        </form>
        <hr class="my-2 md:my-5 text-zinc-700 mx-3" />
        <p class="mb-2 text-zinc-900/70 dark:text-zinc-100/60">
            By logging in you agree to our <Link href="/agb">AGB</Link> and <Link href="/tos">Terms of Service</Link>.
        </p>
    </div>
</div>
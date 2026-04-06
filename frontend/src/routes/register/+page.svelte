<script lang="ts">
    import { enhance } from '$app/forms';
    import Navbar from '$lib/components/Navbar.svelte';
    import BackgroundBlob from '$lib/components/BackgroundBlob.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import FloatingInput from '$lib/components/ui/forms/FloatingInput.svelte';
    import Link from '$lib/components/ui/Link.svelte';
    import {toastStore} from "$lib/components/toasts/toastStore";
    import {TURNSTILE_ENABLED} from "$lib/captcha";
    import {env} from "$env/dynamic/public";
    import {Turnstile} from "svelte-turnstile";

    export let form;

    let isLoading = false;

    let passwordMatch = true;

    let password: string;
    let passwordConfirm: string;

    function onPasswordChange() {
        passwordMatch = password === passwordConfirm;
    }

    let reset: () => void;

</script>

<BackgroundBlob />
<Navbar />

<div class="min-h-screen w-full flex justify-center items-center">
    <div class="w-1/4 h-1/3 text-center bg-gray-300 dark:bg-zinc-900 rounded-lg shadow-lg px-5 py-4 md:py-8">
        <h1 class="text-4xl font-mono font-bold">Register</h1>


        <form
                method="POST"
                use:enhance={() => {
                isLoading = true;
                return async ({ result, update }) => {

                    if(result.type === "failure") {
                        reset?.();
                        let msg;
                        if(result.data && result.data.error) {
                            msg = result.data.error;
                        } else {
                            msg = "Unknown error."
                        }
                        toastStore.add("Failed to log in: " + msg, {type: "error"})
                    }

                    await update();
                    isLoading = false;
                };
            }}
                class="mx-5 md:mx-8 my-4"
        >
            <div class="flex flex-col gap-4 md:gap-6 mt-4 md:mt-8 text-left">

                <FloatingInput label="Username" id="username" name="username" required />
                <FloatingInput label="E-Mail" id="email" type="email" name="email" required />
                <FloatingInput bind:value={password} onKeyChange={onPasswordChange} label="Password" id="password" type="password" name="password" hasError={!passwordMatch} required />
                <FloatingInput bind:value={passwordConfirm} onKeyChange={onPasswordChange} label="Confirm Password" id="password_confirm" type="password" name="password_confirm" hasError={!passwordMatch} required />

                {#if TURNSTILE_ENABLED}
                    <div class="mt-2 mx-auto">
                        <Turnstile bind:reset siteKey={env.PUBLIC_TURNSTILE_SITE_KEY} />
                    </div>
                {/if}

                <div class="mt-3 md:mt-5">
                    <Button type="submit" fullWidth disabled={isLoading || !passwordMatch}>
                        Create Account
                    </Button>

                    {#if !passwordMatch}
                        <p class="text-red-600 dark:text-red-500/70">Passwords do not match.</p>
                    {/if}
                </div>
            </div>
        </form>
        <p class="mb-2 text-zinc-900/70 dark:text-zinc-100/60">
            Already have an account? <Link href="/login">Log in!</Link>
        </p>
        <hr class="my-2 md:my-5 text-zinc-700 mx-3" />
        <p class="mb-2 text-zinc-900/70 dark:text-zinc-100/60">
            By creating an account you agree to our <Link href="/tos">Terms of Service</Link>.
        </p>
    </div>
</div>
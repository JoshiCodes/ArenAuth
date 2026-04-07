<script lang="ts">
    import { enhance } from '$app/forms';
    import Navbar from '$lib/components/Navbar.svelte';
    import BackgroundBlobs from '$lib/components/BackgroundBlobs.svelte';
    import BackgroundGrid from '$lib/components/BackgroundGrid.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import AuthCard from '$lib/components/ui/AuthCard.svelte';
    import FloatingInput from '$lib/components/ui/forms/FloatingInput.svelte';
    import Link from '$lib/components/ui/Link.svelte';
    import {toastStore} from "$lib/components/toasts/toastStore";
    import {TURNSTILE_ENABLED} from "$lib/captcha";
    import {env} from "$env/dynamic/public";
    import {Turnstile} from "svelte-turnstile";

    let { form } = $props();

    let isLoading = $state(false);
    let passwordMatch = $state(true);
    let password = $state("");
    let passwordConfirm = $state("");

    function onPasswordChange() {
        passwordMatch = password === passwordConfirm;
    }

    let reset: () => void = $state(() => {});

</script>

<BackgroundBlobs />
<BackgroundGrid />
<Navbar />

<div class="min-h-screen w-full flex justify-center items-center px-4 relative pt-20 pb-10">
    <AuthCard>
        <div class="text-center">
            <h1 class="text-3xl font-extrabold tracking-tight text-zinc-900 dark:text-white mb-2">
                Create an account
            </h1>
            <p class="text-zinc-500 dark:text-zinc-400 mb-8">
                Please enter your details to register
            </p>

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
                            toastStore.add("Failed to register: " + msg, {type: "error"})
                        }

                        await update();
                        isLoading = false;
                    };
                }}
                    class="space-y-6 text-left"
            >
                <div class="space-y-4">
                    <FloatingInput label="Username" id="username" name="username" required />
                    <FloatingInput label="E-Mail" id="email" type="email" name="email" required />
                    <FloatingInput bind:value={password} onkeyup={onPasswordChange} label="Password" id="password" type="password" name="password" hasError={!passwordMatch} required />
                    <FloatingInput bind:value={passwordConfirm} onkeyup={onPasswordChange} label="Confirm Password" id="password_confirm" type="password" name="password_confirm" hasError={!passwordMatch} required />
                </div>

                {#if !passwordMatch && passwordConfirm.length > 0}
                    <p class="text-sm text-red-600 dark:text-red-400 mt-2">
                        Passwords do not match.
                    </p>
                {/if}

                {#if TURNSTILE_ENABLED}
                    <div class="mt-4 flex justify-center">
                        <Turnstile bind:reset siteKey={env.PUBLIC_TURNSTILE_SITE_KEY} />
                    </div>
                {/if}

                <div class="pt-2">
                    <Button type="submit" fullWidth disabled={isLoading || !passwordMatch} size="lg" class="shadow-lg shadow-violet-500/20">
                        Create Account
                    </Button>
                </div>
            </form>

            <div class="mt-8 space-y-6">
                <p class="text-sm text-zinc-600 dark:text-zinc-400">
                    Already have an account? <Link href="/login" class="font-semibold text-violet-600 dark:text-violet-400 hover:underline">Log in</Link>
                </p>

                <div class="relative">
                    <div class="absolute inset-0 flex items-center" aria-hidden="true">
                        <div class="w-full border-t border-zinc-200 dark:border-zinc-800"></div>
                    </div>
                </div>

                <p class="text-xs text-zinc-500 dark:text-zinc-500 leading-relaxed px-4">
                    By creating an account you agree to our <Link href="/tos" class="hover:text-zinc-700 dark:hover:text-zinc-300 underline underline-offset-4 transition-colors">Terms of Service</Link>.
                </p>
            </div>
        </div>
    </AuthCard>
</div>
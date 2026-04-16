<script lang="ts">
    import {enhance} from '$app/forms';
    import Navbar from '$lib/components/Navbar.svelte';
    import BackgroundGrid from '$lib/components/BackgroundGrid.svelte';
    import Button from '$lib/components/ui/Button.svelte';
    import AuthCard from '$lib/components/ui/AuthCard.svelte';
    import FloatingInput from '$lib/components/ui/forms/FloatingInput.svelte';
    import Link from '$lib/components/ui/Link.svelte';
    import {toastStore} from "$lib/components/toasts/toastStore";

    let isLoading = false;
</script>

<BackgroundGrid />
<Navbar />

<div class="min-h-screen w-full flex justify-center items-center px-4 relative">
    <AuthCard>
        <div class="text-center">
            <h1 class="text-3xl font-extrabold tracking-tight text-zinc-900 dark:text-white mb-2">
                Welcome back
            </h1>
            <p class="text-zinc-500 dark:text-zinc-400 mb-8">
                Please enter your details to sign in
            </p>

            <form
                    method="POST"
                    use:enhance={() => {
                    isLoading = true;
                    return async ({ result, update }) => {

                        if(result.type === "failure") {
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
                    class="space-y-6 text-left"
            >
                <div class="space-y-4">
                    <FloatingInput label="Username" id="username" name="username" />
                    <FloatingInput label="Password" id="password" type="password" name="password" />
                </div>

                <div class="pt-2">
                    <Button type="submit" fullWidth disabled={isLoading} size="lg" class="shadow-lg shadow-violet-500/20">
                        Sign In
                    </Button>
                </div>
            </form>

            <div class="mt-8 space-y-6">
                <p class="text-sm text-zinc-600 dark:text-zinc-400">
                    New here? <Link href="/register" class="font-semibold text-violet-600 dark:text-violet-400 hover:underline">Create an account</Link>
                </p>

                <div class="relative">
                    <div class="absolute inset-0 flex items-center" aria-hidden="true">
                        <div class="w-full border-t border-zinc-200 dark:border-zinc-800"></div>
                    </div>
                </div>

                <p class="text-xs text-zinc-500 dark:text-zinc-500 leading-relaxed px-4">
                    By logging in you agree to our <Link href="/tos" class="hover:text-zinc-700 dark:hover:text-zinc-300 underline underline-offset-4 transition-colors">Terms of Service</Link>.
                </p>
            </div>
        </div>
    </AuthCard>
</div>
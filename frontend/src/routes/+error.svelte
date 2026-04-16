<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import Footer from "$lib/components/Footer.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";

    import {page} from '$app/state';
    import Button from "$lib/components/ui/Button.svelte";
    import {goto} from "$app/navigation";

    const error: any = page.error;
</script>

<BackgroundGrid />

<Navbar />

<main class="relative pt-32 pb-16 lg:pt-48 min-h-[70vh] flex items-center">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center max-w-4xl mx-auto">
            <div class="inline-block px-4 py-1.5 mb-6 text-sm font-medium rounded-full bg-red-500/10 text-red-600 dark:text-red-400 border border-red-500/20">
                Error {page.status}
            </div>
            
            <h1 class="text-5xl md:text-7xl font-extrabold tracking-tight text-zinc-900 dark:text-white mb-6">
                Something went <span class="underline text-red-500 dark:text-red-400">wrong:</span>
            </h1>

            <p class="text-xl text-zinc-600 dark:text-zinc-400 mb-4 leading-relaxed max-w-2xl mx-auto">
                {error?.message || "An unexpected error occurred while loading this page."}
            </p>

            <p class="text-zinc-500 dark:text-zinc-500 mb-10">
                If this keeps happening, maybe try contacting the site administrator.
            </p>

            <div class="flex flex-col sm:flex-row justify-center gap-4">
                <Button size="lg" class="px-8 py-6 text-lg shadow-xl shadow-violet-500/20" onClick={() => location.reload()}>
                    Refresh Page
                </Button>
                <Button variant="ghost" size="lg" class="px-8 py-6 text-lg border-zinc-200 dark:border-zinc-800" onClick={() => goto("/")}>
                    Back Home
                </Button>
            </div>
        </div>
    </div>
</main>

<Footer />
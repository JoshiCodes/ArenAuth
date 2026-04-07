<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import Footer from "$lib/components/Footer.svelte";
    import BackgroundBlobs from "$lib/components/BackgroundBlobs.svelte";

    import { page } from '$app/state';
    import Button from "$lib/components/ui/Button.svelte";
    import {goto} from "$app/navigation";
    const error: any = page.error;
</script>

<BackgroundBlobs />

<Navbar />

<main class="container mx-auto px-4 py-16 sm:px-6 lg:px-8">

    <div class="flex flex-col justify-center content-center items-center">
        <h1 class="font-bold text-3xl">Error {page.status}</h1>

        <span class="font-semibold text-xl">There was an error while loading this page:</span>
        <span class="font-mono text-xl my-2 md:my-4">
            {error?.message || "An unexpected error occurred."}
        </span>
        <span class="mt-2 md:mt-6">
            If this keeps happening, maybe try contacting the site administrator.
        </span>
        <div class="mt-2 md:mt-4 flex gap-x-2 md:gap-x-6">
            <Button variant="secondary" onClick={() => location.reload()}>
                Refresh Page
            </Button>
            <Button variant="secondary" onClick={() => goto("/")}>
                Back Home
            </Button>
        </div>
    </div>

</main>

<Footer />
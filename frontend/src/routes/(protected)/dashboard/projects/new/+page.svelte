<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import FloatingInput from "$lib/components/ui/forms/FloatingInput.svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import {PUBLIC_BACKEND_URL, PUBLIC_FALLBACK_IMG_URL} from "$env/static/public";

    let name: string = "";
    let description: string = "";
    let image: Blob|null = null; // TODO

    $: canSubmit = name.length >= 3;

    let error: string|null = null;

    function submit(): void {
        if(!canSubmit) return;
        error = null;
        const fd = new FormData();
        fd.append("name", name);
        fd.append("description", description);
        if(image)
            fd.append("image", image);
        fetch(PUBLIC_BACKEND_URL + "/api/internal/projects", {
            method: "POST",
            credentials: 'include',
            body: fd
        }).then(async res => {
            console.log(res)
            if(res.ok) {
                const data = await res.json();
                window.location.href = "/dashboard/projects/" + data.id;
            } else {
                const data = await res.json();
                error = data.error || "An error occurred while creating the project.";
            }
        }).catch(err => {
            error = err.message || "An error occurred while creating the project.";
        });
    }

</script>

<Navbar />
<DashboardSidebar />

<DashboardComponent>
    <div class="mt-6 w-full h-full">
        <h1 class="text-4xl font-bold text-zinc-700 dark:text-zinc-50">New Project</h1>
        {#if error}
            <div class="mt-4 p-4 bg-red-100 text-red-700 dark:bg-red-400/30 dark:text-red-400 rounded">
                {error}
            </div>
        {/if}
        <div class="mt-6 grid grid-cols-2 w-2/3">
            <FloatingInput id="name" label="Project name" required bind:value={name} class="col-span-1 row-span-1" />
            <div class="col-span-1 row-span-2 flex flex-col gap-y-4 justify-center content-center items-center">
                <!-- TODO -->
                <img src={PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", (name || "New Project"))}
                     alt="Project icon"
                     class="mt-2 md:mt-4 w-1/3 object-cover rounded-lg" />
                <Button variant="secondary" disabled>
                    Upload Image
                </Button>
            </div>
            <FloatingInput id="description" label="Project Description" type="text" required bind:value={description} class="col-span-1 row-span-1" />
        </div>
        <div class="w-full">
            <Button class="w-1/3" disabled={!canSubmit} onClick={submit}>
                Create
            </Button>
        </div>
    </div>
</DashboardComponent>
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
    let imagePreview: string|null = null;
    let fileInput: HTMLInputElement;

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
        fetch(PUBLIC_BACKEND_URL + "/api/v1/internal/projects", {
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

    function handleFileSelect(event: Event): void {
        const target = event.target as HTMLInputElement;
        const files = target.files;

        if(!files || files.length === 0) return;
        const file = files[0];

        if(!file.type.startsWith("image/")) {
            error = "Please select a valid image file. (PNP,JPG, WebP)";
            return;
        }

        if(file.size > 5 * 1024 * 1024) { // 5MB limit
            error = "Image size must be less than 5MB.";
            return;
        }

        image = file;
        error = null;

        const reader = new FileReader();
        reader.onload = (e) => {
            imagePreview = e.target?.result as string;
        };
        reader.readAsDataURL(file);

    }

    function triggerFileInput(): void {
        fileInput.click();
    }

    function clearImage(): void {
        image = null;
        fileInput.value = "";
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
                <img src={imagePreview || PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", (name || "New Project"))}
                     alt="Project icon"
                     class="mt-2 md:mt-4 w-1/3 object-cover rounded-lg" />
                <input
                    type="file"
                    bind:this={fileInput}
                    accept="image/*"
                    on:change={handleFileSelect}
                    class="hidden"
                />
                <div class="flex gap-2">
                    <Button variant="secondary" onClick={triggerFileInput}>
                        Upload Image
                    </Button>
                    {#if image}
                        <Button variant="secondary" onClick={clearImage}>
                            Clear
                        </Button>
                    {/if}
                </div>
                {#if image}
                    <p class="text-sm text-gray-500 dark:text-gray-400">
                        Selected: {image.name}
                    </p>
                {/if}
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
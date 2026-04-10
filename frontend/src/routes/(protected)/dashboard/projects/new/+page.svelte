<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";
    import FloatingInput from "$lib/components/ui/forms/FloatingInput.svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import {BACKEND_URL} from "$lib/vars";
    import BackgroundBlob from "$lib/components/BackgroundBlob.svelte";
    import {projectAvatarUrl} from "$lib/avatar";

    let sidebarOpen = $state(false);
    let name = $state("");
    let description = $state("");
    let image = $state<File|null>(null);
    let imagePreview = $state<string|null>(null);
    let fileInput: HTMLInputElement;

    const canSubmit = $derived(name.length >= 3);

    let error = $state<string|null>(null);

    function submit(): void {
        if(!canSubmit) return;
        error = null;
        const fd = new FormData();
        fd.append("name", name);
        fd.append("description", description);
        if(image)
            fd.append("image", image);
        fetch(BACKEND_URL + "/api/v1/internal/projects", {
            method: "POST",
            credentials: 'include',
            body: fd
        }).then(async res => {
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

<BackgroundBlob class="top-32 right-6 w-125 h-32 rounded-lg" />
<Navbar bind:sidebarOpen />
<DashboardSidebar bind:open={sidebarOpen} />

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
                    <img src={imagePreview || projectAvatarUrl(null, name)}
                         alt="Project icon"
                         class="mt-2 md:mt-4 w-1/3 object-cover rounded-lg" />
                    <input
                        type="file"
                        bind:this={fileInput}
                        accept="image/*"
                        onchange={handleFileSelect}
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
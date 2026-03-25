<script lang="ts">
    import { Sun, Moon } from "lucide-svelte";
    import { toggleMode, mode } from "mode-watcher";

    // Wir nutzen die Icons von lucide
</script>

<button onclick={toggleMode} class="p-2 transition-all rounded-lg text-zinc-800 dark:text-gray-50 hover:text-zinc-600 hover:bg-zinc-200 dark:hover:bg-zinc-800">
    {#if mode.current === "light"}
        <Sun class="h-[1.2rem] w-[1.2rem] " />
    {:else}
        <Moon class="h-[1.2rem] w-[1.2rem]" />
    {/if}
</button>
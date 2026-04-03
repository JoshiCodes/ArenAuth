<script lang="ts">
    export let visible: boolean = false;
    import { slide } from 'svelte/transition';
</script>

{#if visible}
    <div
            class="absolute bottom-4 left-1/3 right-1/3 bg-gray-200 dark:bg-zinc-950 shadow-lg rounded-lg px-2 py-4 flex justify-between items-center content-center"
            in:slide={{ duration: 200, axis: 'y' }}
            out:slide={{ duration: 200, axis: 'y' }}
    >
        <slot />
    </div>
{/if}
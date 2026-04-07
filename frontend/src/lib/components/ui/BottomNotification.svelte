<script lang="ts">
    interface Props {
        visible?: boolean;
        children?: import('svelte').Snippet;
    }

    let { visible = false, children }: Props = $props();
    import { slide } from 'svelte/transition';
</script>

{#if visible}
    <div
            class="fixed bottom-6 left-1/2 -translate-x-1/2 w-[90%] max-w-2xl bg-white/80 dark:bg-zinc-950/80 backdrop-blur-xl shadow-2xl rounded-2xl border border-zinc-200/50 dark:border-zinc-800/50 px-6 py-4 flex justify-between items-center z-[100]"
            in:slide={{ duration: 300, axis: 'y' }}
            out:slide={{ duration: 250, axis: 'y' }}
    >
        {@render children?.()}
    </div>
{/if}
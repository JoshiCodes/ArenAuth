<script lang="ts">
    import type { Snippet } from 'svelte';

    interface Props {
        title: string;
        description: string;
        icon?: Snippet;
        class?: string;
    }

    let { title, description, icon, class: className = '' }: Props = $props();
</script>

<div class="p-8 rounded-2xl bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm border border-zinc-200/50 dark:border-zinc-800/50 hover:border-violet-500/50 transition-colors {className}">
    {#if icon}
        <div class="w-12 h-12 rounded-lg bg-violet-100 dark:bg-violet-900/30 flex items-center justify-center text-violet-600 dark:text-violet-400 mb-6">
            {@render icon()}
        </div>
    {/if}
    <h3 class="text-xl font-bold text-zinc-900 dark:text-white mb-3">{title}</h3>
    <p class="text-zinc-600 dark:text-zinc-400 leading-relaxed">
        {description}
    </p>
</div>

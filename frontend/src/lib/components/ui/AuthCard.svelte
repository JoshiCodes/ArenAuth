<script lang="ts">
    import type { Snippet } from 'svelte';

    interface Props {
        children: Snippet;
        class?: string;
    }

    let { children, class: className = '' }: Props = $props();
</script>

<div class="w-full max-w-md bg-white/70 dark:bg-zinc-900/70 backdrop-blur-xl rounded-2xl shadow-2xl border border-zinc-200/50 dark:border-zinc-800/50 px-6 py-8 md:px-10 md:py-12 relative overflow-hidden group {className}">
    <!-- Subtiler Hintergrund-Glow Effekt -->
    <div class="absolute -top-24 -right-24 w-48 h-48 bg-violet-500/10 blur-3xl rounded-full group-hover:bg-violet-500/20 transition-colors duration-700"></div>
    
    <div class="relative z-10">
        {@render children()}
    </div>
</div>

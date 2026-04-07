<script lang="ts">
    import type { Snippet } from 'svelte';

    interface Props {
        href: string;
        children: Snippet;
        class?: string;
    }

    let { href, children, class: className = '' }: Props = $props();
</script>

<a {href} class="text-violet-600 dark:text-violet-400 hover:underline {className}">
    {@render children()}
</a>
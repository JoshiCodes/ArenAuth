<script lang="ts">
  import {page} from '$app/state';

  interface Props {
    href: string;
    icon?: import('svelte').Snippet;
    children?: import('svelte').Snippet;
    onClick?: () => void;
  }

  let { href, icon, children, onClick }: Props = $props();

  function isActive(href: string) {
    const path = page.url.pathname;
    if (href === '/dashboard') {
      return path === '/dashboard';
    }
    return path.startsWith(href);
  }

  const active = $derived(isActive(href));
</script>

<a
  {href}
  class="flex items-center gap-x-3 rounded-xl px-4 py-2.5 transition-all duration-200 group
    {active
      ? 'bg-zinc-200 text-violet-600 dark:bg-zinc-800 dark:text-violet-400'
      : 'text-zinc-600 hover:bg-zinc-200/50 dark:text-zinc-400 dark:hover:bg-zinc-800/50'}"
  onclick={onClick}
>
  {#if icon}
    <div class="transition-transform group-hover:scale-110">
      {@render icon()}
    </div>
  {/if}
  <span class="font-medium">{@render children?.()}</span>
</a>

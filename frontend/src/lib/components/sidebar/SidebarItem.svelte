<script lang="ts">
    import {page} from '$app/state';

    interface Props {
    href: string;
    exact?: boolean;
    icon?: import('svelte').Snippet;
    children?: import('svelte').Snippet;
    onClick?: () => void;
    class?: string;
    activeClass?: string;
  }

  let { href, exact = false, icon, children, onClick, class: className, activeClass }: Props = $props();

  function isActive(href: string) {
    const path = page.url.pathname;
    if(path === href) return true;

    if(exact) return path === href;

    return path.startsWith(href + "/");
  }

  const active = $derived(isActive(href));

  const classes = $derived([
    'flex items-center gap-x-3 rounded-xl px-4 py-2.5 transition-all duration-200 group',
    active
      ? [
          !activeClass?.includes('bg') && 'bg-zinc-200 dark:bg-zinc-800',
          !activeClass?.includes('text-') && 'text-violet-600 dark:text-violet-400',
          activeClass
        ]
      : [
          !className?.includes('bg') && 'hover:bg-zinc-200/50 dark:hover:bg-zinc-800/50',
          !className?.includes('text-') && 'text-zinc-600 dark:text-zinc-400'
        ],
    className
  ].flat().filter(Boolean).join(' '));
</script>

<a
  {href}
  class={classes}
  onclick={onClick}
>
  {#if icon}
    <div class="transition-transform group-hover:scale-110">
      {@render icon()}
    </div>
  {/if}
  <span class="font-medium">{@render children?.()}</span>
</a>

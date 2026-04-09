<script lang="ts">
  interface Props {
    title?: string;
    class?: string;
    children?: import('svelte').Snippet;
    border?: boolean;
  }

  let { title, class: className, children, border = true }: Props = $props();
</script>

<div class="{border ? 'mt-6 pt-6 border-t border-zinc-200/50 dark:border-zinc-800/50' : ''} px-0 {className}">
  {#if title}
    <span class="px-4 text-[10px] font-bold uppercase tracking-wider text-zinc-500 mb-2 block">
      {title}
    </span>
  {/if}
  <div class="flex flex-col gap-y-1">
    {@render children?.()}
  </div>
</div>

<script lang="ts">
  interface Props {
    title?: string;
    children?: import('svelte').Snippet;
  }

  let { title, children }: Props = $props();
</script>

<div class="mt-6 pt-6 border-t border-zinc-200/50 dark:border-zinc-800/50 px-0">
  {#if title}
    <span class="px-4 text-[10px] font-bold uppercase tracking-wider text-zinc-500 mb-2 block">
      {title}
    </span>
  {/if}
  <div class="flex flex-col gap-y-1">
    {@render children?.()}
  </div>
</div>

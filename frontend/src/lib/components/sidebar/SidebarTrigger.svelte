<script lang="ts">
  interface Props {
    open: boolean;
    class?: string;
  }

  let { open = $bindable(), class: className }: Props = $props();
</script>

<button
  type="button"
  class="inline-flex h-10 w-10 items-center justify-center rounded-lg hover:bg-zinc-100 dark:hover:bg-zinc-900 lg:hidden {className}"
  onclick={() => (open = !open)}
>
  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
    <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
  </svg>
  <span class="sr-only">Toggle Sidebar</span>
</button>

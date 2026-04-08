<script lang="ts">
  import {setContext, type Snippet} from 'svelte';
  import {writable} from 'svelte/store';
  import {fade, slide} from 'svelte/transition';

  interface Props {
    open?: boolean;
    onClose?: () => void;
    children?: Snippet;
  }

  let { open = $bindable(false), onClose, children }: Props = $props();

  // Create a store for sidebar state that sub-components can access
  const sidebarContext = writable({ open });
  $effect(() => {
    sidebarContext.set({ open });
  });
  setContext('sidebar', sidebarContext);

  function handleClose() {
    open = false;
    if (onClose) onClose();
  }
</script>

<!-- Mobile Sidebar (Drawer) -->
{#if open}
  <!-- Overlay -->
  <!-- svelte-ignore a11y_click_events_have_key_events -->
  <!-- svelte-ignore a11y_no_static_element_interactions -->
  <div
    class="fixed inset-0 z-50 bg-zinc-950/20 backdrop-blur-sm lg:hidden"
    transition:fade={{ duration: 200 }}
    onclick={handleClose}
  ></div>

  <!-- Sidebar Content (Mobile) -->
  <aside
    class="fixed inset-y-0 left-0 z-50 w-72 bg-white/80 dark:bg-zinc-950/80 backdrop-blur-xl border-r border-zinc-200/50 dark:border-zinc-800/50 lg:hidden"
    transition:slide={{ axis: 'x', duration: 300 }}
  >
    <div class="flex h-full flex-col">
      <div class="flex items-center justify-between px-6 py-4 lg:hidden">
        <span class="text-lg font-bold">Menu</span>
        <button
          type="button"
          class="rounded-lg p-2 hover:bg-zinc-100 dark:hover:bg-zinc-900"
          onclick={handleClose}
        >
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
      <nav class="flex-1 overflow-y-auto px-3 py-4">
        {@render children?.()}
      </nav>
    </div>
  </aside>
{/if}

<!-- Desktop Sidebar (Always Visible) -->
<aside
  class="fixed inset-y-0 left-0 z-40 hidden w-64 border-r border-zinc-200/50 bg-white/50 backdrop-blur-xl dark:border-zinc-800/50 dark:bg-zinc-950/50 lg:block"
>
  <div class="flex h-full flex-col">
    <nav class="flex-1 overflow-y-auto px-3 pt-24 pb-4">
      {@render children?.()}
    </nav>
  </div>
</aside>

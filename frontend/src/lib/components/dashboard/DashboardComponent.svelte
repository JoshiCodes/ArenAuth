<script lang="ts">
  interface Props {
    children?: import('svelte').Snippet;
  }

  let { children }: Props = $props();
</script>

<main class="pt-24 md:pt-28 pb-24 px-4 lg:ml-64 transition-all duration-300">
  {@render children?.()}
</main>
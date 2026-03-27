<script lang="ts">
    export let href: string;
</script>

<a href={href} class="text-violet-600  hover:underline ${$$restProps.class}">
    <slot />
</a>
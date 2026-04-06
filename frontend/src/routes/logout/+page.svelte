<script lang="ts">
    import { onMount } from 'svelte';
    import { goto, invalidate } from '$app/navigation';

    onMount(async () => {
        await invalidate('app:me');

        await goto('/', { replaceState: true });
    });
</script>

<p>Logging you out...</p>
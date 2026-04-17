<script lang="ts">
    import './layout.css';
    import favicon from '$lib/assets/favicon.svg';
    import {ModeWatcher} from 'mode-watcher';
    import ToastContainer from "$lib/components/toasts/ToastContainer.svelte";

    let {children} = $props();
</script>

<svelte:head>
    <link rel="icon" href={favicon} />
    <title>ArenAuth</title>
</svelte:head>

<ModeWatcher />
<ToastContainer />

{@render children()}

<script lang="ts">

    interface Props {
        classes?: string;
    }

    let { classes }: Props = $props();

</script>
<div class="-z-20 overflow-hidden pointer-events-none fixed top-40 -left-25 -translate-x-1/2 w-150 h-150 rounded-full {classes?.includes('bg') ? '' : 'bg-violet-700/75 dark:bg-violet-800'} opacity-30 blur-[160px] {classes}"></div>
<div class="-z-20 overflow-hidden pointer-events-none fixed top-60 -right-50 -translate-x-1/2 w-150 h-150 rounded-full {classes?.includes('bg') ? '' : 'bg-violet-600/85 dark:bg-violet-700'} opacity-30 blur-[160px] {classes}"></div>
<div class="-z-20 overflow-hidden pointer-events-none fixed top-40 left-1/2 -translate-x-1/2 w-150 h-150 rounded-full {classes?.includes('bg') ? '' : 'bg-violet-700/75 dark:bg-violet-800'} opacity-30 blur-[160px] {classes}"></div>
<div class="-z-20 overflow-hidden pointer-events-none fixed top-60 left-2/3 -translate-x-1/2 w-125 h-125 rounded-full {classes?.includes('bg') ? '' : 'bg-violet-700/50 dark:bg-violet-800'} opacity-20 blur-[160px] {classes}"></div>

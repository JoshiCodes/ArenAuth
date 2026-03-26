<script lang="ts">
    import type {MouseEventHandler} from "svelte/elements";

    // Optional Props
    export let type: 'button' | 'submit' | 'reset' = 'button';
    export let variant: 'primary' | 'secondary' | 'danger' | 'ghost' = 'primary';
    export let size: 'sm' | 'md' | 'lg' = 'md';

    export let disabled = false;
    export let loading = false;

    export let fullWidth = false;

    export let ariaLabel: string | undefined = undefined;

    export let onClick: MouseEventHandler<HTMLButtonElement> = () => {};

    const base =
        'btn inline-flex items-center justify-center gap-2 rounded-md font-medium ' +
        'transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 ' +
        'select-none ' +
        'transition-all duration-300 ease-in-out';

    const sizes: Record<typeof size, string> = {
        sm: 'h-8 px-3 text-sm',
        md: 'h-10 px-4 text-sm',
        lg: 'h-12 px-5 text-base'
    };

    const variants: Record<typeof variant, string> = {
        primary:
            'bg-violet-600 dark:bg-violet-800 text-white hover:bg-violet-500 hover:text-gray-50 dark:hover:bg-violet-900 dark:hover:text-gray-200 focus-visible:ring-violet-600 disabled:bg-violet-300',
        secondary:
            'bg-slate-200 text-slate-900 hover:bg-slate-300 dark:bg-slate-300 dark:text-slate-700 dark:hover:bg-slate-400 dark:hover:text-gray-50 focus-visible:ring-slate-400 disabled:bg-slate-100 disabled:text-slate-400',
        danger:
            'bg-red-600 text-white hover:bg-red-700 focus-visible:ring-red-600 disabled:bg-red-300',
        ghost:
            'border dark:bg-zinc-800 dark:border-zinc-800 dark:text-zinc-300 dark:hover:bg-zinc-700'
    };

    $: classes = [
        base,
        sizes[size],
        variants[variant],
        fullWidth ? 'w-full' : '',
        (disabled || loading) ? 'opacity-70 cursor-not-allowed' : 'cursor-pointer',
        $$restProps.class
    ]
        .filter(Boolean)
        .join(' ');
</script>

<button
        class={classes}
        {type}
        disabled={disabled || loading}
        aria-label={ariaLabel}
        aria-busy={loading}
        onclick={onClick}
>
    {#if loading}
        <span class="sr-only">Loading...</span>
    {/if}
    <slot />
</button>
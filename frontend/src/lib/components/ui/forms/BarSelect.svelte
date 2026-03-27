<script lang="ts">

    export let id: string;
    export let label: string|null;

    export let disabled: boolean = false;
    export let required: boolean = false;

    export let value: any = null;

    export let onChange: (value: any) => void = () => {};

</script>

<div class="${$$restProps.class}" on:change={onChange}>
    <div class="relative inline-block w-full">
        <label for={id} class="font-semibold mb-2 md:mb-4">{label}</label>
        <select id={id} bind:value={value}
               class="border-box w-full
               bg-zinc-200 dark:bg-zinc-800
               rounded-lg
               py-2 px-4 mt-2
               disabled:cursor-not-allowed
        " disabled={disabled} required={required}>
            <slot />
        </select>
    </div>
</div>
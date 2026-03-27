<script lang="ts">

    export let id: string;
    export let label: string|null;

    export let disabled: boolean = false;
    export let required: boolean = false;

    export let checked: boolean = false;

    export let onChange = () => {};

</script>

<div class="flex content-center items-center my-2">
    <input id={id} type="checkbox" bind:checked={checked} on:change={onChange} class="w-4 h-4 border border-default-medium rounded-xl accent-violet-500 dark:accent-violet-600 bg-neutral-secondary-medium focus:ring-violet-500 dark:focus:ring-violet-600">
    <label for={id} class="select-none ms-2 font-medium text-heading">{label}</label>
</div>
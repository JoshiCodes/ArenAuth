<script lang="ts">

    import type {HTMLInputTypeAttribute} from "svelte/elements";

    export let type: HTMLInputTypeAttribute = "text";
    export let id: string;
    export let label: string;

    export let required: boolean = false;

    export let value: any = null;

</script>

<div class="${$$restProps.class}">
    <label for={id} class="block mb-2 text-sm font-medium text-zinc-600 dark:text-zinc-400">{label}</label>
    <input type={type} id={id}
           required={required}
           class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500 dark:shadow-sm-light"
           bind:value
    >
</div>
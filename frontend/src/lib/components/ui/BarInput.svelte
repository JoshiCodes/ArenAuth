<script lang="ts">

    import type {HTMLInputTypeAttribute} from "svelte/elements";
    import Button from "$lib/components/ui/Button.svelte";

    export let type: HTMLInputTypeAttribute = "text";
    export let id: string;
    export let label: string;
    export let placeholder: string = "";

    export let disabled: boolean = false;
    export let required: boolean = false;

    export let copyButton: boolean = false;
    export let copyButtonDisabled: boolean = false;
    export let copyValue: string|null = null;

    export let value: any = null;

    let copied = false;

    function copy() {
        window.navigator.clipboard.writeText(copyValue ?? value);
        copied = true;
        setTimeout(() => {copied = false}, 2000)
    }

</script>

<div class="${$$restProps.class}">
    <div class="relative inline-block w-full">
        <label for={id} class="font-semibold mb-2 md:mb-4">{label}</label>
        <input type={type} id={id} bind:value={value}
               class="border-box w-full
               bg-zinc-200 dark:bg-zinc-800
               rounded-lg
               py-2 px-4
               disabled:cursor-not-allowed
        " disabled={disabled} placeholder={placeholder} />
        {#if copyButton}
            <button title="Copy" type="button"
                    class="absolute right-2 top-[40%] z-1
                     p-1 md:p-2 rounded-lg
                    {
                    copied ?
                     'bg-emerald-300 hover:bg-emerald-300 dark:bg-emerald-600 dark:hover:bg-emerald-600' :
                     'bg-zinc-300 enabled:hover:bg-zinc-400 ' +
                      'dark:bg-zinc-900 enabled:dark:hover:bg-zinc-950'
                     }
                    hover:cursor-pointer transition-colors duration-200
                    disabled:cursor-not-allowed
            " onclick={copy}
                disabled={copyButtonDisabled}
            >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                    {#if copied}
                        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6">
                            <path stroke-linecap="round" stroke-linejoin="round" d="m4.5 12.75 6 6 9-13.5" />
                        </svg>
                    {:else}
                        <path stroke-linecap="round" stroke-linejoin="round" d="M15.75 17.25v3.375c0 .621-.504 1.125-1.125 1.125h-9.75a1.125 1.125 0 0 1-1.125-1.125V7.875c0-.621.504-1.125 1.125-1.125H6.75a9.06 9.06 0 0 1 1.5.124m7.5 10.376h3.375c.621 0 1.125-.504 1.125-1.125V11.25c0-4.46-3.243-8.161-7.5-8.876a9.06 9.06 0 0 0-1.5-.124H9.375c-.621 0-1.125.504-1.125 1.125v3.5m7.5 10.375H9.375a1.125 1.125 0 0 1-1.125-1.125v-9.25m12 6.625v-1.875a3.375 3.375 0 0 0-3.375-3.375h-1.5a1.125 1.125 0 0 1-1.125-1.125v-1.5a3.375 3.375 0 0 0-3.375-3.375H9.75" />
                    {/if}
                </svg>
            </button>
        {/if}
    </div>
</div>
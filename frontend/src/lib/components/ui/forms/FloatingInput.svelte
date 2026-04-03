<script lang="ts">

    import type {ChangeEventHandler, HTMLInputTypeAttribute} from "svelte/elements";

    export let type: HTMLInputTypeAttribute = "text";
    export let id: string;
    export let label: string;

    export let required: boolean = false;

    export let value: any = null;
    export let onChange: ChangeEventHandler<HTMLInputElement> = () => {};
    export let onKeyChange: ChangeEventHandler<HTMLInputElement> = () => {};

</script>


<div class="relative z-0">
    <input type={type}
           id={id}
           bind:value={value}
           onchange={onChange}
           onkeyup={onKeyChange}
           required={required}
           class="block py-2.5 px-0 w-full text-sm text-heading bg-transparent
           border-0 border-b-2 border-default-medium appearance-none focus:outline-none
           focus:ring-0 focus:border-brand peer
            border-zinc-600 dark:border-zinc-500
            focus:border-violet-500 peer-focus:border-violet-500
            ${$$restProps.class}
" placeholder=" ">

    <label for={id} class="absolute text-sm text-body duration-300 transform -translate-y-6
     scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 peer-focus:text-fg-brand peer-placeholder-shown:scale-100
      peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 rtl:peer-focus:translate-x-1/4
       rtl:peer-focus:left-auto
       text-zinc-600 dark:text-zinc-400
       peer-focus:text-violet-500 dark:peer-focus:text-violet-400">{label}</label>

</div>
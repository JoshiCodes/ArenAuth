<script lang="ts">
    import type { ChangeEventHandler, HTMLInputTypeAttribute } from "svelte/elements";

    interface Props {
        type?: HTMLInputTypeAttribute;
        id: string;
        label: string;
        name?: string | null;
        hasError?: boolean;
        required?: boolean;
        value?: any;
        onchange?: ChangeEventHandler<HTMLInputElement>;
        onkeyup?: ChangeEventHandler<HTMLInputElement>;
        disabled?: boolean;
        class?: string;
    }

    let {
        type = "text",
        id,
        label,
        name = null,
        hasError = false,
        required = false,
        value = $bindable(null),
        onchange = () => {},
        onkeyup = () => {},
        disabled = false,
        class: className = ''
    }: Props = $props();

</script>


<div class="relative z-0">
    <input type={type}
           {id}
           bind:value={value}
           {onchange}
           {onkeyup}
           {required}
           {name}
           {disabled}
           class="block py-2.5 px-0 w-full text-sm text-zinc-900 dark:text-white bg-transparent
           border-0 border-b-2 appearance-none focus:outline-none
           focus:ring-0 peer

            focus:border-violet-500 peer-focus:border-violet-500
            {hasError ? 'border-red-600 dark:border-red-500/70' : 'border-zinc-600 dark:border-zinc-500'}
            {className}
" placeholder=" " >

    <label for={id} class="absolute text-sm duration-300 transform -translate-y-6
     scale-75 top-3 -z-10 origin-[0] peer-focus:start-0 peer-placeholder-shown:scale-100
      peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6 rtl:peer-focus:translate-x-1/4
       rtl:peer-focus:left-auto

       peer-focus:text-violet-500 dark:peer-focus:text-violet-400
            {hasError ? 'text-red-600 dark:text-red-500/70' : 'text-zinc-600 dark:text-zinc-400'}">
        {label}
    </label>

</div>
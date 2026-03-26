<script lang="ts">

    export let id: string;
    export let title: string;

    export let visible: boolean = false;

    let dialogElement: HTMLDialogElement | null = null;
    
    function outsideClick(event: MouseEvent) {
        if(!visible) return;
        if (event.target === event.currentTarget) {
            visible = false;
        }
    }

    $: if (dialogElement) {
        if (visible) {
            dialogElement.showModal();
        } else {
            dialogElement.close();
        }
    }

</script>

<dialog
        bind:this={dialogElement}
        {id}
        class="fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 m-0
        backdrop:bg-zinc-950/80 rounded-base border border-zinc-950
         shadow-lg rounded-lg
        outline-none
        p-4 md:p-6 max-w-2xl"
        on:click={outsideClick}
>
    <div class="bg-neutral-primary-soft">
        <div class="text-left">
            <span class="font-semibold text-2xl">{title}</span>
        </div>
        <hr class="my-2 text-zinc-700">
        <div>
            <slot />
        </div>
    </div>
</dialog>
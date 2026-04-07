<script lang="ts">

import Card from "$lib/components/ui/Card.svelte";
import type {Snippet} from "svelte";

interface Props {
    icon: Snippet;
    iconClass?: string;
    iconBgClass?: string;
    title: string;
    value: string;
    valueClass?: string;
    description: string;
    descriptionClass?: string;
    children?: Snippet;
}

let {
    icon,
    iconClass = "text-violet-600 dark:text-violet-400",
    iconBgClass = "bg-violet-500/10",
    title,
    value,
    valueClass = "text-violet-600 dark:text-violet-400",
    description,
    descriptionClass = "",
    children,
}: Props = $props();

</script>

<Card>
    <div class="w-12 h-12 rounded-xl flex items-center justify-center mb-4 {iconBgClass} {iconClass}">
        {@render icon()}
    </div>
    <h3 class="text-lg font-bold text-zinc-900 dark:text-white mb-1">{title}</h3>
    <p class="text-3xl font-black {valueClass}">{value}</p>
    <p class="text-sm text-zinc-500 mt-2 {descriptionClass}">{description}</p>
    {#if children}
        {@render children()}
    {/if}
</Card>
<script lang="ts">
    export let name: string;
    export let id: string;
    export let iconUrl: string|null = null;
    export let isClickable: boolean = true;

</script>


<a  href="{isClickable ? `/dashboard/projects/${id}` : '#'}">
<div class="bg-gray-100  hover:bg-gray-200 dark:bg-zinc-800 dark:hover:bg-zinc-700 p-2 rounded-lg text-center">
    <img src="{iconUrl || 'https://ui-avatars.com/api/?background=893DC3FF&color=fff&name=' + encodeURIComponent(name) }" alt="{name} icon" class="mx-auto mt-2 md:mt-4 h-32 w-32 object-cover rounded-lg" />
    <p class="my-2 md:my-6 font-semibold text-xl">{name}</p>
</div>
</a>
<script lang="ts">
    import {projectAvatarUrl} from "$lib/avatar";


    export let project: { name: string, id: string, avatarId: string };
    export let isClickable: boolean = true;
    const iconUrl = projectAvatarUrl(project.avatarId, project.name);

</script>


<a  href="{isClickable ? `/dashboard/projects/${project.id}` : '#'}">
<div class="p-6 rounded-2xl bg-white/70 hover:bg-gray-100/70 dark:bg-zinc-900/70 dark:hover:bg-zinc-800/70 backdrop-blur-xl border border-zinc-200/50 dark:border-zinc-800/50 transition-colors duration-200 shadow-xl">
    <img src="{iconUrl}" alt="{project.name} icon" class="mx-auto mt-2 md:mt-4 h-32 w-32 object-cover rounded-lg" />
    <p class="my-2 md:my-6 font-semibold text-xl text-center">{project.name}</p>
</div>
</a>
<script lang="ts">
    import {PUBLIC_BACKEND_URL, PUBLIC_FALLBACK_IMG_URL} from "$env/static/public";

    export let project: { name: string, id: string, avatarId: string };
    export let isClickable: boolean = true;

    const iconUrl = project.avatarId ? PUBLIC_BACKEND_URL + "/api/avatar/project/" + project.avatarId : PUBLIC_FALLBACK_IMG_URL.replaceAll("%name%", encodeURIComponent(project.name));

</script>


<a  href="{isClickable ? `/dashboard/projects/${project.id}` : '#'}">
<div class="bg-gray-100  hover:bg-gray-200 dark:bg-zinc-800 dark:hover:bg-zinc-700 p-2 rounded-lg text-center">
    <img src="{iconUrl}" alt="{project.name} icon" class="mx-auto mt-2 md:mt-4 h-32 w-32 object-cover rounded-lg" />
    <p class="my-2 md:my-6 font-semibold text-xl">{project.name}</p>
</div>
</a>
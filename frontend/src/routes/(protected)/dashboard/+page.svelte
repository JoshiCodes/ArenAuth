<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import DashboardSidebar from "$lib/components/dashboard/DashboardSidebar.svelte";
    import ComponentContainer from "$lib/components/ui/ComponentContainer.svelte";
    import DashboardComponent from "$lib/components/dashboard/DashboardComponent.svelte";


    export let data;
</script>

<Navbar />
<DashboardSidebar />

<DashboardComponent>
    <h1>Dashboard</h1>
    <p>User: {data.me.username}</p>
</DashboardComponent>
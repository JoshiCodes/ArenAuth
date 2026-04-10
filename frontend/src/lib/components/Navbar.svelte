<script lang="ts">
  import {page} from "$app/state";
  import ThemeToggle from "$lib/components/ThemeToggle.svelte";
  import Button from "$lib/components/ui/Button.svelte";
  import Link from "$lib/components/ui/Link.svelte";
  import {goto} from "$app/navigation";
  import SidebarTrigger from "./sidebar/SidebarTrigger.svelte";
  import UserAvatar from "$lib/components/UserAvatar.svelte";

  interface Props {
    sidebarOpen?: boolean;
  }

  let { sidebarOpen = $bindable(false) }: Props = $props();

  const me = $derived((page.data.me as Me | null | undefined) ?? null);

  async function handleLogout() {
    await goto("/logout", { replaceState: true });
  }

  const isDashboard = $derived(page.url.pathname.startsWith("/dashboard") || page.url.pathname.startsWith("/admin"));
</script>

<nav class="fixed inset-x-0 top-0 z-50 w-full px-4 py-4 flex items-center">
  <div class="flex items-center gap-x-3">
    {#if isDashboard}
      <SidebarTrigger bind:open={sidebarOpen} />
    {/if}
    <div class="text-2xl font-bold text-primary"><a href="/">ArenAuth</a></div>
  </div>
  <div class="ml-auto flex items-center gap-x-3 md:gap-x-6">
    {#if me}
      <ThemeToggle />
      <div class="flex items-center gap-2 text-sm text-zinc-800 dark:text-zinc-100">
        <Link href="/dashboard/profile" class="">
          <UserAvatar class="h-8 rounded-full shadow-lg" />
        </Link>
      </div>
      <div class="flex items-center gap-x-2">
        <a href="/dashboard">
          <Button variant="secondary">Dashboard</Button>
        </a>
        <Button variant="ghost" onClick={handleLogout}>Logout</Button>
      </div>
    {:else}
      <ThemeToggle />
      <a href="/login">
        <Button>Login</Button>
      </a>
    {/if}
  </div>
</nav>

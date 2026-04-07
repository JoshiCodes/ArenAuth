<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import Footer from "$lib/components/Footer.svelte";
    import BackgroundBlobs from "$lib/components/BackgroundBlobs.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";
    import Button from "$lib/components/ui/Button.svelte";
    import FeatureCard from "$lib/components/ui/FeatureCard.svelte";
</script>

{#snippet shieldIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
{/snippet}

{#snippet userIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
{/snippet}

{#snippet layersIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="2" width="20" height="8" rx="2" ry="2"/><rect x="2" y="14" width="20" height="8" rx="2" ry="2"/></svg>
{/snippet}

{#snippet zapIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>
{/snippet}

{#snippet lockIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
{/snippet}

{#snippet terminalIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3L15.5 7.5z"/></svg>
{/snippet}

<BackgroundBlobs />
<BackgroundGrid />

<Navbar />

<main class="relative pt-32 pb-16 lg:pt-48">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Hero Section -->
        <div class="text-center max-w-4xl mx-auto">
            <h1 class="text-5xl md:text-7xl font-extrabold tracking-tight text-zinc-900 dark:text-white mb-6">
                Your Modern <span class="text-violet-600 dark:text-violet-400">OAuth 2.0</span> & <span class="text-violet-600 dark:text-violet-400">OpenID Connect</span> Server
            </h1>
            <p class="text-xl text-zinc-600 dark:text-zinc-400 mb-10 leading-relaxed">
                ArenAuth is a powerful, self-hosted authentication platform built for developers who care about security and simplicity.
                Manage users, projects, and applications with ease.
            </p>
            <div class="flex flex-col sm:flex-row justify-center gap-4">
                <a href="/login">
                    <Button size="lg" class="px-8 py-6 text-lg shadow-xl shadow-violet-500/20">
                        Get Started
                    </Button>
                </a>
                <a href="https://github.com/JoshiCodes/ArenAuth" target="_blank">
                    <Button variant="ghost" size="lg" class="px-8 py-6 text-lg border-zinc-200 dark:border-zinc-800">
                        View on GitHub
                    </Button>
                </a>
            </div>
        </div>

        <!-- Features Grid -->
        <div class="mt-32 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            <FeatureCard 
                title="OAuth 2.0 & OIDC" 
                description="Industry standard authorization and authentication protocols. Secure your applications with JWT-based security."
                icon={shieldIcon}
            />

            <FeatureCard 
                title="User Management" 
                description="Easily manage users and projects. Built-in support for user profiles and avatar management."
                icon={userIcon}
            />

            <FeatureCard 
                title="Multi-Project Support" 
                description="Organize your applications into projects for better management and isolation."
                icon={layersIcon}
            />

            <FeatureCard 
                title="High Performance" 
                description="Built with Quarkus 3.x and Java 21 for blazing fast performance and low memory footprint."
                icon={zapIcon}
            />

            <FeatureCard 
                title="Self-Hosted" 
                description="Maintain full control over your data. Easy deployment with Docker and Docker Compose."
                icon={lockIcon}
            />

            <FeatureCard 
                title="Modern Tech Stack" 
                description="Leveraging Svelte 5 and Tailwind CSS for a modern, responsive user experience."
                icon={terminalIcon}
            />
        </div>

        <!-- Tech Stack Section -->
        <div class="mt-40">
            <h2 class="text-3xl font-bold text-zinc-900 dark:text-white mb-16 text-center">Powered by Modern Technology</h2>
            <div class="flex flex-wrap justify-center gap-x-12 gap-y-10 items-center max-w-5xl mx-auto">
                <div class="flex flex-col items-center group/tech">
                    <span class="text-2xl font-bold tracking-tight text-[#4695EB] dark:text-[#5fa6f0] drop-shadow-sm group-hover:scale-110 transition-transform duration-300">Quarkus</span>
                </div>
                <div class="flex flex-col items-center group/tech">
                    <span class="text-2xl font-bold tracking-tight text-[#FF3E00] dark:text-[#ff5c29] drop-shadow-sm group-hover:scale-110 transition-transform duration-300">Svelte 5</span>
                </div>
                <div class="flex flex-col items-center group/tech">
                    <span class="text-2xl font-bold tracking-tight text-[#06B6D4] dark:text-[#22d3ee] drop-shadow-sm group-hover:scale-110 transition-transform duration-300">Tailwind CSS</span>
                </div>
                <div class="flex flex-col items-center group/tech">
                    <span class="text-2xl font-bold tracking-tight text-[#336791] dark:text-[#4d86b3] drop-shadow-sm group-hover:scale-110 transition-transform duration-300">PostgreSQL</span>
                </div>
                <div class="flex flex-col items-center group/tech">
                    <span class="text-2xl font-bold tracking-tight text-[#2496ED] dark:text-[#4facf1] drop-shadow-sm group-hover:scale-110 transition-transform duration-300">Docker</span>
                </div>
            </div>
        </div>

        <!-- Final CTA -->
        <div class="mt-40 p-1px rounded-3xl bg-gradient-to-b from-zinc-200 to-transparent dark:from-zinc-800 dark:to-transparent">
            <div class="p-12 rounded-3xl bg-white dark:bg-zinc-950 text-center shadow-2xl overflow-hidden relative group border border-zinc-200/50 dark:border-zinc-800/50">
                <div class="absolute -top-24 -left-24 w-48 h-48 bg-violet-500/10 blur-3xl rounded-full"></div>
                <div class="absolute -bottom-24 -right-24 w-48 h-48 bg-violet-500/10 blur-3xl rounded-full"></div>
                
                <h2 class="text-3xl md:text-5xl font-bold mb-6 relative z-10 text-zinc-900 dark:text-white">Ready to secure your apps?</h2>
                <p class="text-lg md:text-xl text-zinc-600 dark:text-zinc-400 mb-10 relative z-10 max-w-2xl mx-auto">
                    Join our development journey and build something secure and beautiful with ArenAuth.
                </p>
                <div class="relative z-10 flex flex-col sm:flex-row justify-center gap-4">
                    <a href="/login">
                        <Button size="lg" class="px-10 py-6 text-lg shadow-xl shadow-violet-500/20">
                            Get Started for Free
                        </Button>
                    </a>
                </div>
            </div>
        </div>
    </div>
</main>

<Footer />
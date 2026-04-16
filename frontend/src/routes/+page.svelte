<script lang="ts">
    import Navbar from "$lib/components/Navbar.svelte";
    import Footer from "$lib/components/Footer.svelte";
    import BackgroundGrid from "$lib/components/BackgroundGrid.svelte";
    import Button from "$lib/components/ui/Button.svelte";
</script>

{#snippet shieldIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-zinc-500">
        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
    </svg>
{/snippet}
{#snippet userIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-zinc-500">
        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
        <circle cx="9" cy="7" r="4"/>
        <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
        <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
    </svg>
{/snippet}
{#snippet layersIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-zinc-500">
        <rect x="2" y="2" width="20" height="8" rx="2" ry="2"/>
        <rect x="2" y="14" width="20" height="8" rx="2" ry="2"/>
    </svg>
{/snippet}
{#snippet zapIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-zinc-500">
        <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
    </svg>
{/snippet}
{#snippet lockIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-zinc-500">
        <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
        <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
    </svg>
{/snippet}
{#snippet terminalIcon()}
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-zinc-500">
        <path d="M21 2l-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0l3 3L22 7l-3-3L15.5 7.5z"/>
    </svg>
{/snippet}


<BackgroundGrid/>
<Navbar/>

<main class="relative pt-24 pb-16 lg:pt-32">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 max-w-7xl">

        <div class="grid md:grid-cols-12 gap-12 items-center mb-32">
            <div class="md:col-span-7">
                <h1 class="text-5xl md:text-6xl font-extrabold tracking-tighter text-zinc-950 dark:text-white mb-6 leading-[1.1]">
                    Authentication. <br/> Integrated in <span
                        class="text-violet-600 dark:text-violet-400">Minutes</span>.
                </h1>
                <p class="text-xl text-zinc-700 dark:text-zinc-300 mb-10 leading-relaxed max-w-2xl">
                    ArenAuth provides a secure, self-hosted OAuth 2.0 and OpenID Connect server. Skip the complexity of
                    building auth and focus on your core application logic. Perfect for projects demanding full data
                    control and developer-centric simplicity.
                </p>
                <div class="flex flex-col sm:flex-row gap-4">
                    <a class="w-full sm:w-auto" href="/login">
                        <Button class="w-full px-8 py-6 text-lg" size="lg">
                            Get Started
                        </Button>
                    </a>
                    <a class="w-full sm:w-auto" href="https://github.com/JoshiCodes/ArenAuth" target="_blank">
                        <Button class="w-full px-8 py-6 text-lg border border-zinc-200 dark:border-zinc-800" size="lg"
                                variant="ghost">
                            View on Github
                        </Button>
                    </a>
                </div>
            </div>
            <div class="md:col-span-5 hidden md:block">
                <div class="aspect-square bg-zinc-50 dark:bg-zinc-900 rounded-3xl border border-zinc-100 dark:border-zinc-800 p-8 flex items-center justify-center text-zinc-400 dark:text-zinc-600 font-mono text-sm">
                    <div class="relative w-full aspect-square flex items-center justify-center">

                        <div class="absolute inset-0 flex items-center justify-center select-none pointer-events-none opacity-[0.03] dark:opacity-[0.05]">
                            <span class="text-[20rem] font-black">AA</span>
                        </div>

                        <div class="absolute top-10 left-0 z-20 animate-in fade-in slide-in-from-left-8 duration-700">
                            <div class="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 p-4 rounded-xl shadow-2xl flex items-center gap-4 min-w-60 cursor-default hover:-translate-y-1 hover:-translate-x-1 transition-all duration-300">
                                <div class="w-12 h-12 rounded-full bg-linear-to-tr from-violet-500 to-fuchsia-500"></div>
                                <div>
                                    <div class="text-sm font-bold text-zinc-900 dark:text-white">JoshiCodes</div>
                                    <div class="text-xs text-zinc-500">active_session: 2h 4m</div>
                                </div>
                                <div class="ml-auto w-2 h-2 rounded-full bg-emerald-500"></div>
                            </div>
                        </div>

                        <div class="relative z-10 w-full max-w-85 bg-white dark:bg-zinc-950 border-2 border-zinc-200 dark:border-zinc-800 rounded-lg p-8 shadow-[12px_12px_0px_0px_rgba(124,58,237,0.1)] dark:shadow-[12px_12px_0px_0px_rgba(139,92,246,0.1)]">
                            <div class="flex justify-between items-center mb-8">
                                <div class="font-black tracking-tighter text-xl">ArenAuth</div>
                                <div class="text-[10px] text-emerald-500 dark:text-emerald-600 cursor-default hover:text-emerald-400 transition-all duration-300 hover:shadow-emerald-300 hover:shadow-xl font-mono p-1 bg-zinc-100 dark:bg-zinc-800 rounded">v1.0.0</div>
                            </div>
                            <div class="space-y-4">
                                <div class="space-y-1.5">
                                    <div class="h-2 w-12 bg-zinc-200 dark:bg-zinc-800 rounded"></div>
                                    <div class="h-10 w-full border border-zinc-200 dark:border-zinc-800 bg-zinc-50 dark:bg-zinc-900 rounded"></div>
                                </div>
                                <div class="space-y-1.5">
                                    <div class="h-2 w-16 bg-zinc-200 dark:bg-zinc-800 rounded"></div>
                                    <div class="h-10 w-full border border-zinc-200 dark:border-zinc-800 bg-zinc-50 dark:bg-zinc-900 rounded"></div>
                                </div>
                                <div class="h-10 w-full bg-violet-600 rounded flex items-center justify-center">
                                    <div class="h-2 w-20 bg-white/30 rounded"></div>
                                </div>
                            </div>
                        </div>

                        <div class="absolute bottom-12 right-0 z-20 animate-in fade-in slide-in-from-right-8 duration-1000 delay-300">
                            <div class="bg-gray-100 dark:bg-[#09090b] text-[#a1a1aa] p-5 rounded-lg border border-zinc-800 shadow-2xl font-mono text-[10px] space-y-1 min-w-70 cursor-default hover:translate-y-1 hover:translate-x-1 transition-all duration-300">
                                <div class="flex justify-between text-zinc-500 border-b border-zinc-800 pb-2 mb-2">
                                    <span>GET /api/user/info</span>
                                    <span class="text-emerald-500">200 OK</span>
                                </div>
                                <div>&lbrace;</div>
                                    <div class="pl-4">"sub": <span class="text-emerald-400">"joshicodes"</span>,</div>
                                    <div class="pl-4">"scope": <span class="text-violet-400">"openid profile"</span>,</div>
                                    <div class="pl-4">"iss": <span class="text-emerald-400">"https://aren.joshicodes.de"</span></div>
                                    <div>&rbrace;</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 mb-32">

            <div class="space-y-12">
                <h2 class="text-3xl font-bold tracking-tight text-zinc-900 dark:text-white mb-10">Security & Core
                    Protocols</h2>

                <div class="flex items-start gap-4">
                    <div class="p-3 bg-white dark:bg-zinc-950 rounded-xl border border-zinc-200 dark:border-zinc-800">
                        {@render shieldIcon()}
                    </div>
                    <div>
                        <h3 class="text-xl font-semibold text-zinc-900 dark:text-white mb-2">OAuth 2.0 & OIDC
                            Standards</h3>
                        <p class="text-zinc-600 dark:text-zinc-400 leading-relaxed">Implement industry-standard
                            authorization and authentication. Secure your APIs and applications with robust JWT-based
                            security protocols.</p>
                    </div>
                </div>

                <div class="flex items-start gap-4">
                    <div class="p-3 bg-white dark:bg-zinc-950 rounded-xl border border-zinc-200 dark:border-zinc-800">
                        {@render lockIcon()}
                    </div>
                    <div>
                        <h3 class="text-xl font-semibold text-zinc-900 dark:text-white mb-2">Self-Hosted
                            Sovereignty</h3>
                        <p class="text-zinc-600 dark:text-zinc-400 leading-relaxed">Maintain absolute control over your
                            user data. Deploy on your own infrastructure easily using Docker and Docker Compose.</p>
                    </div>
                </div>
            </div>

            <div class="space-y-12 lg:pt-20">
                <h2 class="text-3xl font-bold tracking-tight text-zinc-900 dark:text-white mb-10">Developer
                    Experience</h2>

                <div class="flex items-start gap-4">
                    <div class="p-3 bg-white dark:bg-zinc-950 rounded-xl border border-zinc-200 dark:border-zinc-800">
                        {@render userIcon()}
                    </div>
                    <div>
                        <h3 class="text-xl font-semibold text-zinc-900 dark:text-white mb-2">Intuitive Management</h3>
                        <p class="text-zinc-600 dark:text-zinc-400 leading-relaxed">A clean interface to manage users,
                            projects, and client applications. Includes built-in support for user profiles and avatar
                            management.</p>
                    </div>
                </div>

                <div class="flex items-start gap-4">
                    <div class="p-3 bg-white dark:bg-zinc-950 rounded-xl border border-zinc-200 dark:border-zinc-800">
                        {@render zapIcon()}
                    </div>
                    <div>
                        <h3 class="text-xl font-semibold text-zinc-900 dark:text-white mb-2">High-Performance Core</h3>
                        <p class="text-zinc-600 dark:text-zinc-400 leading-relaxed">Built with Quarkus 3.x and Java 21,
                            ArenAuth delivers blazing-fast responses and has a remarkably low memory footprint.</p>
                    </div>
                </div>
            </div>

        </div>

        <div class="bg-white dark:bg-zinc-950 rounded-3xl border border-zinc-100 dark:border-zinc-800 p-10 md:p-16 mb-32">
            <div class="grid md:grid-cols-2 gap-12">
                <div>
                    <h2 class="text-4xl font-extrabold tracking-tighter text-zinc-950 dark:text-white mb-6">Built for
                        <br/>Scale & Simplicity</h2>
                    <p class="text-lg text-zinc-700 dark:text-zinc-300 mb-8 leading-relaxed">
                        ArenAuth leverages a modern, efficient technology stack to ensure performance and reliability,
                        making it easy to integrate and scale as your needs grow.
                    </p>
                </div>
                <div class="space-y-6">
                    <div class="flex items-center gap-3 p-4 bg-zinc-50 dark:bg-zinc-900/50 rounded-2xl border border-zinc-100 dark:border-zinc-800 hover:-translate-y-1 transition-all duration-300">
                        <span class="w-3 h-3 rounded-full bg-[#4695EB]"></span>
                        <span class="font-mono text-zinc-900 dark:text-white">quarkus.io</span>
                        <span class="text-zinc-500 dark:text-zinc-600 ml-auto text-sm">Backend Core (Java 21)</span>
                    </div>
                    <div class="flex items-center gap-3 p-4 bg-zinc-50 dark:bg-zinc-900/50 rounded-2xl border border-zinc-100 dark:border-zinc-800 hover:-translate-y-1 transition-all duration-300">
                        <span class="w-3 h-3 rounded-full bg-[#FF3E00]"></span>
                        <span class="font-mono text-zinc-900 dark:text-white">svelte.dev</span>
                        <span class="text-zinc-500 dark:text-zinc-600 ml-auto text-sm">Frontend Interface (Svelte 5)</span>
                    </div>
                    <div class="flex items-center gap-3 p-4 bg-zinc-50 dark:bg-zinc-900/50 rounded-2xl border border-zinc-100 dark:border-zinc-800 hover:-translate-y-1 transition-all duration-300">
                        <span class="w-3 h-3 rounded-full bg-[#336791]"></span>
                        <span class="font-mono text-zinc-900 dark:text-white">postgresql.org</span>
                        <span class="text-zinc-500 dark:text-zinc-600 ml-auto text-sm">Persistent Storage</span>
                    </div>
                    <div class="flex items-center gap-3 p-4 bg-zinc-50 dark:bg-zinc-900/50 rounded-2xl border border-zinc-100 dark:border-zinc-800 hover:-translate-y-1 transition-all duration-300">
                        <span class="w-3 h-3 rounded-full bg-[#2496ED]"></span>
                        <span class="font-mono text-zinc-900 dark:text-white">docker.com</span>
                        <span class="text-zinc-500 dark:text-zinc-600 ml-auto text-sm">Containerization</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="text-center max-w-xl mx-auto mb-20">
            <h2 class="text-4xl font-extrabold tracking-tighter text-zinc-950 dark:text-white mb-6">
                Start Securing Your Applications
            </h2>
            <p class="text-lg text-zinc-700 dark:text-zinc-300 mb-10 leading-relaxed">
                Join our development journey. Deploy ArenAuth today and build something secure and beautiful.
            </p>
            <a href="/login">
                <Button class="px-10 py-6 text-xl shadow-none" size="lg">
                    Create Your First Project
                </Button>
            </a>
        </div>

    </div>
</main>

<Footer/>
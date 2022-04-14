<script lang="ts">
    import {
        Button,
        DataTable,
        DataTableSkeleton,
        Search,
        Toolbar,
        ToolbarBatchActions,
        ToolbarContent,
        ToolbarMenu,
        ToolbarMenuItem,
        SkeletonText,
        InlineLoading,
    } from "carbon-components-svelte";
    import { Filter16 as Filter, AddAlt16 as Add } from "carbon-icons-svelte";
    import * as http from "@tauri-apps/api/http";
    import Fuse from "fuse.js";
    import { afterUpdate } from "svelte";
    import { steamApps } from "./loadData";
    import type { JSONSteamAppInfo, SteamApp } from "./types";
    import { profile } from "./stores";

    const options: Fuse.IFuseOptions<SteamApp> = {
        isCaseSensitive: false,
        // includeScore: false,
        shouldSort: true,
        // includeMatches: false,
        // findAllMatches: false,
        // minMatchCharLength: 1,
        // location: 0,
        // threshold: 0.6,
        // distance: 100,
        // useExtendedSearch: false,
        // ignoreLocation: false,
        // ignoreFieldNorm: false,
        // fieldNormWeight: 1,
        keys: ["title"],
    };

    const fuse = new Fuse(steamApps, options);

    let container: HTMLElement,
        results: SteamApp[] = [],
        lastResultsCount = 1,
        lastResultsWidth = 0,
        query = "",
        isSearching = false,
        searchTimer: NodeJS.Timeout;

    afterUpdate(() => {
        const resultsSkeleton = container.querySelector<HTMLElement>(
            ".skeleton-sticky-fix > table"
        );
        if (resultsSkeleton) {
            resultsSkeleton.style.minWidth = `${lastResultsWidth}px`;
            resultsSkeleton.style.width = `${lastResultsWidth}px`;
            // css fix
            resultsSkeleton.classList.add("bx--data-table--sticky-header");
        }
        const results = container.querySelector<HTMLElement>(
            ".bx--data-table-container"
        );
        if (results) {
            lastResultsWidth = results.offsetWidth;
        }
    });

    function search() {
        isSearching = true;
        clearTimeout(searchTimer);
        searchTimer = setTimeout(() => {
            results = fuse
                .search(query, {
                    limit: 30,
                })
                .map((r) => r.item);
            isSearching = false;
            lastResultsCount = Math.max(1, results.length);
        }, 1000);
    }

    async function fetchType(appId: number) {
        const response = await http.fetch<JSONSteamAppInfo>(
            `http://store.steampowered.com/api/appdetails?appids=${appId}`
        );
        const type = response.data[appId].data.type;
        steamApps.get(appId);
        return type;
    }
</script>

<div bind:this={container} class="container search">
    <Search
        searchClass="query"
        placeholder="Search steam game..."
        bind:value={query}
        on:input={search}
    />
    {#if isSearching}
        <DataTableSkeleton
            size="short"
            showHeader={false}
            class="skeleton-sticky-fix"
            headers={[
                {
                    empty: true,
                },
                {
                    empty: true,
                },
                {
                    empty: true,
                },
            ]}
            rows={lastResultsCount}
        />
    {:else}
        <DataTable
            stickyHeader
            selectable
            size="short"
            sortable
            headers={[
                { key: "id", value: "App ID" },
                { key: "title", value: "Title" },
                { key: "type", value: "Type" },
            ]}
            rows={results}
            nonSelectableRowIds={$profile.steamApps.map((e) => e.id)}
        >
            <svelte:fragment slot="cell" let:row let:cell>
                {#if cell.key == "type" && !cell.value}
                    {#await fetchType(row.id)}
                        <SkeletonText width="5ch" />
                    {:then type}
                        {type}
                    {:catch error}
                        <InlineLoading
                            status="error"
                            description="unavailable"
                        />
                    {/await}
                {:else}
                    {cell.value}
                {/if}
            </svelte:fragment>
            <Toolbar size="sm">
                <ToolbarBatchActions>
                    <Button icon={Add}>Add Games</Button>
                </ToolbarBatchActions>
                <ToolbarContent>
                    <ToolbarMenu icon={Filter}>
                        <ToolbarMenuItem>Games</ToolbarMenuItem>
                        <ToolbarMenuItem>DLC</ToolbarMenuItem>
                        <ToolbarMenuItem>Other</ToolbarMenuItem>
                    </ToolbarMenu>
                </ToolbarContent>
            </Toolbar>
        </DataTable>
    {/if}
</div>

<style lang="scss">
    .container {
        display: flex;
        flex-direction: column;
        // gap: 0.3rem;

        :global(.bx--data-table-container) {
            flex-grow: 1;
        }

        :global(.bx--skeleton) {
            :global(.bx--table-toolbar),
            :global(.bx--toolbar-content) {
                min-height: inherit;
                max-height: 2rem;
            }
        }
    }
</style>

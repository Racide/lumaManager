<script lang="ts">
    import {
        DataTable,
        Toolbar,
        ToolbarContent,
        ToolbarMenu,
        ToolbarMenuItem,
        ToolbarBatchActions,
        Button,
    } from "carbon-components-svelte";
    import {
        Filter16 as Filter,
        TrashCan16 as TrashCan,
        Save16 as Save,
    } from "carbon-icons-svelte";
    import { profile } from "./stores";

    let selectedRowIds: number[] = [];
</script>

<DataTable
    stickyHeader
    batchSelection
    sortable
    class="games"
    size="short"
    headers={[{ key: "title", value: "Games List" }]}
    rows={$profile.steamApps}
    bind:selectedRowIds
>
    <Toolbar size="sm">
        <ToolbarBatchActions>
            <Button icon={TrashCan}>Remove Games</Button>
        </ToolbarBatchActions>
        <ToolbarContent>
            <ToolbarMenu icon={Filter}>
                <ToolbarMenuItem>Games</ToolbarMenuItem>
                <ToolbarMenuItem>DLC</ToolbarMenuItem>
                <ToolbarMenuItem>Other</ToolbarMenuItem>
            </ToolbarMenu>
            <Button icon={Save}>Generate</Button>
        </ToolbarContent>
    </Toolbar>
</DataTable>

<style lang="scss">
</style>

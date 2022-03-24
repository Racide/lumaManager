<script lang="ts">
    import {
        DataTable,
        Toolbar,
        ToolbarContent,
        ToolbarSearch,
        ToolbarMenu,
        ToolbarMenuItem,
        ToolbarBatchActions,
        Button,
    } from "carbon-components-svelte";

    import { Filter16 as Filter, AddAlt16 as Add } from "carbon-icons-svelte";
</script>

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
    rows={[
        {
            id: "a",
            title: "Load Balancer 1",
            type: "HTTP",
        },
        {
            id: "b",
            title: "Load Balancer 2",
            type: "HTTP",
        },
        {
            id: "c",
            title: "Load Balancer 3",
            type: "HTTP",
        },
        {
            id: "d",
            title: "Load Balancer 4",
            type: "HTTP",
        },
        {
            id: "e",
            title: "Load Balancer 5",
            type: "HTTP",
        },
        {
            id: "f",
            title: "Load Balancer 6",
            type: "HTTP",
        },
        {
            id: "g",
            title: "Load Balancer 7",
            type: "HTTP",
        },
        {
            id: "h",
            title: "Load Balancer 8",
            type: "HTTP",
        },
        {
            id: "i",
            title: "Load Balancer 9",
            type: "HTTP",
        },
        {
            id: "j",
            title: "Load Balancer 10",
            type: "HTTP",
        },
        {
            id: "k",
            title: "Load Balancer 11",
            type: "HTTP",
        },
        {
            id: "l",
            title: "Load Balancer 12",
            type: "HTTP",
        },
    ]}
>
    <Toolbar size="sm">
        <ToolbarBatchActions>
            <Button icon={Add}>Add Games</Button>
        </ToolbarBatchActions>
        <ToolbarContent>
            <ToolbarSearch persistent />
            <ToolbarMenu icon={Filter}>
                <ToolbarMenuItem primaryFocus>Restart all</ToolbarMenuItem>
                <ToolbarMenuItem href="">API documentation</ToolbarMenuItem>
                <ToolbarMenuItem danger>Stop all</ToolbarMenuItem>
            </ToolbarMenu>
        </ToolbarContent>
    </Toolbar>
</DataTable>

<style lang="scss">
</style>

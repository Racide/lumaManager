<script>
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
    } from "carbon-icons-svelte";
</script>

<DataTable
    stickyHeader
    batchSelection
    sortable
    size="short"
    headers={[{ key: "name", value: "Games List" }]}
    rows={[
        {
            id: "a",
            name: "Load Balancer 1",
        },
        {
            id: "b",
            name: "Load Balancer 2",
        },
        {
            id: "c",
            name: "Load Balancer 3",
        },
        {
            id: "d",
            name: "Load Balancer 4",
        },
        {
            id: "e",
            name: "Load Balancer 5",
        },
        {
            id: "f",
            name: "Load Balancer 6",
        },
        {
            id: "g",
            name: "Load Balancer 7",
        },
        {
            id: "h",
            name: "Load Balancer 8",
        },
        {
            id: "i",
            name: "Load Balancer 9",
        },
        {
            id: "j",
            name: "Load Balancer 10",
        },
        {
            id: "k",
            name: "Load Balancer 11",
        },
        {
            id: "l",
            name: "Load Balancer 12",
        },
    ]}
>
    <Toolbar size="sm">
        <ToolbarBatchActions>
            <Button icon={TrashCan}>Remove Games</Button>
        </ToolbarBatchActions>
        <ToolbarContent>
            <ToolbarMenu icon={Filter}>
                <ToolbarMenuItem primaryFocus>Restart all</ToolbarMenuItem>
                <ToolbarMenuItem
                    href="https://cloud.ibm.com/docs/loadbalancer-service"
                    >API documentation</ToolbarMenuItem
                >
                <ToolbarMenuItem danger>Stop all</ToolbarMenuItem>
            </ToolbarMenu>
        </ToolbarContent>
    </Toolbar>
</DataTable>

<style lang="scss">
</style>

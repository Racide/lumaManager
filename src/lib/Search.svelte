<script>
    import {
        DataTable,
        Toolbar,
        ToolbarContent,
        ToolbarSearch,
        ToolbarMenu,
        ToolbarMenuItem,
        Button,
    } from "carbon-components-svelte";
</script>

<DataTable
    batchSelection
    size="short"
    headers={[
        { key: "id", value: "App ID" },
        { key: "title", value: "Title" },
        { key: "type", value: "Type" },
    ]}
    rows={[
        {
            id: "a",
            title: "Load Balancer 3",
            type: "HTTP",
        },
        {
            id: "b",
            title: "Load Balancer 1",
            type: "HTTP",
        },
        {
            id: "c",
            title: "Load Balancer 2",
            type: "HTTP",
        },
        {
            id: "d",
            title: "Load Balancer 6",
            type: "HTTP",
        },
        {
            id: "e",
            title: "Load Balancer 4",
            type: "HTTP",
        },
        {
            id: "f",
            title: "Load Balancer 5",
            type: "HTTP",
        },
    ]}
>
    <Toolbar>
        <ToolbarContent>
            <ToolbarSearch persistent />
            <ToolbarMenu>
                <ToolbarMenuItem primaryFocus>Restart all</ToolbarMenuItem>
                <ToolbarMenuItem
                    href="https://cloud.ibm.com/docs/loadbalancer-service"
                    >API documentation</ToolbarMenuItem
                >
                <ToolbarMenuItem danger>Stop all</ToolbarMenuItem>
            </ToolbarMenu>
            <Button>Create balancer</Button>
        </ToolbarContent>
    </Toolbar>
</DataTable>

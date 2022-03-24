<script lang="ts">
    import { Dropdown, Button } from "carbon-components-svelte";
    import { Add16 as Add } from "carbon-icons-svelte";

    let items = [
        { id: "0", text: "Slack" },
        { id: "1", text: "Email" },
        { id: "2", text: "Fax" },
    ];
</script>

<div>
    <Dropdown titleText="Contact" selectedId="0" {items} let:item let:index>
        <div>
            <strong>{item.text}</strong>
        </div>
        <div>
            id: {item.id} - index:
            {index}
        </div>
    </Dropdown>
    <Button
        kind="secondary"
        size="field"
        iconDescription="New Profile"
        icon={Add}
    />
</div>

<style lang="scss">
    div {
        display: flex;
        align-items: flex-end;

        > :global(:nth-child(1)) {
            flex-grow: 1;
        }
    }

    :global(.bx--list-box__menu-item, .bx--list-box__menu-item__option) {
        height: auto;
    }
</style>

<script lang="ts">
    import {
        Dropdown,
        Button,
        ComposedModal,
        ModalHeader,
        ModalBody,
        ModalFooter,
        TextInput,
        Modal,
    } from "carbon-components-svelte";
    import { Add16 as Add, TrashCan16 as Delete } from "carbon-icons-svelte";
    import { profile, profiles } from "./stores";
    import { Profile } from "./types";

    let newProfileModal_open = false,
        newProfileModal_name = "",
        newProfileModal_invalidText = "",
        delProfileModal_open = false,
        delProfileModal_name = "",
        delProfileModal_id = "",
        dropdown_open = false,
        dropdown_id = $profile.id;

    $: $profile = $profiles.get(dropdown_id)!;

    function addProfile() {
        newProfileModal_name = newProfileModal_name.trim();
        const profile = new Profile(newProfileModal_name);
        profiles.addProfile(profile);
        dropdown_id = profile.id;
        newProfileModal_open = false;
    }

    function delProfile() {
        profiles.delProfile(delProfileModal_id);
        if (dropdown_id == delProfileModal_id) {
            dropdown_id = $profiles[0].id;
        }
        delProfileModal_open = false;
    }
</script>

<div class="container">
    <Dropdown
        class="dropdown"
        titleText="Profiles"
        bind:selectedId={dropdown_id}
        items={$profiles}
        bind:open={dropdown_open}
        let:item={profile}
    >
        <div>{profile.text}</div>
        <Button
            kind="danger-ghost"
            size="field"
            tooltipPosition="left"
            iconDescription="Delete &quot;{profile.text}&quot; Profile"
            icon={Delete}
            on:click={(e) => {
                e.stopPropagation();
                dropdown_open = false;
                delProfileModal_name = profile.text;
                delProfileModal_id = profile.id;
                delProfileModal_open = true;
            }}
            disabled={$profiles.length < 2}
        />
    </Dropdown>
    <Button
        kind="secondary"
        size="field"
        iconDescription="New Profile"
        icon={Add}
        on:click={() => {
            newProfileModal_name = "";
            newProfileModal_open = true;
        }}
    />
</div>
<ComposedModal
    size="sm"
    bind:open={newProfileModal_open}
    selectorPrimaryFocus=".bx--text-input"
    on:submit={addProfile}
>
    <ModalHeader label="New Profile" title="Profile name:" />
    <ModalBody hasForm>
        <TextInput
            hideLabel
            placeholder="Enter profile name..."
            invalidText={newProfileModal_invalidText}
            invalid={!!newProfileModal_invalidText}
            bind:value={newProfileModal_name}
            on:input={() => {
                const length = newProfileModal_name.trim().length;
                if (length < 1 || length > 10) {
                    newProfileModal_invalidText =
                        "Profile name must be between 1 and 10 characters.";
                } else if ($profiles.has(newProfileModal_name.trim())) {
                    newProfileModal_invalidText = "Profile name must be unique";
                } else {
                    newProfileModal_invalidText = "";
                }
            }}
        />
    </ModalBody>
    <ModalFooter
        primaryButtonText="Confirm"
        primaryButtonDisabled={!!newProfileModal_invalidText}
    />
</ComposedModal>
<Modal
    danger
    size="sm"
    bind:open={delProfileModal_open}
    modalHeading="Delete the &quot;{delProfileModal_name}&quot; Profile?"
    primaryButtonText="Delete"
    secondaryButtonText="Cancel"
    on:click:button--secondary={() => (delProfileModal_open = false)}
    on:open
    on:close
    on:submit={delProfile}
>
    This is a permanent action and cannot be undone.
</Modal>

<style lang="scss">
    .container {
        display: flex;
        align-items: flex-end;

        :global(.dropdown) {
            flex-grow: 1;
        }

        :global(.bx--list-box__menu) {
            min-width: min-content;
            max-height: unset;
            overflow: visible;
        }

        :global(.bx--list-box__menu-item__option) {
            display: flex;
            align-items: center;
            padding-right: 0;
            margin-right: 0;
            overflow: inherit;

            > :global(:nth-child(1)) {
                flex-grow: 1;
                margin-right: 0.7rem;
            }

            :global(svg) {
                margin: 0;
            }
        }
    }
</style>

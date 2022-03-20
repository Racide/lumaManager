<script lang="ts">
  import { appWindow } from "@tauri-apps/api/window";
  import { onMount } from "svelte";
  import {
    Header,
    HeaderNav,
    HeaderNavItem,
    HeaderNavMenu,
    HeaderUtilities,
    HeaderGlobalAction,
    Content,
  } from "carbon-components-svelte";
  import {
    Close16 as Close,
    Subtract16 as Subtract,
    Maximize16 as Maximize,
  } from "carbon-icons-svelte";
  import Search from "./lib/Search.svelte";
  import Games from "./lib/Games.svelte";

  onMount(() => {
    document
      .querySelectorAll(
        ".bx--header *:not(.bx--header__menu-bar *, .bx--header__global *)"
      )
      .forEach((el) => el.setAttribute("data-tauri-drag-region", ""));
  });
</script>

<Header>
  <svelte:fragment slot="platform">
    <img src="../src-tauri/icons/32x32.png" alt="logo" class="icon" />
    GreenLuma Manager
  </svelte:fragment>
  <HeaderNav>
    <HeaderNavItem text="Link 1" />
    <HeaderNavItem text="Link 2" />
    <HeaderNavItem text="Link 3" />
    <HeaderNavMenu text="Menu">
      <HeaderNavItem text="Link 1" />
      <HeaderNavItem text="Link 2" />
      <HeaderNavItem text="Link 3" />
    </HeaderNavMenu>
  </HeaderNav>
  <HeaderUtilities>
    <HeaderGlobalAction icon={Subtract} on:click={() => appWindow.minimize()} />
    <HeaderGlobalAction
      icon={Maximize}
      on:click={() => appWindow.toggleMaximize()}
    />
    <HeaderGlobalAction icon={Close} on:click={() => appWindow.close()} />
  </HeaderUtilities>
</Header>

<Content>
  <!-- <div class="container search"> -->
  <Search />
  <!-- </div> -->
  <!-- <div class="container games"> -->
  <Games />
  <!-- </div> -->
</Content>

<style lang="scss">
  .icon {
    width: 18px;
    margin-right: 5px;
  }

  :global(#main-content) {
    display: grid;
    grid-template-columns:
      minmax(min-content, auto)
      minmax(min-content, max-content);
    align-items: stretch;
    column-gap: 1em;
  }

  :global(.bx--data-table-container:nth-child(1)) {
    // background: aqua !important;
  }

  :global(.bx--data-table-container:nth-child(2)) {
    width: fit-content;
  }
</style>

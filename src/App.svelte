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
    Grid,
    Row,
    Column,
  } from "carbon-components-svelte";
  import { Close16, Subtract16, Maximize16 } from "carbon-icons-svelte";
  import Search from "./lib/Search.svelte";

  export let theme: string;
  $: document.documentElement.setAttribute("theme", theme);

  export let name: string;

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
    <HeaderGlobalAction
      icon={Subtract16}
      on:click={() => {
        appWindow.minimize();
      }}
    />
    <HeaderGlobalAction
      icon={Maximize16}
      on:click={() => {
        appWindow.toggleMaximize();
      }}
    />
    <HeaderGlobalAction
      icon={Close16}
      on:click={() => {
        appWindow.close();
      }}
    />
  </HeaderUtilities>
</Header>

<Content>
  <Grid>
    <Row>
      <Column sm={11} md={11} lg={11}>
        <Search />
      </Column>
      <Column sm={1} md={1} lg={1}>
        Hello {name}!
      </Column>
    </Row>
  </Grid>
</Content>

<style lang="scss">
  .icon {
    width: 18px;
    margin-right: 5px;
  }
</style>

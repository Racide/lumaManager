<script lang="ts">
  import "./styles/globals.scss";
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
    SkeletonPlaceholder,
  } from "carbon-components-svelte";
  import {
    Close16 as Close,
    Subtract16 as Subtract,
    Maximize16 as Maximize,
    Minimize16 as Minimize,
  } from "carbon-icons-svelte";
  import Search from "./lib/Search.svelte";
  import Games from "./lib/Games.svelte";
  import Profiles from "./lib/Profiles.svelte";
  import { loadData } from "./lib/loadData";

  let windowMaximized: boolean,
    sizeIcon = Maximize;

  onMount(() => {
    appWindow.listen("tauri://resize", async () => {
      let t = await appWindow.isMaximized();
      if (windowMaximized != t) {
        windowMaximized = t;
        sizeIcon = windowMaximized ? Minimize : Maximize;
      }
    });
    appWindow.listen("tauri://focus", () => {
      document
        .querySelector<HTMLElement>(".bx--header")
        ?.classList.remove("window-blurred");
    });
    appWindow.listen("tauri://blur", () => {
      document
        .querySelector<HTMLElement>(".bx--header")
        ?.classList.add("window-blurred");
    });
    document
      .querySelectorAll(
        ".bx--header *:not(.bx--header__menu-bar *, .bx--header__global *)"
      )
      .forEach((el) => el.setAttribute("data-tauri-drag-region", ""));
  });

  document.addEventListener("contextmenu", (event) => event.preventDefault());
</script>

<Header>
  <svelte:fragment slot="platform">
    <img
      src="../src-tauri/icons/32x32.png"
      alt="logo"
      style:width="18px"
      style:margin-right="5px"
    />
    GreenLuma Manager
  </svelte:fragment>
  <HeaderNav>
    <HeaderNavMenu text="File">
      <HeaderNavItem text="Settings" />
      <HeaderNavItem text="About" />
      <HeaderNavItem text="Exit" />
    </HeaderNavMenu>
  </HeaderNav>
  <HeaderUtilities>
    <HeaderGlobalAction icon={Subtract} on:click={() => appWindow.minimize()} />
    <HeaderGlobalAction
      icon={sizeIcon}
      on:click={() => appWindow.toggleMaximize()}
    />
    <HeaderGlobalAction
      icon={Close}
      class="window-close"
      on:click={() => appWindow.close()}
    />
  </HeaderUtilities>
</Header>

<Content>
  {#await loadData()}
    <SkeletonPlaceholder />
    <SkeletonPlaceholder />
    <SkeletonPlaceholder />
  {:then}
    <Search />
    <Profiles />
    <Games />
  {/await}
</Content>

<style lang="scss">
</style>

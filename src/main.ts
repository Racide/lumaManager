// @ts-ignore
import App from "./App.svelte"
import "/src/styles/carbon-overrides.scss";

const app = new App({
  target: document.querySelector("#app"),
  props: {
    name: "world",
    theme: "g100"  // "white" | "g10" | "g80" | "g90" | "g100"
  }
})

export default app

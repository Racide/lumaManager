import App from "./App.svelte";

console.log(process.env.NODE_ENV);

const app = new App({
  target: document.querySelector("#app")!,
  props: {
  }
});

export default app;

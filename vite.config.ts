import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";
import sveltePreprocess from "svelte-preprocess";
import { options, preprocessOptions, sveltePreprocessOptions } from "./svelte.config.js";
import { optimizeCss } from "carbon-preprocess-svelte";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  return {
    plugins: [
      svelte({
        ...options,
        preprocess: [
          sveltePreprocess({
            ...sveltePreprocessOptions,
            sourceMap: (mode == "development"),
          }),
          ...preprocessOptions
        ],
      }),
      {
        ...optimizeCss(),
        apply: 'build'
      }
    ]
  }
})

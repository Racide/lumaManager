import sveltePreprocess from "svelte-preprocess";
import sass from "sass-embedded";
import { optimizeImports/* , optimizeCss */ } from "carbon-preprocess-svelte";

export const options = {
  // dev automatically set
  // https://github.com/sveltejs/vite-plugin-svelte/blob/main/docs/config.md#compileroptions
}

export const preprocessOptions = [
  optimizeImports()
]

// Consult https://github.com/sveltejs/svelte-preprocess
// for more information about preprocessors
export const sveltePreprocessOptions = {
  sourceMap: true, // overridden
  scss: {
    renderSync: true,
    implementation: sass
  }
};

export default {
  ...options,
  preprocess: [
    sveltePreprocess(sveltePreprocessOptions),
    ...preprocessOptions
  ]
};

// Import required dependencies
import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'
import { resolve } from 'path'

export default defineConfig({
  // Configure plugins
  plugins: [
    uni(), // Enable uni-app support
  ],
  // Configure path aliases
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src') // Map '@' to the src directory
    }
  },
  // Configure CSS preprocessing
  css: {
    preprocessorOptions: {
      scss: {
        // Automatically import uni.scss file in all components
        additionalData: `@import "@/uni.scss";`
      }
    }
  }
})

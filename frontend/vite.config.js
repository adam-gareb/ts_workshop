// import { fileURLToPath, URL } from 'url'

// import { defineConfig } from 'vite'
// import vue from '@vitejs/plugin-vue'

// // https://vitejs.dev/config/
// export default defineConfig({
//     plugins: [vue()],
//     resolve: {
//         alias: {
//             '@': fileURLToPath(new URL('./src', import.meta.url)),
//         },
//     },
// })

import { fileURLToPath, URL } from 'url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080', // byt till Jooby-serverns port
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, ''),
            },
        },
    },
})
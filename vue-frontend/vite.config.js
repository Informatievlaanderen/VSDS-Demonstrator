import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        cors: {
            origin: "*"
        },
        proxy: {
            '/in-rectangle': 'http://localhost:8084',
            '/triples': 'http://localhost:8084'
        }
    }
})

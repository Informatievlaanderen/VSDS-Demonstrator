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
            // string shorthand: http://localhost:5173/foo -> http://localhost:8084/foo
            '/in-rectangle': 'http://localhost:8084',
            '/triples': 'http://spring-boot-backend:8084'
        }
    }
})

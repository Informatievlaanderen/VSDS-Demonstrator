import {fileURLToPath, URL} from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig(({command, mode}) => {
    const env = loadEnv(mode, process.cwd(), '')
    console.log(env.SERVER_PORT)
    return {
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
            port: env.SERVER_PORT,
            proxy: {
                '/api': {
                    target: env.VITE_API_BASE_URL,
                },
                '/update': {
                    target: env.VITE_WS_BASE_URL,
                    ws: true
                },
                '/broker': {
                    target: env.VITE_WS_BASE_URL,
                    ws: true
                }
            },

        },
        build: {
            outDir: 'target/dist'
        }
    }
})

import {fileURLToPath, URL} from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig(({command, mode}) => {
    const env = loadEnv(mode, process.cwd(), '')
    return {
        define: {
            "process.env": env,
        },
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
                '/api': `https://demonstrator.smartdataspace.dev-vlaanderen.be`,
            },

        },
        build: {
            outDir: 'target/dist'
        }
    }
})

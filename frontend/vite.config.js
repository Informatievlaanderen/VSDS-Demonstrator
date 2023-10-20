import {fileURLToPath, URL} from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig(({command, mode}) => {
    const env = loadEnv(mode, process.cwd(), '')
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
            proxy: {
                '/api': env.VITE_API_BASE_URL,
            },

        },
        build: {
            outDir: 'target/dist'
        },
        define: {
            'import.meta.env.VITE_STREAMS': JSON.stringify({ streams: [{id: "gipod", fullname: "GIPOD", color: "#443939"}, {id: "verkeersmeting", fullname: "Verkeersmetingen Verkeerscentrum LDES", color: "#FFE615"}, {id: "bluebikes", fullname: "Blue Bikes", color: "#05c"}]})
        }
    }
})

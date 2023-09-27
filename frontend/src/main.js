import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import LeafletMap from "@/components/map/LeafletMap.vue";
import KnowledgeGraph from "@/components/graph/KnowledgeGraph.vue";
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/map', component: LeafletMap },
        { path: '/graph', component: KnowledgeGraph },
    ]
});

const app = createApp(App);
app.use(router);
app.mount('#app')

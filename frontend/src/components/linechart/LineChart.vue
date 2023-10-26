<template>
  <Line :data="data" :options="options"/>
</template>

<script lang="ts">
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'
import {Line} from 'vue-chartjs'
import {streams} from "../../../streams.json"

ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    Title,
    Tooltip,
    Legend
)

export default {
  name: 'App',
  components: {
    Line
  },
  props: ["stompClient"],
  data() {
    return {
      data: {
        labels: [],
        datasets: [],
        subscription: null,
      },
      options: {
        interaction: {
          intersect: false,
        },
        responsive: true,
        maintainAspectRatio: false

      }
    };
  },
  watch: {
    'stompClient.connected': {
      handler(newConnectedValue) {
        if (newConnectedValue) {
          this.subscribe();
        }
      },
      immediate: true
    }
  },
  mounted() {
    ChartJS.defaults.font = {
      family: "'Flanders Art Sans Regular', sans-serif",
      size: 12,
      style: "normal",
      weight: 400,
    }
  },
  unmounted() {
    this.unsubscribe();
  },
  methods: {
    subscribe() {
      this.subscription = this.stompClient.subscribe("/broker/linechart", (memberCounter) => {
        let linechart = JSON.parse(memberCounter.body)
        let datasetsOfLineChart = []
        for (const element of linechart.dataSetDtos) {
          let color = streams.find(stream => stream.id == element.name).color
          datasetsOfLineChart.push({
            borderColor: color,
            pointRadius: 0,
            tension: 0.3,
            label: 'Aantal datapunten ' + element.name,
            backgroundColor: color,
            data: element.values,
          })
        }
        this.data = {
          labels: linechart.labels,
          datasets: datasetsOfLineChart
        }
        this.options = {
          interaction: {
            intersect: false,
          },
          scales: {
            x: {
              ticks: {
                maxTicksLimit: 7,
                maxRotation: 0,
                minRotation: 0,
              },
              options: {
                text: "Tijd (dagen)"
              }
            }
          },
          responsive: true,
          maintainAspectRatio: false

        }
        // this.memberCounter = JSON.parse(memberCounter.body)
      });
    },
    unsubscribe() {
      this.subscription?.unsubscribe();
    }
  }
}
</script>
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
import Stomp from "webstomp-client";
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
  data() {
    return {
      data: {
        labels: [],
        datasets: [],
        stompClient: null,
      },
      //colorCodesFlanders: ["#FFED00", "blue", "#443939"],
      options: {
        interaction: {
          intersect: false,
        },
        responsive: true,
        maintainAspectRatio: false

      }
    };
  },
  mounted() {
    this.connect();
    ChartJS.defaults.font = {
      family: "'Flanders Art Sans Regular', sans-serif",
      size: 12,
      style: "normal",
      weight: 400,
    }

  },
  methods: {
    //websocket
    connect() {
      this.stompClient = new Stomp.client(`${import.meta.env.VITE_WS_BASE_URL}/update`, {debug: false});
      this.stompClient.connect(
          {},
          () => {
            this.stompClient.subscribe("/broker/linechart", (memberCounter) => {
              let linechart = JSON.parse(memberCounter.body)
              let datasetsOfLineChart = []
              for (const element of linechart.dataSetDtos) {
                let color = streams.find(stream => stream.id == element.name).color
                datasetsOfLineChart.push({
                  borderColor: color,
                  pointRadius: 0,
                  tension: 0.3,
                  label: 'Aantal members ' + element.name,
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
          error => {
            console.error(error);
            this.connect()
          }
      );
    },
    disconnect() {
      if (this.stompClient) {
        this.stompClient.disconnect();
      }
    }
  }
}
</script>
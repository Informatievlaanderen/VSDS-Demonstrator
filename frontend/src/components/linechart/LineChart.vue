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
      colorCodesFlanders: ["#FFED00", "#443939"],
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
              for (let i = 0; i < linechart.dataSetDtos.length; i++) {
                datasetsOfLineChart.push({
                  borderColor: this.colorCodesFlanders[i],
                  pointRadius: 0,
                  tension: 0.3,
                  label: 'Aantal members ' + linechart.dataSetDtos[i].name,
                  backgroundColor: this.colorCodesFlanders[i],
                  data: linechart.dataSetDtos[i].values,
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
            console.log(error);
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
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
  Legend
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
        datasets: []
      }, options: {
        interaction: {
          intersect: false,
        },
        scales: {
          yAxes: [{
            ticks: {
              min: 0,
              max: 300,
              stepSize: 100,
              reverse: false,
              beginAtZero: true
            }
          }]
        },
        responsive: true,
        maintainAspectRatio: false

      }
    };
  },
  mounted() {
    this.connect()
  },
  methods: {
    //websocket
    connect() {
      this.stompClient = new Stomp.client('ws://localhost:8084/update', {debug: false});
      this.stompClient.connect(
          {},
          () => {
            this.stompClient.subscribe("/broker/linechart", (memberCounter) => {
              let linechart = JSON.parse(memberCounter.body)
              this.data = {
                labels: linechart.labels,
                datasets: [
                  {
                    borderColor: "#FFED00",
                    pointRadius: 0,
                    tension: 0.3,
                    label: 'Gipod',
                    backgroundColor: '#FFED00',
                    data: linechart.values[0],
                  }
                ]
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
<template>
  <div>
    <svg id="knowledge-graph" class="linked-data-container" v-if="memberId" style="height:500px; min-width:600px; width: 100%"></svg>
    <div class="linked-data-container" v-else style="height:500px; min-width:600px">
      <img src="../../assets/svgs/Linked%20data.svg" alt="Linked data" class="linked-data-icon"/>
      <span>Linked data</span>
      <span>Klik op een punt op de kaart om de kennisgrafiek te laden</span>
    </div>
  </div>
</template>
<script>
import {useTriplesFetching} from "@/components/graph/composables/useTriplesFetching";

export default {
  props: {
    memberId: String | null,
  },
  watch: {
    memberId: function (newVal) {
      if(newVal) {
        useTriplesFetching(newVal)
      }
    }
  },
};
</script>
<style type="text/css">
.node {
  stroke: #fff;
  fill: #ddd;
  stroke-width: 1.5px;
}

.link {
  stroke: #999;
  stroke-opacity: 0.6;
  stroke-width: 1px;
}

marker {
  stroke: #999;
  fill: rgba(124, 240, 10, 0);
}

.node-text {
  font: 11px sans-serif;
  fill: black;
}

.link-text {
  font: 9px sans-serif;
  fill: grey;
}

.linked-data-container {
  border: 1px solid rgba(207, 213, 221, 0.50);
  border-left: 0;
}

.linked-data-icon {
  width: 21px;
  height: 25px;
  rotation: 225deg;
}

div.linked-data-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>
<template>
  <div>
    <svg id="knowledge-graph" class="linked-data-container" v-if="memberId"></svg>
    <div class="linked-data-container" v-else>
        <img src="../../assets/svgs/Linked%20data.svg" alt="Linked data" class="linked-data-icon"/>
        <h5 class="header5">Linked data</h5>
        <small class="small-regular">Klik op een punt op de kaart om de kennisgrafiek te laden</small>
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
      if (newVal) {
        useTriplesFetching(newVal)
      }
    }
  },
};
</script>
<style type="text/css" scoped>
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
  border-top: 0.5px solid #CFD5DD;
  border-right: 0.5px solid #CFD5DD;
  border-bottom: 0.5px solid #CFD5DD;
  border-left: 0;

  height: 350px;
  min-width: 600px;
  width: 100%;
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

.header5 {
  margin: 8px 0;
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
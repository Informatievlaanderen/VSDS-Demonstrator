<template>
  <div>
    <svg style="height:500px; width:600px"></svg>
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
      useTriplesFetching(newVal)
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

svg {
  border: 1px solid black;
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
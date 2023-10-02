<template>
  <div>
    <svg id="knowledge-graph" class="linked-data-container" v-if="memberId"></svg>
    <div class="linked-data-container" v-else>
      <img src="../../assets/svgs/Linked_data.svg" alt="Linked data" class="linked-data-icon"/>
      <h5 class="header header5 margin-vert-8">Linked data</h5>
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

<!--Styling that goes deeper then the component only, in this case is this styling used for the elements in the knowledge graph-->
<style>
.node {
  stroke: #FFE615;
  fill: #FFA405;
  stroke-width: 1px;
}

.link {
  stroke: #993F00;
  stroke-width: 0.66px;
}

.node-text {
  color: #333332;
  font-family: Flanders Art Sans Regular, sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 11px;
}

.link-text {
  color: #808080;
  font-family: Flanders Art Sans Regular, sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 9px;
}
</style>

<!--Styling that is related to the component only-->
<style scoped>
.linked-data-container {
  border-top: 0.5px solid #CFD5DD;
  border-right: 0.5px solid #CFD5DD;
  border-bottom: 0.5px solid #CFD5DD;
  border-left: 0;

  height: 450px;
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

.margin-vert-8 {
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
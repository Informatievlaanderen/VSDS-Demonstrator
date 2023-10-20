<template>
  <div>
    <div id="knowledge-graph" class="knowledge-graph-container" v-if="memberId">
      <ZoomButtons></ZoomButtons>
      <div id="knowledge-graph-loading" class="small-regular loading">Kennisgrafiek wordt ingeladen ...</div>
    </div>
    <div class="knowledge-graph-container" v-else>
      <img src="../../assets/svgs/Linked_data.svg" alt="Linked data" class="linked-data-icon"/>
      <h5 class="header header5 margin-vert-8">Linked data</h5>
      <small class="small-regular">Klik op een punt op de kaart om de kennisgrafiek te laden</small>
    </div>
  </div>
</template>
<script>
import {useTriplesFetching} from "@/components/graph/composables/useTriplesFetching";
import ZoomButtons from "@/components/graph/ZoomButtons.vue";

export default {
  components: {ZoomButtons},
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
}

#end polyline {
  fill: #993F00;
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
}

.link-text {
  color: #808080;
  font-family: Flanders Art Sans Regular, sans-serif;
  font-style: normal;
  font-weight: 400;
}

#tooltip {
  position: absolute;
  z-index: 9999999;
  background-color: #fff;
  padding: 9px;
  border-radius: 3px;
  white-space: nowrap;
}

#knowledge-graph > svg {
  width: 100%;
  height: 100%;
}
</style>

<!--Styling that is related to the component only-->
<style scoped>


#knowledge-graph  .loading {
  display: flex;
  width: 100%;
  height: 100%;
  justify-content: center;
  align-items: center;
  z-index: 100;
}

.knowledge-graph-container {
  border-top: 0.5px solid #CFD5DD;
  border-right: 0.5px solid #CFD5DD;
  border-bottom: 0.5px solid #CFD5DD;
  border-left: 0;

  height: 450px;
  min-width: 600px;
  width: 100%;

  position: relative;
}

.linked-data-icon {
  width: 21px;
  height: 25px;
  rotation: 225deg;
}

div.knowledge-graph-container {
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
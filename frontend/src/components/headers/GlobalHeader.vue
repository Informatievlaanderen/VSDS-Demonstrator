<template>
<div>
  <div class="global-header">
    <img src="../../assets/svgs/global-header/LOGO%20Vlaanderen%20NIV2.svg" alt="Vlaanderen">
    <img class="small-global-header-divider" src="../../assets/svgs/global-header/divider%20globalheader.svg" alt="divider">
    <span class="header-title header-font">Vlaamse Smart Data Space</span>
    <span class="spacer"></span>
    <div id="help-needed">
      <span class="header-font">Hulp nodig</span>
      <img class="question-menu-item" src="../../assets/svgs/global-header/question-mark.svg" alt="?">
    </div>
  </div>
  <hr class="divider">
</div>
</template>

<style scoped>
.global-header {
  display: flex;
  margin-left: 8px;
  align-items: center;
}

.small-global-header-divider {
  margin: 0 8px 0 4px;
}

#help-needed {
  display: flex;
  align-items: center;
  gap: 5px;
  background-image: url("src/assets/svgs/global-header/hulp nodig.svg");
  width: 144px;
  height: 44px;
}

#help-needed > span {
  margin-left: 25px;
}

.question-menu-item {
  width: 24px;
  height: 24px;
}


</style>
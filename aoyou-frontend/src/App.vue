<template>
    <BasicLayout ></BasicLayout>
</template>


<script setup lang="ts">

import BasicLayout from "./layouts/BasicLayout.vue";
import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";

var router = useRouter();
var route = useRoute();


</script>


<style scoped>

</style>

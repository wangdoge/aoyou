<template>
    <div id="teamPage">

        <van-search v-model="searchText" placeholder="搜索队伍" @search="onSearch" />
        <team-card-list :teamList="teamList"></team-card-list>
        <van-empty image="search" description="找不到亲亲想要的队伍捏"  v-if="teamList.length<1||!teamList"/>
        <van-button class="add-button" icon="plus" type="primary" @click="doAddTeam" ></van-button>
    </div>
</template>

<script setup lang="ts">

import {useRouter} from "vue-router";
import TeamCardList from "../components/TeamCardList.vue";
import {onMounted} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showToast} from "vant";
import {ref} from "vue";
import axios from "axios";

const router = useRouter();
const searchText=ref('');
const teamList=ref([]);
const doAddTeam = () =>{
    router.push({
        path:"team/add"
    })
}

onMounted(async ()=>{
    listTeam();
})

const listTeam= async (val='')=>{
    const res = await myAxios.get("/team/list",{
        params:{
            searchText: val
        }
    });
    if(res?.code===0){
        teamList.value=res.data;
    }else {
        showFailToast('加载失败,请刷新重试')
    }
}
const onSearch = (val) => {
    listTeam(val)
};


</script>

<style scoped>

</style>
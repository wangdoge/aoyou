<template>
    <div id="teamPage">
        <team-card-list :teamList="teamList"></team-card-list>
        <van-button type="primary" @click="doJoinTeam">创建队伍</van-button>
        <van-empty image="search" description="亲亲还没有创建小队哦"  v-if="teamList.length<1||!teamList"/>
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
const doJoinTeam = () =>{
    router.push({
        path:"../../team/add"
    })
}

onMounted(async ()=>{
    listTeam();
})

const listTeam= async (val='')=>{
    const res = await myAxios.get("/team/list/my/create",{
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
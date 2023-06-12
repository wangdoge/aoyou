<template>
    <van-nav-bar
            :title=title
            left-text=""
            left-arrow
            @click-left="onClickLeft"
            @click-right="onClickRight">
        <template #right>
            <van-icon name="search" size="18" />
        </template>
    </van-nav-bar>
    <div id="content">
        <router-view/>
    </div>


    <van-tabbar v-model="active" @change="onChange">
        <van-tabbar-item to="/" icon="home-o" name="index">主页</van-tabbar-item>
        <van-tabbar-item to="/team" icon="search" name="team">队伍</van-tabbar-item>
        <van-tabbar-item to="/user" icon="friends-o" name="user">个人</van-tabbar-item>
    </van-tabbar>

</template>


<script setup lang="ts">
import { ref } from 'vue';
import {showToast, Toast} from "vant";

import {useRoute, useRouter} from "vue-router";
import routes from "../config/route";


const title=ref(DEFAULT_TITLE);
const router=useRouter();
const route=useRoute();
const DEFAULT_TITLE='遨游';

router.beforeEach((to,from)=>{

    const toPath=to.path;
    const route= routes.find((route)=>{
        return toPath == route.path
    })
    console.log(route);
    if(!route?.title){
        title.value= DEFAULT_TITLE;
    }else {
        title.value= route.title;
    }

})

const onClickLeft = () => {
    router.back()
};
const onClickRight = () => {
    router.push('/search')
};

const active = ref('index');
const onChange = (index) => showToast(`标签 ${index}`)
</script>


<style scoped>
#content{
    padding-bottom: 55px;
}

</style>

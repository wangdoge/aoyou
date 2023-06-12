<script setup lang="ts">
import { ref } from 'vue';
import { Toast } from 'vant';
import {useRouter} from "vue-router";

const router=useRouter()

const searchText = ref('');

const onCancel = () =>{
    searchText.value==='';
}

const activeIds = ref([]);
const activeIndex = ref(0);
const tagList = [
    {
        text: '性别',
        children: [
            { text: '男', id: '男' },
            { text: '女', id: '女' },
        ],
    },
    // {
    //     text: '江苏',
    //     children: [
    //         { text: '南京', id: 4 },
    //         { text: '无锡', id: 5 },
    //         { text: '徐州', id: 6 },
    //     ],
    // },
    // { text: '福建', disabled: true },
];

const close=(tag)=>{
    activeIds.value=activeIds.value.filter(item =>{
        return item!==tag;
    })
}

//执行搜索
const doSearchResult= ()=>{
    router.push({
        path:'user/list',
        query:{
            tags:activeIds.value
        }
    })
}
</script>

<template>

    <van-divider content-position="left">已选标签</van-divider>
    <van-row gutter="20">
        <van-col span="3" v-for="tag in activeIds" >
            <van-tag closeable size="medium" type="primary" @close="close(tag) " round=true >
                {{tag}}
            </van-tag>
        </van-col>
    </van-row>

    <van-divider content-position="left">请选择标签</van-divider>
    <van-tree-select
        v-model:active-id="activeIds"
        v-model:main-active-index="activeIndex"
        :items="tagList"
    />
    <div style="padding: 20px">
        <van-button type="primary" size="large" round=true  @click="doSearchResult" style=" padding: 20px">提交</van-button>
    </div>




</template>

<style scoped>

</style>
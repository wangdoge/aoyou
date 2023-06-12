<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {getCurrentUser} from "../services/user.ts";


const user=ref()

onMounted(async ()=>{
    user.value=await getCurrentUser();
})

// const user = {
//     id: 1,
//     planetCode: 2,
//     username : '王',
//     avatarUrl : 'https://ts1.cn.mm.bing.net/th/id/R-C.8c3d9db706903ea7edb103fe6501fffb?rik=frgn0IOZRdbHvg&riu=http%3a%2f%2fa2.att.hudong.com%2f55%2f19%2f01300000287245123658196997577.jpg&ehk=DLdafyxVZTESIczH2QuRQzIIx0waAA5sNOngLJjwUvo%3d&risl=&pid=ImgRaw&r=0',
//     userAccount: '123456',
//     gender: '男',
//     phone: '15355400184',
//     email: '2541277577@qq.com',
//     userStatus: '1',
//     createTime: new Date()
// };
const router=useRouter();


const toEdit=(editKey: string,editName:string,currentValue: string) => {
    router.push({
        path: '/user/edit',
        query: {
            editKey,
            editName,
            currentValue,
        }
    })
}


onMounted(async ()=>{
    user.value=await getCurrentUser();
    user.value.tags=JSON.parse(user.value.tags);
})
</script>



<template>
    <div  id="userHead">
        <template v-if="user">
            <van-image
                style="margin-top: 20px;margin-bottom: 15px"
                round
                width="7rem"
                height="7rem"
                :src="user.avatarUrl"
            />
            <div id="nameText">
                <view >{{user.username}}</view>
            </div>
            <div >
                <van-tag style="margin-right: 5px" v-for="tag in user.tags" color="#F2E6CE" text-color="#ad0000">{{tag}}</van-tag>
            </div>

        </template>
    </div>
    <van-grid clickable :column-num="3">
        <van-grid-item icon="home-o" text="修改信息" to="/user/update" />
        <van-grid-item icon="search" text="我的队伍" to="/user/team/create" />
        <van-grid-item icon="search" text="加入的队伍" to="/user/team/join" />
    </van-grid>
</template>


<style scoped>
#nameText{
    color: #F2E6CE;
    font-size: 21px;
    text-align: center;
}
#userHead{
    background-color: #638c71;
    text-align: center;
    padding: 20px;
}
</style>
<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {getCurrentUser} from "../services/user.ts";
import {showSuccessToast} from "vant";


const user=ref({})

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

const onsubmit=async ()=>{
    const res=await myAxios.post("/user/logout");
    console.log(res);
    if(res.code===0){
        showSuccessToast("退出成功");
        router.push("/user/login/phone");
    }
}
</script>


<template>
    <template v-if="user">
        <van-image
            style="margin-left: 100px"
            round
            width="10rem"
            height="10rem"
            :src="user.avatarUrl"
        />
        <van-cell title="用户名" is-link to="/user/edit" arrow-direction="down" :value="user.username" @click="toEdit('username','用户名 ',user.username)"/>
        <van-cell title="账号" is-link :value="user.userAccount"/>
<!--        <van-cell title="编号" is-link to="/user/edit" arrow-direction="down" :value="user.planetCode"/>-->
        <van-cell title="性别" is-link to="/user/edit" arrow-direction="down" :value="user.gender" @click="toEdit('gender','性别 ',user.gender)"/>
        <van-cell title="电话" is-link to="/user/edit" arrow-direction="down"  :value="user.phone" @click="toEdit('phone','电话 ',user.phone)"/>
        <van-cell title="注册时间" is-link :value="user.createTime" />
        <van-cell title="邮箱" is-link arrow-direction="down" :value="user.email" @click="toEdit('email','邮箱 ',user.email)"/>
        <van-button type="primary" @click="onsubmit">退出登录</van-button>
    </template>
</template>


<style scoped>

</style>
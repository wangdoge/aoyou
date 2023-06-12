<script setup lang="ts">

import {TeamType} from "../models/team";
import {teamStatusEnums} from "../constants/team.ts";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../services/user.ts";
import {getCurrentUserState} from "../states/user.ts";

import {ref,onMounted} from "vue";
import {useRouter} from "vue-router";

interface TeamCardListProps{
    teamList:TeamType[];
}

const props=withDefaults(defineProps<TeamCardListProps>(),{
    teamList:[] as TeamType[],
})

const currentUser=ref('');

onMounted(async ()=>{
    currentUser.value=await getCurrentUser()
})

const doJoinTeam=async ()=>{
    if (!joinTeamId.value){
        return;
    }
    const res= await myAxios.post('/team/join',{
        teamId:joinTeamId.value,
        password:password.value,
    })
    console.log('res',res);
    if(res?.code===0){
        showSuccessToast('加入成功')
        location.reload()
    }else {
        showFailToast(res.description?res.description:'加入失败')
    }
}

const showPasswordDialog=ref(false);
const password=ref()
const joinTeamId=ref()
const router=useRouter();
/**
 * 跳转至更新队伍页面
 * @param id
 */
const doUpdateTeam=async (id:number)=>{
    router.push({
        path:'/team/update',
        query:{
            id,
        }
    })
}

const doQuitTeam=async (id:number)=>{
    const res= await myAxios.post('/team/quit',{
        teamId:id,
    })
    console.log('res',res);
    if(res?.code===0){
        showSuccessToast('退出队伍成功');
        location.reload();
    }else {
        showFailToast(res.description?res.description:'退出队伍失败')
    }
}


const doDeleteTeam=async (id:number)=>{
    const res= await myAxios.post('team/delete',{
        teamId:id,
    })
    console.log('res',res);
    if(res?.code===0){
        showSuccessToast('解散队伍成功');
        location.reload();
    }else {
        showFailToast(res.description?res.description:'解散队伍失败')
    }
}
const preJoinTeam= (team:TeamType)=>{
        joinTeamId.value=team.id
        if(team.status===0){
            doJoinTeam(team.id)
        }else{
            showPasswordDialog.value=true;
        }
    }

const doJoinCancel=()=>{
    joinTeamId.value=0;
    password.value='';
}

</script>

<template>
    <div id="teamCardList">
        <a href="/team/info">
            <van-card
                v-for="team in props.teamList"
                :desc="team.description"
                :title="team.name"
                thumb="https://image3.buy.ccb.com/merchant/201903/1159177923/1587015061879_4.jpg"
            >
                <template #tags>
                    <van-tag plain type="danger" style="margin-right:7px;margin-top:5px  ">
                        {{teamStatusEnums[team.status]}}
                    </van-tag>
                </template>
                <template #bottom>
                    <div>
                        {{`队伍人数：${team.hasJoinNum}/${team.maxNum }`}}
                    </div>
                    <div>
                        {{'过期时间：'+team.expireTime}}
                    </div>
                    <van-button v-if="team.userId===currentUser?.id" id="bottom" size="small" type="danger" plain @click="doDeleteTeam(team.id)">解散队伍</van-button>
                    <van-button v-if="team.hasJoin===false" id="bottom" size="small" type="primary" plain @click="preJoinTeam(team)">加入队伍</van-button>
                    <van-button v-if="team.userId===currentUser?.id" id="bottom" size="small" type="primary" plain @click="doUpdateTeam(team.id)">更新队伍</van-button>
                    <van-button v-if="team.hasJoin===true" id="bottom" size="small" type="primary" plain @click="doQuitTeam(team.id)">退出队伍</van-button>

                </template>

                <template #footer>

                </template>
            </van-card>
        </a>
        <van-dialog v-model:show="showPasswordDialog" title="请输入密码" show-cancel-button @confirm="doJoinTeam" @cancel="doJoinCancel">
            <van-field v-model="password"  placeholder="请输入房间密码" />
        </van-dialog>
    </div>
</template>

<style scoped>
.van-card__thumb{

}
#teamCardList :deep(.van-image__img){
    height: 128px;
}
#teamCardList :deep(.van-card__title){
    line-height:24px;
    height: 25px;

    font-size: 17px;
}
#bottom{
    float: right;
    margin-right: 5px;
    margin-top: 5px;
    /*height: 30px;*/
    //margin-left: 170px;
    //margin-top: 15px;
}
</style>
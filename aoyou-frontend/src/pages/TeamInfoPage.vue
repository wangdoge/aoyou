<script setup lang="ts">
import {useRoute, useRouter} from "vue-router";
  import {onMounted, ref} from "vue";
  import myAxios from "../plugins/myAxios.ts"

  import {getCurrentUser} from "../services/user.ts";
  import {showFailToast, showSuccessToast} from "vant";
  import {TeamType} from "../models/team";


  const route=useRoute()
  const router=useRouter()
  const id=route.query.id;


  const teamIn=ref();
  const userList=ref([]);
  const currentUser=ref('');
  const joinTeamId=ref();
  const showPasswordDialog=ref(false);
  const password=ref()
  const showOption=ref(false)


  onMounted(async() =>{
    /**
     * 获取队伍信息
     */
      const teamInfo  =await myAxios.get('team/get', {
          params: {
              id: id
          },
      })
          .then(function (response) {
              console.log(response);
              teamIn.value=response?.data;
              return response?.data
          })
          .catch(function (error) {
              console.log(error);
          })
      if(teamInfo){
        teamIn.value=teamInfo;
        userList.value=teamIn.value.userList;
      }
      userList.value.forEach(user=>{
      if(user.tags){
        user.tags=JSON.parse(user.tags);
      }
    })

    currentUser.value=await getCurrentUser()

  })

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
      router.push("/team");
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
      router.push("/team");
    }else {
      showFailToast(res.description?res.description:'解散队伍失败')
    }
  }

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

  const preJoinTeam= (team:TeamType)=>{
    joinTeamId.value=team.id
    if(team.status===0){
      doJoinTeam(team.id)
    }else{
      showPasswordDialog.value=true;
    }
  }

  const onClickChange=()=>{
    showOption.value=true;
  }

</script>

<template>

    <template v-if="teamIn">
      <div  id="teamHead">
      <van-image
          style="margin-top: 20px;margin-bottom: 15px"
          round
          width="8rem"
          height="8rem"
          :src="teamIn.teamAvatar"
      />
        <div id="nameText">
          <view >{{teamIn.name}}</view>
        </div>
      </div>
      <div>
        <van-divider>
          {{`队伍成员：${teamIn.userCount}/${teamIn.maxNum}`}}
        </van-divider>
      </div>
    </template>

  <div id="userList">
    <van-card
        id="userCard"
        v-for="user in userList"
        :desc="user.profile"
        :title="user.username"
        :thumb="user.avatarUrl"
        style="--van-card-title-size:15px"
    >
      <template #tags>
        <van-tag plain type="danger" v-for="tag in user.tags" style="margin-right:7px;margin-top:5px  ">
          {{tag}}
        </van-tag>
      </template>
    </van-card>
  </div>

  <div>
    <van-divider>
       队伍信息
    </van-divider>
  </div>
  <template v-if="teamIn">
    <van-dialog v-model:show="showPasswordDialog" title="请输入密码" show-cancel-button @confirm="doJoinTeam" @cancel="doJoinCancel">
      <van-field v-model="password"  placeholder="请输入房间密码" />
    </van-dialog>

    <van-tabbar >
      <van-tabbar-item v-if="teamIn.userId===currentUser?.id && teamIn.hasJoin===true" @click="onClickChange" icon="home-o" name="index">队伍操作</van-tabbar-item>
      <van-tabbar-item v-if="teamIn.hasJoin!==true" @click="preJoinTeam(teamIn)" icon="home-o" name="index">加入队伍</van-tabbar-item>
      <van-tabbar-item v-if="teamIn.userId!==currentUser?.id && teamIn.hasJoin===true" @click="doQuitTeam(teamIn.id)" icon="home-o" name="index">退出队伍</van-tabbar-item>
      <van-tabbar-item v-if="teamIn.userId!==currentUser?.id && teamIn.hasJoin===true" @click="onClickChange" icon="home-o" name="index">退出队伍</van-tabbar-item>
      <van-tabbar-item icon="friends-o" name="user">小队聊天室</van-tabbar-item>
    </van-tabbar>

    <van-popup
        v-model:show="showOption"
        position="bottom"
        :style="{ height: '25%' }"
    >
      <div style="text-align: center; margin-top:10px"  >队伍操作</div>
      <van-button class="quit-button"  type="primary" @click="doQuitTeam(teamIn.id)" >
        <van-icon name="revoke" size="30px"/>
      </van-button>
      <van-button class="update-button"  type="primary" @click="doUpdateTeam(teamIn.id)" >
        <van-icon name="edit" size="30px"/>
      </van-button>
      <van-button class="delete-button" type="danger" @click="doDeleteTeam(teamIn.id)" >
        <van-icon name="cross" size="30px"/>
      </van-button>
    </van-popup>
  </template>
</template>

<style scoped>
#nameText{
  color: #F2E6CE;
  font-size: 25px;
  text-align: center;
  float: right;
  margin-top: 60px;
  margin-right: 50px;
  margin-left: 10px;
}
#teamHead{
  background-color: #638c71;
  text-align: center;
  padding: 20px;
}
#userCard :deep(.element.style){
  width: 8rem;
  height: 8rem;
  margin-top: 20px;
  margin-bottom: 15px;

}
</style>
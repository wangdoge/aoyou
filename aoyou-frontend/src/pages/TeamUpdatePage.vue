<template>
    <div id="teamAddPage">
        <van-form @submit="onSubmit">
            <van-cell-group inset >
                <van-field
                    v-model="addTeamData.name"
                    name="name"
                    label="队伍名"
                    placeholder="请输入队伍名"
                    :rules="[{ required: true, message: '请填写队伍名' }]"
                />


                <van-field
                    style="margin-bottom: 10px"
                    is-link
                    readonly
                    name="dateTimePicker"
                    label="过期日期"
                    :placeholder="addTeamData.expireTime ?? '请选择过期时间'"
                    @click="showPicker = true"
                />
                <van-popup v-model:show="showPicker" position="bottom">
                    <van-date-picker @confirm="onConfirm" @cancel="showPicker = false" :min-date="new Date(new Date().setDate(new Date().getDate() + 1))"/>
                </van-popup>

                <van-field name="stepper" label="最大人数" style="margin-bottom: 10px">
                    <template #input>
                        <van-stepper v-model="addTeamData.maxNum" max="10" />
                    </template>
                </van-field>

                <van-field name="radio" label="单选框" style="margin-bottom: 10px">
                    <template #input>
                        <van-radio-group v-model="addTeamData.status" direction="horizontal" >
                            <van-radio name="0">公开</van-radio>
                            <van-radio name="1">私有</van-radio>
                            <van-radio name="2">加密</van-radio>
                        </van-radio-group>
                    </template>
                </van-field>

                <van-field
                    style="margin-bottom: 10px"
                    v-if="addTeamData.status==='2'"
                    v-model="addTeamData.password"
                    type="password"
                    name="密码"
                    label="密码"
                    placeholder="请输入队伍密码"
                    :rules="[{ required: true, message: '请填写密码' }]"
                />

                <van-field
                    style="margin-bottom: 10px"
                    v-model="addTeamData.description"
                    rows="2"
                    autosize
                    label="队伍描述"
                    type="textarea"
                    placeholder="请输入队伍描述"
                />
            </van-cell-group>
            <div style="margin: 16px;">
                <van-button round block type="primary" native-type="submit">
                    添加
                </van-button>
            </div>
        </van-form>
    </div>
</template>

<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import {ref,onMounted} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";
import {TeamType} from "../models/team";

const router=useRouter();

const route=useRoute();

const id=route.query.id;

onMounted(async ()=>{
    if (id<=0){
        showFailToast('加载队伍信息失败');
    }
    const res =await myAxios.get("/team/get",{
        params:{
            id
        }
    });
    if(res?.code===0){
        addTeamData.value=res.data
    }else {
        showFailToast('加载队伍信息失败');
    }
})


const addTeamData=ref({})

const onSubmit = async () => {
    const postData={
        ...addTeamData.value,
        status: Number(addTeamData.value.status)
    }
    const res = await myAxios.post("/team/update",addTeamData.value);
    if(res?.code===0){
        showSuccessToast('修改成功');
        router.push({
            path:'team',
            replace:true,
        });
        router.back();
    }else {
        showFailToast('修改失败');
    }
};


const showPicker = ref(false);
const onConfirm = ({ selectedValues }) => {
    addTeamData.value.expireTime = selectedValues.join('/');
    showPicker.value = false;
};
</script>

<style scoped>

</style>
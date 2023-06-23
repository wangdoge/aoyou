<template>
    <div id="teamAddPage">
        <van-form @submit="onSubmit">
            <van-cell-group inset >
                <van-field
                    style="margin-bottom: 10px"
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

import {useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/myAxios.ts";
import {showFailToast, showSuccessToast} from "vant";

const router=useRouter();

const initFormData={
    "name": "",
    "description": "",
    "expireTime": null,
    "maxNum":5,
    "password": "",
    "status":'0',
    "userId":0
}

const addTeamData=ref({...initFormData})

const onSubmit = async () => {
    const postData={
        ...addTeamData.value,
        status: Number(addTeamData.value.status)
    }
    const res = await myAxios.post("/team/add",addTeamData.value);
    if(res?.code===0){
        showSuccessToast('添加成功');
        router.push({
            path:'/team',
            replace:true,
        });
    }else {
        showFailToast('添加失败');
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
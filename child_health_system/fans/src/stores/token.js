import { defineStore } from "pinia";
import { ref } from 'vue';

/*
    defineStore参数描述：
        第一个参数：给状态起名，具有唯一性
        第二个参数：函数，可以把定义该状态中拥有的内容

    defineStore返回值描述：
        返回的是一个函数，将来可以调用该函数，得到第二个参数中返回的内容
*/
export const useTokenStore = defineStore('token', () => {
    //1.定义描述token
    const token = ref('')
    const username = ref('')
    const roleId = ref(null)
    const roleName = ref('')

    //2.定义修改token的方法
    const setToken = (newToken) => {
        token.value = newToken
    }

    const setUsername = (name) => {
        username.value = name
    }

    const setRole = (id, name) => {
        roleId.value = id
        roleName.value = name
    }

    //3.定义移除token的方法
    const removeToken = () => {
        token.value = ''
        username.value = ''
        roleId.value = null
        roleName.value = ''
    }
    return {
        token, 
        username,
        roleId,
        roleName,
        setToken,
        setUsername,
        setRole,
        removeToken
    }
},
    //参数持久化
    {
        persist: true
    }
)
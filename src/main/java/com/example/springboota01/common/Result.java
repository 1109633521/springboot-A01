package com.example.springboota01.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口统一返回包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String code; //状态码
    private String msg;
    private Object data;

    public static Result success(){
        return new Result(Constants.CODE_200,"",null);
    }

    public static Result success(Object data){
        return new Result(Constants.CODE_200,"",data);
    }

    public static Result success(String code,String msg){
        return new Result(code,msg,null);
    }

    public static Result success(String code,String msg,Object data){
        return new Result(code,msg,data);
    }

    public static Result error(String code,String msg){
        return new Result(code,msg,null);
    }

    public static Result loginError(){
        return new Result(Constants.CODE_500,"用户名或密码错误",null);
    }

    public static Result registerError(){
        return new Result(Constants.CODE_500,"该用户名已被注册",null);
    }
}

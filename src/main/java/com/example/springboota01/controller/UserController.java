package com.example.springboota01.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.UserDTO;
import com.example.springboota01.controller.dto.pageDTO.UserPageDTO;
import com.example.springboota01.mapper.UserMapper;
import com.example.springboota01.service.impl.UserServiceImpl;
import io.swagger.annotations.*;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboota01.entity.User;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author luoxu
 * @since 2022-03-13
 */
@Api(tags="user类控制器：用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    @ApiResponses({
            @ApiResponse(code = 200,message = "登录成功"),
            @ApiResponse(code = 400,message = "系统错误（传递空字符串之类的）"),
            @ApiResponse(code = 500,message = "用户名或密码错误")
    })
    public Result login(@RequestBody UserDTO userDTO){
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "系统错误!");
        }
        UserDTO one = userService.login(userDTO);
        if(one!=null) {

            return Result.success(one);
        }else{
            return Result.loginError();
        }
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    @ApiResponses({
            @ApiResponse(code = 200,message = "注册成功"),
            @ApiResponse(code = 400,message = "系统错误（传递空字符串之类的）"),
            @ApiResponse(code = 500,message = "用户名已被注册")
    })
    public Result register(@RequestBody User user){
        if(StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())){
            return Result.error(Constants.CODE_400, "系统错误!");
        }
        User one = userService.register(user);
        if(one!=null){
            return Result.success(Constants.CODE_200,"注册成功");
        }else{
            return Result.registerError();
        }
    }

    @PostMapping("/save")
    @ApiOperation("添加用户")
    @ApiResponses({
            @ApiResponse(code = 200,message = "添加成功"),
            @ApiResponse(code = 400,message = "系统错误：用户名、密码、用户名称不能为空"),
            @ApiResponse(code = 500,message = "用户名已存在")
    })
    public Result save(@RequestBody User user){
        if(StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())
                || StrUtil.isBlank(user.getNickname())){
            return Result.error(Constants.CODE_400, "系统错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        if(userService.getOne(queryWrapper)!=null){
            return Result.error(Constants.CODE_500,"用户名已存在");
        }
        userService.save(user);
        return Result.success(Constants.CODE_200,"添加成功");
    }

    @PostMapping("/update")
    @ApiOperation("修改用户信息,以用户名为索引，只能修改密码、名称、电话、性别、邮箱，时间值放空就行，每次操作后台更改。不需要修改的值允许传递空值，空值不会进行修改")
    public Result update(@RequestBody User user){
        if(StrUtil.isBlank(user.getUsername())){
            return Result.error(Constants.CODE_400, "系统错误");
        }
        user.setChangeTime(LocalDateTime.now());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        User one = userService.getOne(queryWrapper);
        if(one==null){
            return Result.error(Constants.CODE_500,"用户名不存在");
        }
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",one.getId());
        if(user.getNickname()!=null) {one.setNickname(user.getNickname());}
        if(user.getAddress()!=null) {one.setAddress(user.getAddress());}
        if(user.getEmail()!=null) {one.setEmail(user.getEmail());}
        if(user.getPhone()!=null) {one.setPhone(user.getPhone());}
        if(user.getSex()!=null) {one.setSex(user.getSex());}
        one.setChangeTime(LocalDateTime.now());
        userService.updateById(one);
        return Result.success(Constants.CODE_200,"修改成功");
    }

    /*//查询所有数据
    @GetMapping
    public List<User> findAll(){
        //List<User> all = userMapper.findAll();
        List<User> all = userService.list();
        return all;
    }*/

    @ApiOperation("依据ID删除数据，实现单个删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "单个id",required = true)
    })
    @GetMapping("/del/single")
    public Boolean delete(@RequestParam Integer id){
        return userService.removeById(id);
    }

    @PostMapping("/del/batch")
    @ApiOperation("依据ID集合删除数据，实现批量删除的功能")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids",value = "id值的集合",dataType = "Integer",required = true)
    })
    public boolean deleteBatch(@RequestBody List<Integer> ids){
        return userService.removeByIds(ids);
    }

    //分页查询 + 模糊查询
    //@RequestParam接收pageNum=1 & pageSize=10
    @PostMapping("/page")
    @ApiOperation("获取页面尺寸和页码进行分页查询（可选带入用户名称进行模糊查询）")
    @ApiResponses({
            @ApiResponse(code = 200,message = "查询成功"),
            @ApiResponse(code = 400,message = "系统错误:页码和页面尺寸要大于0"),
            @ApiResponse(code = 500,message = "查询不到数据")
    })
    public Result findPage(@RequestBody UserPageDTO uPage){
        if(uPage.getPageNum()<1 || uPage.getPageSize()<1){
            return Result.error(Constants.CODE_400, "系统错误");
        }
        Integer pageNum = (uPage.getPageNum() - 1)* uPage.getPageSize();
        String nickname = "%" + uPage.getNickname() +"%";
        List<User> data = userMapper.selectPage(pageNum,uPage.getPageSize(),nickname);
        Integer total = userMapper.selectTotal(nickname);
        if(total == 0){
            return Result.error(Constants.CODE_500,"查询不到数据");
        }
        Map<String,Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return Result.success(Constants.CODE_200,"查询成功",res);
    }

    /*@PostMapping("/page")
    @ApiOperation("获取页面尺寸和页码进行分页查询（可选带入用户名称进行模糊查询）")
    public IPage<User> findPage(@RequestBody UserPageDTO uPage) {
        IPage<User> page = new Page<>(uPage.getPageNum(),uPage.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("nickname",uPage.getNickname());
        //queryWrapper.like("username",username);  多条模糊查询，mp自动加 and
        return userService.page(page,queryWrapper);
    }*/
}



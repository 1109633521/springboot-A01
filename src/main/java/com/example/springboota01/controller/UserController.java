package com.example.springboota01.controller;


import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.UserDTO;
import com.example.springboota01.controller.dto.pageDTO.UserPageDTO;
import com.example.springboota01.mapper.UserMapper;
import com.example.springboota01.service.impl.UserServiceImpl;
import io.swagger.annotations.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboota01.entity.User;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
@Transactional
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

    @GetMapping("/getNowUser")
    @ApiOperation("根据token获取当前用户信息")
    @ApiResponses({
            @ApiResponse(code = 200,message = "获取成功"),
            @ApiResponse(code = 401,message = "token验证有误"),
            @ApiResponse(code = 500,message = "用户不存在")
    })
    public Result getNowUser(HttpServletRequest request){
        String token = request.getHeader("token");
        String userId;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            return Result.error(Constants.CODE_401,"token验证失败，请重新登录");
        }
        // 根据token中的userid查询数据库
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(Constants.CODE_500,"用户不存在，请重新登录");
        }
        return Result.success(Constants.CODE_200,"获取成功",user);
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
        User one = userService.getOne(queryWrapper);
        if(one==null){
            return Result.error(Constants.CODE_500,"用户名不存在");
        }
        UpdateWrapper<User> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("id",one.getId());
        if(user.getPassword()!=null){ one.setPassword(user.getPassword());}
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

    @GetMapping("/getMsg")
    public Result getMsg(@RequestParam String str){
        System.out.println();
        System.out.println(str);
        System.out.println();
        return Result.success(Constants.CODE_200,"嗨害嗨",str);
    }

    @PostMapping("/postTest")
    public Result postTest(@RequestBody Flask flask){
        Set<String> keySet = flask.getReverse().keySet();
        Iterator<String> im = keySet.iterator();
        while(im.hasNext()){
            String key = im.next();
            //通过key获取对应的value
            List<String> list = flask.getReverse().get(key);
            for (String str : list) {
                System.out.println(str);
            }
        }
        return Result.success(Constants.CODE_200,"嗨害嗨");
    }
}

@Data
class Flask{
    private Map<String,List<String>> overspeed;
    private Map<String,List<String>> car_break_red;
    private Map<String,List<String>> online;
    private Map<String,List<String>> pedestrian_red;
    private Map<String,List<String>> reverse;
}



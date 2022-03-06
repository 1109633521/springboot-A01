package com.example.springboota01.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboota01.common.Constants;
import com.example.springboota01.common.Result;
import com.example.springboota01.controller.dto.UserDTO;
import com.example.springboota01.entity.User;
import com.example.springboota01.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO){
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO dto = userService.login(userDTO);
        if(dto!=null) {
            return Result.success(dto);
        }else{
            return Result.error();
        }
    }
    //新增或修改数据
    @PostMapping("/save")
    public Boolean save(@RequestBody User user){
        return userService.saveOrUpdate(user);
    }

    //查询所有数据
    @GetMapping
    public List<User> findAll(){
        //List<User> all = userMapper.findAll();
        List<User> all = userService.list();
        return all;
    }

    //依据ID删除数据
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id){
        return userService.removeById(id);
    }

    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids){
        return userService.removeByIds(ids);
    }

    //分页查询 + 模糊查询
    //@RequestParam接收pageNum=1 & pageSize=10
    /*@GetMapping("/page")
    public Map<String,Object> findPage(@RequestParam Integer pageNum,
                                       @RequestParam Integer pageSize,
                                       @RequestParam String nickname){
        pageNum = (pageNum - 1)* pageSize;
        nickname = "%" + nickname +"%";
        List<User> data = userMapper.selectPage(pageNum,pageSize,nickname);
        Integer total = userMapper.selectTotal(nickname);
        Map<String,Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }*/

    @GetMapping("/page")
    public IPage<User> findPage(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize,
                                @RequestParam String nickname) {
        IPage<User> page = new Page<>(pageNum,pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("nickname",nickname);
        //queryWrapper.like("username",username);  多条模糊查询，mp自动加 and
        return userService.page(page,queryWrapper);
    }

}

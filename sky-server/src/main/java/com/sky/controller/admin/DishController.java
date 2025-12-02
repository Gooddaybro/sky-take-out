package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {

        log.info("新增菜品{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> Page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 菜品的批量删除
     * 简单的三层架构
     * 但是在业务逻辑层要给他一个查询数据库的操作
     * 而且他有删除的一些条件
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品的批量删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品的批量删除{}", ids);
        dishService.Batchdelete(ids);
        return Result.success();
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/id")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> findById(@RequestParam Long id) {
        log.info("根据id查找菜品{}", id);
        DishVO dishVO = dishService.findById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品数据
     * @param dishDTO
     * @return
     */
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品数据{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

}

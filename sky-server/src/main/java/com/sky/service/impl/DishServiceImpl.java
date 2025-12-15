package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    /**
     * 添加菜品和口味
     *
     * @param dishDTO
     */
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入一条菜品
        dishMapper.insert(dish);
        //获取插入的Id
        Long id = dish.getId();
        //向菜品表添加多种口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishflavor -> {
                dishflavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页菜品查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     * 1.如果是在售的时候就不可以删除，首先给他找到数据库里的数据对他进行遍历
     * 如果说遍历到了是在售的就抛出异常
     * 2.如果这个菜品被某套餐关联了也不可以删除
     * 还是得去数据库中去找套餐相关联的这个菜品，如果有那就得抛出异常
     * 得新建一个setmeal_dish的mapper
     * 3.删除菜品 通过循环一个一个的删掉
     * 4.删除菜品对应的口味
     *
     * @param ids
     */
    @Transactional
    @Override
    public void Batchdelete(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getById(ids);
            //怎么去判断呢，就看他的这个状态是不是在售
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Long> longs = setmealDishMapper.getsetmeal_dish(ids);
        if (longs != null && longs.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
//        for (Long id : ids) {
//            dishMapper.getBydelite(id);
//
//            dishFlavorMapper.getBydelite(id);
//        }
        //由于这样查数据库查的太多次，效率太低，直接写一个sql让他批量删除
        dishMapper.getBydelites(ids);
        dishFlavorMapper.getBydelites(ids);

    }

    /**
     * 根据id查找菜品,肯定对应找数据库里的东西
     * 找到菜品，然后找到菜品的口味，都传入dishVO中，用copy 口味用set放入
     * @param id
     * @return
     */
    @Override
    public DishVO findById(Long id) {
        Dish dish= dishMapper.getById(Collections.singletonList(id));
        DishVO dishVO = new DishVO();

        List<DishFlavor>dishFlavors= dishFlavorMapper.getById(id);
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 修改菜品数据
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //修改菜品表基本信息
        dishMapper.update(dish);

        //删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish>listDish=dishMapper.list(dish);
        List<DishVO> dishVOList=new ArrayList<>();
        for(Dish d:listDish){
            DishVO dishVO=new DishVO();
            BeanUtils.copyProperties(d,dishVO);
            List<DishFlavor> byId = dishFlavorMapper.getById(d.getId());
            dishVO.setFlavors(byId);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}

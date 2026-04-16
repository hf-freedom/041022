package com.barbershop.saas.controller;

import com.barbershop.saas.common.Result;
import com.barbershop.saas.dto.HairstyleDTO;
import com.barbershop.saas.entity.Hairstyle;
import com.barbershop.saas.service.HairstyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/hairstyle")
public class HairstyleController {

    @Autowired
    private HairstyleService hairstyleService;

    @PostMapping("/add")
    public Result<Void> addHairstyle(@Valid @RequestBody HairstyleDTO dto) {
        hairstyleService.addHairstyle(dto);
        return Result.success();
    }

    @PostMapping("/update/{id}")
    public Result<Void> updateHairstyle(@PathVariable Long id, @Valid @RequestBody HairstyleDTO dto) {
        hairstyleService.updateHairstyle(id, dto);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Hairstyle>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        List<Hairstyle> list = hairstyleService.list(category, keyword);
        return Result.success(list);
    }

    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        List<String> list = hairstyleService.getAllCategories();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Hairstyle> getById(@PathVariable Long id) {
        Hairstyle hairstyle = hairstyleService.getById(id);
        return Result.success(hairstyle);
    }
}

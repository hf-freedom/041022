package com.barbershop.saas.controller;

import com.barbershop.saas.common.Result;
import com.barbershop.saas.dto.BarberDTO;
import com.barbershop.saas.dto.MatchBarberDTO;
import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.entity.BarberLevel;
import com.barbershop.saas.service.BarberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/barber")
public class BarberController {

    @Autowired
    private BarberService barberService;

    @PostMapping("/add")
    public Result<Void> addBarber(@Valid @RequestBody BarberDTO dto) {
        barberService.addBarber(dto);
        return Result.success();
    }

    @PostMapping("/update/{id}")
    public Result<Void> updateBarber(@PathVariable Long id, @Valid @RequestBody BarberDTO dto) {
        barberService.updateBarber(id, dto);
        return Result.success();
    }

    @PostMapping("/match")
    public Result<List<Barber>> matchBarbers(@Valid @RequestBody MatchBarberDTO dto) {
        List<Barber> list = barberService.matchBarbers(dto.getHairstyleId(), dto.getDetailOptions(), null);
        return Result.success(list);
    }

    @GetMapping("/list")
    public Result<List<Barber>> list(@RequestParam(required = false) String keyword) {
        List<Barber> list = barberService.list(keyword);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Barber> getById(@PathVariable Long id) {
        Barber barber = barberService.getById(id);
        return Result.success(barber);
    }

    @GetMapping("/levels")
    public Result<List<BarberLevel>> listLevels() {
        List<BarberLevel> list = barberService.listLevels();
        return Result.success(list);
    }
}

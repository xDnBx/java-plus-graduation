package ru.practicum.compilations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationResponse;
import ru.practicum.compilations.service.CompilationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AdminCompilationController {
    final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponse createCompilation(@Validated(CompilationDto.OnCreate.class) @Valid @RequestBody CompilationDto compilationDto) {
        return compilationService.create(compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        compilationService.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponse patchCompilation(@PathVariable Long compId,
                                                @Validated(CompilationDto.OnUpdate.class) @Valid @RequestBody CompilationDto updateCompilationRequest) {
        return compilationService.update(compId, updateCompilationRequest);
    }
}
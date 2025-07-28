package ru.practicum.compilations.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.service.CompilationService;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.CompilationResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
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
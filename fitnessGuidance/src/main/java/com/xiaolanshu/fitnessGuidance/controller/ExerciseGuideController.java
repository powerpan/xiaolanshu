package com.xiaolanshu.fitnessGuidance.controller;

import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import com.xiaolanshu.fitnessGuidance.pojo.Result;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.service.ExerciseGuideService;
import com.xiaolanshu.fitnessGuidance.service.UserService;
import com.xiaolanshu.fitnessGuidance.utils.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/exerciseguide")
public class ExerciseGuideController {

    @Autowired
    private ExerciseGuideService exerciseGuideService;

    @Autowired
    private UserService userService;

    @GetMapping("/getexerciseguide")
    public Result<ExerciseGuide> getexerciseguide(@RequestHeader(name = "Authorization", required = false) String authorization,
                                                  String jwttoken, String actionPattern, String equipment)
    {
        //令牌验证
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            // http 响应状态码为401
            return Result.error("未登录");
        }
        ExerciseGuide exerciseGuide = exerciseGuideService.getexerciseguide(actionPattern,equipment);
        //System.out.println(exerciseGuide);
        return Result.success(exerciseGuide);
    }

    @GetMapping("/list")
    public Result<ArrayList<ExerciseGuide>> list(@RequestHeader(name = "Authorization", required = false) String authorization,
                                                 String jwttoken, String actionPattern, String equipment, Boolean missingImageOnly,
                                                 Boolean incompleteOnly, Boolean missingStepsOnly, Boolean missingTipsOnly,
                                                 Boolean missingMistakesOnly, Boolean missingAlternativesOnly) {
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            return Result.error("未登录");
        }
        return Result.success(exerciseGuideService.listexerciseguides(
                actionPattern,
                equipment,
                missingImageOnly,
                incompleteOnly,
                missingStepsOnly,
                missingTipsOnly,
                missingMistakesOnly,
                missingAlternativesOnly
        ));
    }

    @GetMapping("/alternatives")
    public Result<ArrayList<ExerciseGuide>> alternatives(@RequestHeader(name = "Authorization", required = false) String authorization,
                                                         String jwttoken, String actionPattern, String preferredEquipment,
                                                         Integer currentGuideId, Integer limit) {
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
        } catch (Exception e) {
            return Result.error("未登录");
        }
        if (actionPattern == null || actionPattern.isBlank()) {
            return Result.error("动作模式不能为空");
        }
        return Result.success(exerciseGuideService.listAlternativeGuides(actionPattern, preferredEquipment, currentGuideId, limit));
    }

    @PostMapping("/addguide")
    public Result addGuide(@RequestHeader(name = "Authorization", required = false) String authorization,
                           String jwttoken, ExerciseGuide exerciseGuide) {
        Result authResult = validateAdmin(authorization, jwttoken);
        if (authResult != null) {
            return authResult;
        }
        Result validationResult = validateGuide(exerciseGuide, false);
        if (validationResult != null) {
            return validationResult;
        }
        exerciseGuideService.addExerciseGuide(exerciseGuide);
        return Result.success();
    }

    @PutMapping("/editguide")
    public Result editGuide(@RequestHeader(name = "Authorization", required = false) String authorization,
                            String jwttoken, ExerciseGuide exerciseGuide) {
        Result authResult = validateAdmin(authorization, jwttoken);
        if (authResult != null) {
            return authResult;
        }
        Result validationResult = validateGuide(exerciseGuide, true);
        if (validationResult != null) {
            return validationResult;
        }
        exerciseGuideService.updateExerciseGuide(exerciseGuide);
        return Result.success();
    }

    @PutMapping("/deleteguide")
    public Result deleteGuide(@RequestHeader(name = "Authorization", required = false) String authorization,
                              String jwttoken, Integer id) {
        Result authResult = validateAdmin(authorization, jwttoken);
        if (authResult != null) {
            return authResult;
        }
        if (id == null) {
            return Result.error("动作指导ID不能为空");
        }
        exerciseGuideService.deleteExerciseGuide(id);
        return Result.success();
    }

    @PostMapping("/uploadimage")
    public Result<String> uploadImage(@RequestHeader(name = "Authorization", required = false) String authorization,
                                      String jwttoken,
                                      @RequestParam("file") MultipartFile file) {
        Result authResult = validateAdmin(authorization, jwttoken);
        if (authResult != null) {
            return authResult;
        }
        if (file == null || file.isEmpty()) {
            return Result.error("请选择图片文件");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }
        String extension = resolveImageExtension(file.getOriginalFilename(), contentType);
        if (extension == null) {
            return Result.error("图片格式仅支持 jpg、jpeg、png、webp、gif");
        }
        try {
            Path uploadDir = Path.of("uploads", "exercise-guides").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            String filename = UUID.randomUUID() + extension;
            Path target = uploadDir.resolve(filename).normalize();
            file.transferTo(target);
            return Result.success("/uploads/exercise-guides/" + filename);
        } catch (IOException e) {
            return Result.error("图片保存失败");
        }
    }

    private String resolveToken(String authorization, String jwttoken) {
        if (authorization != null && !authorization.isBlank()) {
            return authorization;
        }
        return jwttoken;
    }

    private Result validateAdmin(String authorization, String jwttoken) {
        String username;
        try {
            Map<String, Object> claims = Jwtutil.parseToken(resolveToken(authorization, jwttoken));
            username = String.valueOf(claims.get("username"));
        } catch (Exception e) {
            return Result.error("未登录");
        }
        User user = userService.findUsername(username);
        if (user == null || !Objects.equals(user.getIdentity(), "ADMIN")) {
            return Result.error("无相关权限");
        }
        return null;
    }

    private Result validateGuide(ExerciseGuide exerciseGuide, boolean requireId) {
        if (requireId && exerciseGuide.getId() == null) {
            return Result.error("动作指导ID不能为空");
        }
        if (exerciseGuide.getActionPattern() == null || exerciseGuide.getActionPattern().isBlank()) {
            return Result.error("动作模式不能为空");
        }
        if (exerciseGuide.getEquipment() == null || exerciseGuide.getEquipment().isBlank()) {
            return Result.error("器材不能为空");
        }
        if (exerciseGuide.getActionName() == null || exerciseGuide.getActionName().isBlank()) {
            return Result.error("动作名称不能为空");
        }
        return null;
    }

    private String resolveImageExtension(String originalFilename, String contentType) {
        String filename = originalFilename == null ? "" : originalFilename.toLowerCase(Locale.ROOT);
        String extension = "";
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            extension = filename.substring(dot);
        }
        if (extension.isBlank()) {
            extension = switch (contentType.toLowerCase(Locale.ROOT)) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                case "image/gif" -> ".gif";
                default -> "";
            };
        }
        return switch (extension) {
            case ".jpg", ".jpeg", ".png", ".webp", ".gif" -> extension;
            default -> null;
        };
    }

}

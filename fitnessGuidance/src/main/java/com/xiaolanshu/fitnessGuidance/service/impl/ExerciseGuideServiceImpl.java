package com.xiaolanshu.fitnessGuidance.service.impl;

import com.xiaolanshu.fitnessGuidance.mapper.ExerciseGuideMapper;
import com.xiaolanshu.fitnessGuidance.mapper.ExerciseAlternativeMapper;
import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import com.xiaolanshu.fitnessGuidance.service.ExerciseGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ExerciseGuideServiceImpl implements ExerciseGuideService {

    @Autowired
    private ExerciseGuideMapper exerciseGuideMapper;

    @Autowired
    private ExerciseAlternativeMapper exerciseAlternativeMapper;

    @Override
    public ExerciseGuide getexerciseguide(String actionPattern, String equipment){
        return enrichDefaults(exerciseGuideMapper.getexerciseguide(actionPattern,equipment));
    }

    @Override
    public ArrayList<ExerciseGuide> listexerciseguides(String actionPattern, String equipment, Boolean missingImageOnly,
                                                       Boolean incompleteOnly, Boolean missingStepsOnly,
                                                       Boolean missingTipsOnly, Boolean missingMistakesOnly,
                                                       Boolean missingAlternativesOnly) {
        String safePattern = actionPattern == null || actionPattern.isBlank() ? null : actionPattern;
        String safeEquipment = equipment == null || equipment.isBlank() ? null : equipment;
        ArrayList<ExerciseGuide> guides = exerciseGuideMapper.listexerciseguides(
                safePattern,
                safeEquipment,
                Boolean.TRUE.equals(missingImageOnly),
                Boolean.TRUE.equals(incompleteOnly),
                Boolean.TRUE.equals(missingStepsOnly),
                Boolean.TRUE.equals(missingTipsOnly),
                Boolean.TRUE.equals(missingMistakesOnly),
                Boolean.TRUE.equals(missingAlternativesOnly)
        );
        guides.forEach(this::enrichDefaults);
        return guides;
    }

    @Override
    public ArrayList<ExerciseGuide> listAlternativeGuides(String actionPattern, String preferredEquipment, Integer currentGuideId, Integer limit) {
        Integer safeLimit = limit == null || limit < 1 ? 4 : Math.min(limit, 8);
        ArrayList<ExerciseGuide> guides = exerciseAlternativeMapper.alternativesForPattern(
                actionPattern,
                preferredEquipment,
                currentGuideId,
                safeLimit
        );
        guides.forEach(this::enrichDefaults);
        return guides;
    }

    @Override
    public void addExerciseGuide(ExerciseGuide exerciseGuide) {
        normalize(exerciseGuide);
        exerciseGuideMapper.addExerciseGuide(exerciseGuide);
    }

    @Override
    public void updateExerciseGuide(ExerciseGuide exerciseGuide) {
        normalize(exerciseGuide);
        exerciseGuideMapper.updateExerciseGuide(exerciseGuide);
    }

    @Override
    public void deleteExerciseGuide(Integer id) {
        exerciseGuideMapper.deleteExerciseGuide(id);
    }

    private void normalize(ExerciseGuide exerciseGuide) {
        if (exerciseGuide.getImageurl() != null && exerciseGuide.getImageurl().isBlank()) {
            exerciseGuide.setImageurl(null);
        }
        if (exerciseGuide.getImageCredit() != null && exerciseGuide.getImageCredit().isBlank()) {
            exerciseGuide.setImageCredit(null);
        }
        if (exerciseGuide.getImageSourceUrl() != null && exerciseGuide.getImageSourceUrl().isBlank()) {
            exerciseGuide.setImageSourceUrl(null);
        }
        if (exerciseGuide.getPrimaryMuscles() != null && exerciseGuide.getPrimaryMuscles().isBlank()) {
            exerciseGuide.setPrimaryMuscles(null);
        }
        if (exerciseGuide.getSecondaryMuscles() != null && exerciseGuide.getSecondaryMuscles().isBlank()) {
            exerciseGuide.setSecondaryMuscles(null);
        }
        if (exerciseGuide.getDifficulty() != null && exerciseGuide.getDifficulty().isBlank()) {
            exerciseGuide.setDifficulty(null);
        }
        if (exerciseGuide.getContraindications() != null && exerciseGuide.getContraindications().isBlank()) {
            exerciseGuide.setContraindications(null);
        }
        if (exerciseGuide.getCommonMistakes() != null && exerciseGuide.getCommonMistakes().isBlank()) {
            exerciseGuide.setCommonMistakes(null);
        }
        if (exerciseGuide.getSuitableFor() != null && exerciseGuide.getSuitableFor().isBlank()) {
            exerciseGuide.setSuitableFor(null);
        }
        if (exerciseGuide.getAlternatives() != null && exerciseGuide.getAlternatives().isBlank()) {
            exerciseGuide.setAlternatives(null);
        }
    }

    private ExerciseGuide enrichDefaults(ExerciseGuide guide) {
        if (guide == null) {
            return null;
        }
        if (isBlank(guide.getPrimaryMuscles())) {
            guide.setPrimaryMuscles(defaultPrimaryMuscles(guide.getActionPattern()));
        }
        if (isBlank(guide.getSecondaryMuscles())) {
            guide.setSecondaryMuscles(defaultSecondaryMuscles(guide.getActionPattern()));
        }
        if (isBlank(guide.getDifficulty())) {
            guide.setDifficulty(defaultDifficulty(guide.getActionPattern()));
        }
        if (isBlank(guide.getContraindications())) {
            guide.setContraindications(defaultContraindications(guide.getActionPattern()));
        }
        if (isBlank(guide.getCommonMistakes())) {
            guide.setCommonMistakes(defaultCommonMistakes(guide.getActionPattern()));
        }
        if (isBlank(guide.getSuitableFor())) {
            guide.setSuitableFor(defaultSuitableFor(guide.getActionPattern()));
        }
        if (isBlank(guide.getAlternatives())) {
            guide.setAlternatives(defaultAlternatives(guide.getActionPattern()));
        }
        guide.setMissingImage(isBlank(guide.getImageurl()));
        guide.setIncomplete(isBlank(guide.getSteps())
                || isBlank(guide.getTips())
                || isBlank(guide.getCommonMistakes())
                || isBlank(guide.getPrimaryMuscles())
                || isBlank(guide.getAlternatives()));
        guide.setQualityScore(resolveQualityScore(guide));
        return guide;
    }

    private Integer resolveQualityScore(ExerciseGuide guide) {
        int passed = 0;
        String[] fields = {
                guide.getActionName(),
                guide.getDescription(),
                guide.getSteps(),
                guide.getTips(),
                guide.getPrimaryMuscles(),
                guide.getDifficulty(),
                guide.getCommonMistakes(),
                guide.getContraindications(),
                guide.getSuitableFor(),
                guide.getAlternatives()
        };
        for (String field : fields) {
            if (!isBlank(field)) {
                passed += 1;
            }
        }
        return Math.round(passed * 100F / fields.length);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String defaultPrimaryMuscles(String actionPattern) {
        return switch (actionPattern) {
            case "水平推" -> "胸大肌、三角肌前束、肱三头肌";
            case "垂直推" -> "三角肌、肱三头肌";
            case "水平拉" -> "背阔肌、菱形肌、斜方肌中下束";
            case "垂直拉" -> "背阔肌、大圆肌、肱二头肌";
            case "下肢蹲" -> "股四头肌、臀大肌";
            case "髋铰链" -> "臀大肌、腘绳肌";
            case "单腿训练" -> "股四头肌、臀大肌、臀中肌";
            case "核心稳定" -> "腹横肌、腹直肌、腹斜肌";
            case "手臂弯举" -> "肱二头肌、肱肌";
            case "手臂伸展" -> "肱三头肌";
            case "灵活恢复" -> "肩、胸椎、髋、踝活动链";
            default -> "全身基础肌群";
        };
    }

    private String defaultSecondaryMuscles(String actionPattern) {
        return switch (actionPattern) {
            case "水平推" -> "核心、前锯肌";
            case "垂直推" -> "上胸、核心、斜方肌下束";
            case "水平拉" -> "肱二头肌、后三角肌、核心";
            case "垂直拉" -> "下斜方肌、核心、握力";
            case "下肢蹲" -> "腘绳肌、小腿、核心";
            case "髋铰链" -> "竖脊肌、背阔肌、核心";
            case "单腿训练" -> "腘绳肌、小腿、核心";
            case "核心稳定" -> "臀部、肩胛稳定肌、髋屈肌";
            case "手臂弯举" -> "前臂屈肌、肩部稳定肌";
            case "手臂伸展" -> "肩部稳定肌、胸大肌";
            case "灵活恢复" -> "轻度核心和稳定肌群";
            default -> "核心稳定肌群";
        };
    }

    private String defaultDifficulty(String actionPattern) {
        return switch (actionPattern) {
            case "垂直推", "垂直拉", "髋铰链", "单腿训练" -> "进阶";
            default -> "新手";
        };
    }

    private String defaultContraindications(String actionPattern) {
        return switch (actionPattern) {
            case "水平推" -> "肩肘疼痛明显时先降低幅度或更换动作。";
            case "垂直推" -> "肩峰撞击感或腰椎代偿明显时先降低重量。";
            case "水平拉" -> "下背无法稳定或肩部拉扯痛时先减小幅度。";
            case "垂直拉" -> "肩部上举疼痛或握力不足时先使用辅助版本。";
            case "下肢蹲" -> "膝髋疼痛明显或无法稳定下蹲时先缩小幅度。";
            case "髋铰链" -> "下背疼痛或无法保持中立位时先练徒手髋铰链。";
            case "单腿训练" -> "平衡差或膝踝不稳时先扶墙或降低幅度。";
            case "核心稳定" -> "腰痛明显或无法控制骨盆时先缩短保持时间。";
            case "手臂弯举" -> "肘腕疼痛时先降低阻力并检查手腕角度。";
            case "手臂伸展" -> "肩肘不适时先缩小幅度或换成弹力带版本。";
            case "灵活恢复" -> "任何关节刺痛都应停止并缩小活动范围。";
            default -> "出现关节刺痛或动作失控时应停止。";
        };
    }

    private String defaultCommonMistakes(String actionPattern) {
        return switch (actionPattern) {
            case "水平推" -> "塌腰、耸肩、下降过快、手腕过度折叠。";
            case "垂直推" -> "腰部后仰、耸肩顶脖子、推举路径过度前移。";
            case "水平拉" -> "耸肩、含胸、用腰甩动、只拉手臂不收肩胛。";
            case "垂直拉" -> "身体摆动、后仰过多、耸肩、下放失控。";
            case "下肢蹲" -> "膝盖内扣、脚跟离地、弯腰塌背、底部松掉核心。";
            case "髋铰链" -> "弯腰代替髋折叠、重量离身体太远、站起时过度后仰。";
            case "单腿训练" -> "步幅过小、膝盖内扣、身体前后晃动、左右次数不一致。";
            case "核心稳定" -> "塌腰、撅臀、憋气、用脖子代偿。";
            case "手臂弯举" -> "身体后仰借力、肘部前后摆动、下放失控。";
            case "手臂伸展" -> "肘部外翻、肩膀耸起、下降过深、用身体摆动借力。";
            case "灵活恢复" -> "动作过快、追求疲劳感、忽略呼吸、拉伸到疼痛。";
            default -> "动作过快、核心放松、关节路径失控。";
        };
    }

    private String defaultSuitableFor(String actionPattern) {
        return switch (actionPattern) {
            case "水平推" -> "需要提升上肢推力和胸肩控制的训练者。";
            case "垂直推" -> "需要提升肩部推举和上肢稳定能力的训练者。";
            case "水平拉" -> "需要改善背部厚度、体态和肩胛控制的训练者。";
            case "垂直拉" -> "需要提升背阔肌发力和垂直拉能力的训练者。";
            case "下肢蹲" -> "需要提升下肢基础力量和日常蹲起能力的训练者。";
            case "髋铰链" -> "需要强化后链、臀腿发力和硬拉基础的训练者。";
            case "单腿训练" -> "需要改善单侧力量、平衡和左右差异的训练者。";
            case "核心稳定" -> "需要提升躯干稳定和动作控制的训练者。";
            case "手臂弯举" -> "需要补足手臂拉力和肘屈控制的训练者。";
            case "手臂伸展" -> "需要补足推类动作辅助力量的训练者。";
            case "灵活恢复" -> "训练前热身、训练后整理和恢复日活动。";
            default -> "需要补足基础动作能力的训练者。";
        };
    }

    private String defaultAlternatives(String actionPattern) {
        return switch (actionPattern) {
            case "水平推" -> "跪姿俯卧撑、哑铃卧推、弹力带胸推、坐姿推胸机。";
            case "垂直推" -> "Pike 俯卧撑、哑铃肩推、杠铃推举、肩推机。";
            case "水平拉" -> "反向划船、哑铃划船、弹力带划船、坐姿划船机。";
            case "垂直拉" -> "弹力带下拉、辅助引体向上、高位下拉。";
            case "下肢蹲" -> "徒手深蹲、高脚杯深蹲、杠铃深蹲、腿举机。";
            case "髋铰链" -> "早安式、罗马尼亚硬拉、弹力带拉穿、绳索拉穿。";
            case "单腿训练" -> "反向箭步蹲、分腿蹲、台阶上步、扶墙单腿蹲。";
            case "核心稳定" -> "平板支撑、死虫、鸟狗、Pallof Press、悬垂举膝。";
            case "手臂弯举" -> "自阻力弯举、哑铃弯举、弹力带弯举、绳索弯举。";
            case "手臂伸展" -> "窄距俯卧撑、椅上臂屈伸、哑铃臂屈伸、绳索下压。";
            case "灵活恢复" -> "肩绕环、胸椎旋转、髋屈伸动态拉伸、弹力带轻阻力激活。";
            default -> "同动作模式、同难度且器材可用的动作。";
        };
    }
}

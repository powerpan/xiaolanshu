package com.xiaolanshu.fitnessGuidance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaolanshu.fitnessGuidance.mapper.ExerciseGuideMapper;
import com.xiaolanshu.fitnessGuidance.mapper.FitnessCheckinMapper;
import com.xiaolanshu.fitnessGuidance.mapper.NutritionPreferenceMapper;
import com.xiaolanshu.fitnessGuidance.mapper.NutritionRecommendationHistoryMapper;
import com.xiaolanshu.fitnessGuidance.mapper.NoticeMapper;
import com.xiaolanshu.fitnessGuidance.mapper.PlanTaskRecordMapper;
import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import com.xiaolanshu.fitnessGuidance.pojo.NutritionPreference;
import com.xiaolanshu.fitnessGuidance.pojo.NutritionRecommendationHistory;
import com.xiaolanshu.fitnessGuidance.pojo.Notice;
import com.xiaolanshu.fitnessGuidance.pojo.PlanTaskRecord;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import com.xiaolanshu.fitnessGuidance.service.FitnessCheckinService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestExecutionListeners(
        listeners = DependencyInjectionTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.REPLACE_DEFAULTS
)
class FitnessGuidanceApplicationTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private ExerciseGuideMapper exerciseGuideMapper;

    @Autowired
    private FitnessCheckinMapper fitnessCheckinMapper;

    @Autowired
    private FitnessCheckinService fitnessCheckinService;

    @Autowired
    private NutritionPreferenceMapper nutritionPreferenceMapper;

    @Autowired
    private PlanTaskRecordMapper planTaskRecordMapper;

    @Autowired
    private NutritionRecommendationHistoryMapper nutritionRecommendationHistoryMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void userSerializationDoesNotExposePassword() throws Exception {
        User user = new User();
        user.setUsername("demo");
        user.setNickname("测试用户");
        user.setPassword("pbkdf2$65536$salt$hash");

        String json = objectMapper.writeValueAsString(user);

        assertThat(json).contains("\"username\":\"demo\"");
        assertThat(json).doesNotContain("password");
        assertThat(json).doesNotContain("pbkdf2");
    }

    @Test
    void noticeEditAndDeleteOnlyAffectSelectedId() {
        String title = "重复公告-" + System.nanoTime();
        String content = "同一内容";
        noticeMapper.addnotice("admin", title, content);
        noticeMapper.addnotice("admin", title, content);

        List<Notice> created = noticeMapper.getnotice().stream()
                .filter(notice -> title.equals(notice.getTitle()) && content.equals(notice.getContent()))
                .toList();
        assertThat(created).hasSize(2);

        Integer targetId = created.get(0).getId();
        Integer otherId = created.get(1).getId();
        try {
            noticeMapper.editNoticeById(targetId, title + "-已编辑", content + "-已编辑");

            Notice target = noticeMapper.getNoticeDetail(targetId);
            Notice other = noticeMapper.getNoticeDetail(otherId);
            assertThat(target.getTitle()).isEqualTo(title + "-已编辑");
            assertThat(target.getContent()).isEqualTo(content + "-已编辑");
            assertThat(other.getTitle()).isEqualTo(title);
            assertThat(other.getContent()).isEqualTo(content);

            noticeMapper.deleteNoticeById(targetId);
            assertThat(noticeMapper.getNoticeDetail(targetId)).isNull();
            assertThat(noticeMapper.getNoticeDetail(otherId)).isNotNull();
        } finally {
            noticeMapper.deleteNoticeById(targetId);
            noticeMapper.deleteNoticeById(otherId);
        }
    }

    @Test
    void exerciseGuideCrudKeepsAdminUploadedImageUrl() {
        String actionPattern = "测试动作-" + System.nanoTime();
        String equipment = "测试器材";
        ExerciseGuide guide = new ExerciseGuide();
        guide.setActionPattern(actionPattern);
        guide.setActionName("测试动作名称");
        guide.setEquipment(equipment);
        guide.setDescription("测试描述");
        guide.setSteps("第一步|第二步");
        guide.setTips("要点一|要点二");
        guide.setPrimaryMuscles("测试主肌群");
        guide.setCommonMistakes("测试常见错误");
        exerciseGuideMapper.addExerciseGuide(guide);

        List<ExerciseGuide> created = exerciseGuideMapper.listexerciseguides(actionPattern, equipment, false, false, false, false, false, false);
        assertThat(created).hasSize(1);

        Integer id = created.get(0).getId();
        try {
            ExerciseGuide update = created.get(0);
            update.setActionName("测试动作名称-已更新");
            update.setImageurl("/uploads/exercise-guides/test.png");
            update.setImageCredit("管理员上传");
            exerciseGuideMapper.updateExerciseGuide(update);

            ExerciseGuide updated = exerciseGuideMapper.listexerciseguides(actionPattern, equipment, false, false, false, false, false, false).get(0);
            assertThat(updated.getActionName()).isEqualTo("测试动作名称-已更新");
            assertThat(updated.getImageurl()).isEqualTo("/uploads/exercise-guides/test.png");
            assertThat(updated.getImageCredit()).isEqualTo("管理员上传");
            assertThat(updated.getPrimaryMuscles()).isEqualTo("测试主肌群");
            assertThat(updated.getCommonMistakes()).isEqualTo("测试常见错误");
        } finally {
            exerciseGuideMapper.deleteExerciseGuide(id);
        }
    }

    @Test
    void seededExerciseGuidesUseActionSpecificMetadata() {
        ExerciseGuide benchPress = exerciseGuideMapper.getexerciseguide("水平推", "杠铃");
        ExerciseGuide pushUp = exerciseGuideMapper.getexerciseguide("水平推", "徒手");
        ExerciseGuide chairDip = exerciseGuideMapper.getexerciseguide("手臂伸展", "徒手");

        assertThat(benchPress.getActionName()).isEqualTo("杠铃卧推");
        assertThat(benchPress.getDifficulty()).isEqualTo("熟练");
        assertThat(benchPress.getContraindications()).contains("无安全架");

        assertThat(pushUp.getActionName()).isEqualTo("俯卧撑");
        assertThat(pushUp.getAlternatives()).contains("上斜俯卧撑");

        assertThat(chairDip.getActionName()).isEqualTo("椅上臂屈伸");
        assertThat(chairDip.getPrimaryMuscles()).contains("三角肌前束");
        assertThat(chairDip.getCommonMistakes()).contains("椅子不稳");
    }

    @Test
    void nutritionPreferenceCanBeInsertedAndUpdated() {
        String username = "preference-" + System.nanoTime();
        NutritionPreference preference = new NutritionPreference();
        preference.setUsername(username);
        preference.setDietType("高蛋白");
        preference.setAllergies("乳糖不耐");
        preference.setBudgetLevel("中等");
        preference.setEatingOutFrequency("偶尔外食");
        preference.setMealCount(4);
        preference.setTastePreference("清淡");
        nutritionPreferenceMapper.insert(preference);

        NutritionPreference created = nutritionPreferenceMapper.findByUsername(username);
        assertThat(created.getDietType()).isEqualTo("高蛋白");
        assertThat(created.getAllergies()).isEqualTo("乳糖不耐");

        created.setBudgetLevel("经济");
        created.setMealCount(3);
        Integer updatedRows = nutritionPreferenceMapper.update(created);

        NutritionPreference updated = nutritionPreferenceMapper.findByUsername(username);
        assertThat(updatedRows).isEqualTo(1);
        assertThat(updated.getBudgetLevel()).isEqualTo("经济");
        assertThat(updated.getMealCount()).isEqualTo(3);
    }

    @Test
    void planTaskRecordCanBeUpsertedByDayAndIndex() {
        PlanTaskRecord record = new PlanTaskRecord();
        String username = "plan-" + System.nanoTime();
        record.setUsername(username);
        record.setPlanDate(LocalDate.now());
        record.setDaytime(1);
        record.setActionIndex(0);
        record.setActionPattern("水平推");
        record.setActionName("俯卧撑");
        record.setEquipment("徒手");
        record.setCompleted(true);
        planTaskRecordMapper.insert(record);

        List<PlanTaskRecord> created = planTaskRecordMapper.listByDay(username, LocalDate.now(), 1);
        assertThat(created).hasSize(1);

        PlanTaskRecord update = created.get(0);
        update.setCompleted(false);
        update.setActualSets(2);
        Integer updatedRows = planTaskRecordMapper.updateByKey(update);

        List<PlanTaskRecord> updated = planTaskRecordMapper.listByDay(username, LocalDate.now(), 1);
        assertThat(updatedRows).isEqualTo(1);
        assertThat(updated.get(0).getCompleted()).isFalse();
        assertThat(updated.get(0).getActualSets()).isEqualTo(2);
    }

    @Test
    void planDayCheckinRequiresPreviousPlanDays() {
        String username = "checkin-order-" + System.nanoTime();

        assertThatThrownBy(() -> fitnessCheckinService.saveToday(username, 30, "状态不错", "直接打第 2 天", 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("请先完成第 1 天训练打卡");

        fitnessCheckinMapper.insertToday(
                username,
                LocalDate.now().minusDays(1),
                1,
                30,
                "状态不错",
                "完成第 1 天");

        fitnessCheckinService.saveToday(username, 35, "有挑战", "完成第 2 天", 2);

        assertThat(fitnessCheckinMapper.findByDate(username, LocalDate.now()).getPlanDay()).isEqualTo(2);
    }

    @Test
    void todayPlanDayCannotBeChangedAfterCheckin() {
        String username = "checkin-conflict-" + System.nanoTime();
        fitnessCheckinService.saveToday(username, 30, "状态不错", "完成第 1 天", 1);

        assertThatThrownBy(() -> fitnessCheckinService.saveToday(username, 30, "状态不错", "改成第 2 天", 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("今天已记录第 1 天训练");
    }

    @Test
    void nutritionRecommendationHistoryCanBeListedNewestFirst() {
        NutritionRecommendationHistory history = new NutritionRecommendationHistory();
        String username = "history-" + System.nanoTime();
        history.setUsername(username);
        history.setTargetCalories(2000);
        history.setProteinGrams(120);
        history.setCarbohydrateGrams(240);
        history.setFatGrams(60);
        history.setPreferenceSummary("测试偏好");
        history.setSummary("测试推荐");
        nutritionRecommendationHistoryMapper.insert(history);

        List<NutritionRecommendationHistory> histories = nutritionRecommendationHistoryMapper.recent(username, 5);

        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).getTargetCalories()).isEqualTo(2000);
        assertThat(histories.get(0).getPreferenceSummary()).isEqualTo("测试偏好");
    }

}

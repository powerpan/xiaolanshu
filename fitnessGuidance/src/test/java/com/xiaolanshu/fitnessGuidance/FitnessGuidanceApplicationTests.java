package com.xiaolanshu.fitnessGuidance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaolanshu.fitnessGuidance.mapper.ExerciseGuideMapper;
import com.xiaolanshu.fitnessGuidance.mapper.NoticeMapper;
import com.xiaolanshu.fitnessGuidance.pojo.ExerciseGuide;
import com.xiaolanshu.fitnessGuidance.pojo.Notice;
import com.xiaolanshu.fitnessGuidance.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        exerciseGuideMapper.addExerciseGuide(guide);

        List<ExerciseGuide> created = exerciseGuideMapper.listexerciseguides(actionPattern, equipment, false);
        assertThat(created).hasSize(1);

        Integer id = created.get(0).getId();
        try {
            ExerciseGuide update = created.get(0);
            update.setActionName("测试动作名称-已更新");
            update.setImageurl("/uploads/exercise-guides/test.png");
            update.setImageCredit("管理员上传");
            exerciseGuideMapper.updateExerciseGuide(update);

            ExerciseGuide updated = exerciseGuideMapper.listexerciseguides(actionPattern, equipment, false).get(0);
            assertThat(updated.getActionName()).isEqualTo("测试动作名称-已更新");
            assertThat(updated.getImageurl()).isEqualTo("/uploads/exercise-guides/test.png");
            assertThat(updated.getImageCredit()).isEqualTo("管理员上传");
        } finally {
            exerciseGuideMapper.deleteExerciseGuide(id);
        }
    }

}

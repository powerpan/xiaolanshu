-- 跃鹿运动健身指导系统 openGauss 初始化脚本
-- 适用于新建或可重置的 openGauss 数据库。执行前请确认目标库中的同名表可以被删除。

DROP TABLE IF EXISTS nutritionpreferences;
DROP TABLE IF EXISTS eatingscenarios;
DROP TABLE IF EXISTS foodreplacements;
DROP TABLE IF EXISTS mealtemplates;
DROP TABLE IF EXISTS fooditems;
DROP TABLE IF EXISTS nutritionrecommendationhistories;
DROP TABLE IF EXISTS exercisealternatives;
DROP TABLE IF EXISTS planadjustmentrecords;
DROP TABLE IF EXISTS trainingcycles;
DROP TABLE IF EXISTS plantaskrecords;
DROP TABLE IF EXISTS fitnesscheckins;
DROP TABLE IF EXISTS exerciseguides;
DROP TABLE IF EXISTS actiondetails;
DROP TABLE IF EXISTS trainingparams;
DROP TABLE IF EXISTS daytrains;
DROP TABLE IF EXISTS fitnessplanrules;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS tempnotices;
DROP TABLE IF EXISTS notices;
DROP TABLE IF EXISTS userprofiles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    nickname VARCHAR(50),
    password VARCHAR(100) NOT NULL,
    userpic VARCHAR(1000),
    createtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    registered BOOLEAN DEFAULT FALSE,
    identity VARCHAR(20) DEFAULT 'user',
    specialty VARCHAR(200),
    height DOUBLE PRECISION,
    weight DOUBLE PRECISION
);

CREATE TABLE userprofiles (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    fitnessgoal VARCHAR(50) NOT NULL,
    weeklyfrequency INT NOT NULL,
    equipment VARCHAR(50) NOT NULL,
    exlevel VARCHAR(50) NOT NULL
);

CREATE TABLE notices (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    author VARCHAR(50),
    noticetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tempnotices (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    author VARCHAR(50),
    tempnoticetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE articles (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    author VARCHAR(50),
    topic VARCHAR(100),
    articletime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fitnessplanrules (
    id SERIAL PRIMARY KEY,
    fitnessgoal VARCHAR(50) NOT NULL,
    weeklyfrequency INT NOT NULL,
    splitmode TEXT NOT NULL
);

CREATE UNIQUE INDEX uk_fitnessplanrules_goal_frequency
    ON fitnessplanrules(fitnessgoal, weeklyfrequency);

CREATE TABLE daytrains (
    id SERIAL PRIMARY KEY,
    splitmode VARCHAR(100) NOT NULL UNIQUE,
    actionpattern TEXT NOT NULL
);

CREATE TABLE trainingparams (
    id SERIAL PRIMARY KEY,
    actionpattern VARCHAR(100) NOT NULL,
    exlevel VARCHAR(50) NOT NULL,
    minsets INT NOT NULL,
    maxsets INT NOT NULL,
    minreps INT NOT NULL,
    maxreps INT NOT NULL,
    minrestseconds INT NOT NULL,
    maxrestseconds INT NOT NULL
);

CREATE UNIQUE INDEX uk_trainingparams_pattern_level
    ON trainingparams(actionpattern, exlevel);

CREATE TABLE actiondetails (
    id SERIAL PRIMARY KEY,
    actionpattern VARCHAR(100) NOT NULL,
    equipment VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE UNIQUE INDEX uk_actiondetails_pattern_equipment
    ON actiondetails(actionpattern, equipment);

CREATE TABLE exerciseguides (
    id SERIAL PRIMARY KEY,
    actionpattern VARCHAR(100) NOT NULL,
    actionname VARCHAR(100),
    equipment VARCHAR(50) NOT NULL,
    description TEXT,
    steps TEXT,
    tips TEXT,
    imageurl VARCHAR(1000),
    imagecredit VARCHAR(200),
    imagesourceurl VARCHAR(1000),
    primarymuscles VARCHAR(200),
    secondarymuscles VARCHAR(200),
    difficulty VARCHAR(50),
    contraindications TEXT,
    commonmistakes TEXT,
    suitablefor TEXT,
    alternatives TEXT
);

CREATE UNIQUE INDEX uk_exerciseguides_pattern_equipment
    ON exerciseguides(actionpattern, equipment);

CREATE TABLE fitnesscheckins (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    checkin_date DATE NOT NULL,
    plan_day INT,
    duration_minutes INT DEFAULT 0,
    mood VARCHAR(50),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_fitnesscheckins_username_date
    ON fitnesscheckins(username, checkin_date);

CREATE TABLE plantaskrecords (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    plan_date DATE NOT NULL,
    daytime INT NOT NULL,
    action_index INT NOT NULL,
    actionpattern VARCHAR(100),
    actionname VARCHAR(100),
    equipment VARCHAR(50),
    completed BOOLEAN DEFAULT FALSE,
    actual_sets INT,
    actual_reps INT,
    difficulty_score INT,
    note TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_plantaskrecords_key
    ON plantaskrecords(username, plan_date, daytime, action_index);

CREATE TABLE trainingcycles (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    cycle_week INT DEFAULT 1,
    stage VARCHAR(80),
    cycle_goal TEXT,
    recovery_day INT DEFAULT 4,
    start_date DATE,
    end_date DATE,
    status VARCHAR(30) DEFAULT 'active'
);

CREATE INDEX idx_trainingcycles_username_status
    ON trainingcycles(username, status);

CREATE TABLE planadjustmentrecords (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    reason VARCHAR(200),
    adjustment_type VARCHAR(80),
    summary TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_planadjustmentrecords_username_time
    ON planadjustmentrecords(username, created_at);

CREATE TABLE exercisealternatives (
    id SERIAL PRIMARY KEY,
    source_guide_id INT NOT NULL,
    target_guide_id INT NOT NULL,
    reason VARCHAR(300),
    priority INT DEFAULT 10
);

CREATE UNIQUE INDEX uk_exercisealternatives_pair
    ON exercisealternatives(source_guide_id, target_guide_id);

CREATE TABLE nutritionpreferences (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    diettype VARCHAR(50) DEFAULT '均衡饮食',
    allergies VARCHAR(300),
    budgetlevel VARCHAR(50) DEFAULT '中等',
    eatingoutfrequency VARCHAR(50) DEFAULT '偶尔外食',
    mealcount INT DEFAULT 4,
    tastepreference VARCHAR(100) DEFAULT '清淡'
);

CREATE TABLE nutritionrecommendationhistories (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    targetcalories INT,
    proteingrams INT,
    carbohydrategrams INT,
    fatgrams INT,
    preferencesummary TEXT,
    summary TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fooditems (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    calories_per100g INT,
    protein_per100g DOUBLE PRECISION,
    carbohydrate_per100g DOUBLE PRECISION,
    fat_per100g DOUBLE PRECISION,
    tags VARCHAR(300),
    scene VARCHAR(80)
);

CREATE TABLE mealtemplates (
    id SERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    goal VARCHAR(50) DEFAULT '通用',
    scene VARCHAR(80) DEFAULT '通用',
    target_calories INT,
    description TEXT,
    foods TEXT
);

CREATE UNIQUE INDEX uk_mealtemplates_identity
    ON mealtemplates(name, meal_type, goal, scene);

CREATE TABLE foodreplacements (
    id SERIAL PRIMARY KEY,
    source_food VARCHAR(100) NOT NULL,
    replacement_food VARCHAR(200) NOT NULL,
    reason TEXT,
    category VARCHAR(50)
);

CREATE UNIQUE INDEX uk_foodreplacements_pair
    ON foodreplacements(source_food, replacement_food);

CREATE TABLE eatingscenarios (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    goal VARCHAR(50) DEFAULT '通用',
    strategy TEXT,
    avoid TEXT,
    example TEXT
);

CREATE UNIQUE INDEX uk_eatingscenarios_identity
    ON eatingscenarios(name, goal);

INSERT INTO users (
    username, nickname, password, userpic, createtime, registered, identity, specialty, height, weight
) VALUES
('admin', '管理员', 'pbkdf2$65536$9ojDiz+XxDpAAyzkmKoBSw==$Ykc4o6smaK4Z6rWKd92wcMpjaRKNHhSsQQwXGGY4o5g=', '', CURRENT_TIMESTAMP, TRUE, 'ADMIN', '力量训练', 175, 70),
('demo', '小蓝用户', 'pbkdf2$65536$VkDq1OiLzwV4K9r8COcaNA==$/juevOS0Y31WJkELtkAynsK9Wh09GzPdqfRInvMks98=', '', CURRENT_TIMESTAMP, TRUE, 'user', '跑步和徒手训练', 168, 62);

INSERT INTO userprofiles (
    username, fitnessgoal, weeklyfrequency, equipment, exlevel
) VALUES
('admin', '保持健康', 3, '徒手', '新手'),
('demo', '保持健康', 3, '徒手', '新手');

INSERT INTO nutritionpreferences (
    username, diettype, allergies, budgetlevel, eatingoutfrequency, mealcount, tastepreference
) VALUES
('demo', '均衡饮食', '无', '中等', '偶尔外食', 4, '清淡'),
('admin', '高蛋白', '无', '中等', '很少外食', 4, '清淡');

INSERT INTO notices (
    id, title, content, author, noticetime
) VALUES
(1, '欢迎来到跃鹿运动', '这里会同步训练提醒、产品更新和健康建议。你可以先完善训练目标、每周频率、可用器材和当前水平，再进入训练计划查看今日安排。', 'admin', CURRENT_TIMESTAMP),
(2, '本周训练建议：先稳定，再加量', '如果你刚开始恢复训练，本周优先完成计划内次数，不需要追求额外加练。每次训练前做 5 到 8 分钟热身，训练后记录状态，连续两周稳定后再逐步增加组数。', 'admin', CURRENT_TIMESTAMP - INTERVAL '1 day');

INSERT INTO articles (
    id, title, content, author, topic, articletime
) VALUES
(1, '新手训练前的三个提醒', '刚开始训练时，最容易被忽略的是训练前后的准备，而不是动作本身。第一，先热身。用 5 到 8 分钟让关节和心率进入状态，肩、髋、膝和踝都要动起来。第二，把动作速度放慢。新手阶段先让身体记住正确轨迹，下降、停顿、发力都要能控制。第三，训练后记录身体反馈。今天哪里累，哪个动作不稳，休息是否足够，这些记录会帮助你下次更稳地调整。稳定执行比一次练很多更重要。', 'admin', '新手入门', CURRENT_TIMESTAMP),
(2, '徒手训练也能做好基础力量', '没有器械并不代表训练质量低。深蹲、俯卧撑、平板支撑和臀桥可以覆盖推、蹲、核心和髋伸展等基础动作模式。建议先从三轮循环开始，每个动作保持能够控制的次数，动作间休息 45 到 75 秒。徒手训练的关键不是把次数堆到极限，而是保持身体线条、关节方向和呼吸节奏。等到动作稳定后，再通过增加组数、放慢离心或缩短休息来提高难度。', 'admin', '徒手训练', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(3, '减脂期怎么安排饮食和训练', '减脂不是把食物砍到越少越好，而是在可持续的热量缺口里保持训练质量。每餐先保证蛋白质，再安排主食和蔬菜。训练日可以把更多碳水放在训练前后，休息日保持蔬菜和饮水。训练上建议保留力量训练，它能帮助你维持肌肉和基础代谢。体重每天波动很正常，更应该看两周趋势、围度和精神状态。', 'admin', '饮食策略', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(4, '如何判断今天是否适合加量', '加量不是看情绪，而是看连续记录。若最近两周训练完成率稳定、动作质量没有下降、睡眠和食欲正常，可以优先增加一组或略微增加动作难度。若连续两次训练都出现动作变形、关节不适或恢复困难，就应先维持原计划甚至减少训练量。科学训练的核心是可恢复的渐进，而不是每次都练到极限。', 'admin', '训练复盘', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(5, '动作指导应该看哪些细节', '看动作指导时不要只看最终姿势，还要看起始位、发力路径和错误代偿。推类动作关注肩胛稳定和腰腹控制；拉类动作关注肩胛下沉、肘部路径和颈部放松；下肢动作关注脚跟稳定、膝盖方向和髋部发力。把这些要点记录到训练备注里，下一次训练会更容易进步。', 'admin', '动作技术', CURRENT_TIMESTAMP - INTERVAL '4 days'),
(6, '训练打卡不是签到，而是复盘数据', '打卡的价值不在于留下一个完成标记，而在于沉淀趋势。训练时长、状态、备注和连续天数能帮助你判断计划是否过轻、过重或刚好合适。若连续打卡但状态越来越差，说明需要恢复；若很少打卡但每次时长很长，说明计划可能不够稳定。记录越轻，反馈越及时，下一次训练就越容易调整。', 'admin', '习惯养成', CURRENT_TIMESTAMP - INTERVAL '5 days');

INSERT INTO fitnessplanrules (
    fitnessgoal, weeklyfrequency, splitmode
) VALUES
('保持健康', 1, '["全身训练"]'),
('保持健康', 2, '["上肢训练","下肢与核心"]'),
('保持健康', 3, '["上肢训练","下肢训练","核心与灵活性"]'),
('保持健康', 4, '["上肢推","下肢训练","上肢拉","核心与灵活性"]'),
('保持健康', 5, '["上肢推","下肢训练","上肢拉","核心训练","全身轻量"]'),
('保持健康', 6, '["上肢推","下肢训练","上肢拉","核心训练","全身轻量","恢复训练"]'),
('增肌', 1, '["全身训练"]'),
('增肌', 2, '["上肢推","下肢训练"]'),
('增肌', 3, '["上肢推","下肢训练","上肢拉"]'),
('增肌', 4, '["上肢推","下肢训练","上肢拉","核心训练"]'),
('增肌', 5, '["上肢推","下肢训练","上肢拉","核心训练","全身轻量"]'),
('增肌', 6, '["上肢推","下肢训练","上肢拉","核心训练","全身轻量","恢复训练"]'),
('减脂', 1, '["全身训练"]'),
('减脂', 2, '["全身训练","核心训练"]'),
('减脂', 3, '["全身训练","核心训练","全身轻量"]'),
('减脂', 4, '["全身训练","下肢与核心","上肢训练","核心训练"]'),
('减脂', 5, '["全身训练","下肢与核心","上肢训练","核心训练","全身轻量"]'),
('减脂', 6, '["全身训练","下肢与核心","上肢训练","核心训练","全身轻量","恢复训练"]'),
('塑形', 1, '["全身训练"]'),
('塑形', 2, '["上肢训练","下肢与核心"]'),
('塑形', 3, '["上肢训练","下肢与核心","全身轻量"]'),
('塑形', 4, '["上肢训练","下肢与核心","上肢拉","核心训练"]'),
('塑形', 5, '["上肢训练","下肢与核心","上肢拉","核心训练","全身轻量"]'),
('塑形', 6, '["上肢训练","下肢与核心","上肢拉","核心训练","全身轻量","恢复训练"]'),
('提升力量', 1, '["全身训练"]'),
('提升力量', 2, '["上肢推","下肢训练"]'),
('提升力量', 3, '["上肢推","下肢训练","上肢拉"]'),
('提升力量', 4, '["上肢推","下肢训练","上肢拉","核心训练"]'),
('提升力量', 5, '["上肢推","下肢训练","上肢拉","核心训练","全身轻量"]'),
('提升力量', 6, '["上肢推","下肢训练","上肢拉","核心训练","全身轻量","恢复训练"]');

DELETE FROM daytrains;
DELETE FROM trainingparams;
DELETE FROM actiondetails;
DELETE FROM exerciseguides;

INSERT INTO daytrains (
    splitmode, actionpattern
) VALUES
('全身训练', '["水平推","下肢蹲","水平拉","核心稳定"]'),
('上肢训练', '["水平推","水平拉","垂直推","垂直拉"]'),
('下肢与核心', '["下肢蹲","髋铰链","单腿训练","核心稳定"]'),
('下肢训练', '["下肢蹲","髋铰链","单腿训练"]'),
('核心与灵活性', '["核心稳定","灵活恢复"]'),
('上肢推', '["水平推","垂直推","手臂伸展"]'),
('上肢拉', '["水平拉","垂直拉","手臂弯举"]'),
('核心训练', '["核心稳定","灵活恢复"]'),
('全身轻量', '["水平推","下肢蹲","水平拉","核心稳定"]'),
('恢复训练', '["灵活恢复","核心稳定"]');

INSERT INTO trainingparams (
    actionpattern, exlevel, minsets, maxsets, minreps, maxreps, minrestseconds, maxrestseconds
) VALUES
('水平推', '新手', 2, 3, 8, 12, 60, 90),
('垂直推', '新手', 2, 3, 8, 12, 60, 90),
('水平拉', '新手', 2, 3, 8, 12, 60, 90),
('垂直拉', '新手', 2, 3, 6, 10, 75, 105),
('下肢蹲', '新手', 2, 3, 8, 12, 75, 105),
('髋铰链', '新手', 2, 3, 8, 12, 75, 120),
('单腿训练', '新手', 2, 3, 8, 10, 60, 90),
('核心稳定', '新手', 2, 3, 20, 40, 45, 75),
('手臂弯举', '新手', 2, 3, 10, 15, 45, 75),
('手臂伸展', '新手', 2, 3, 10, 15, 45, 75),
('灵活恢复', '新手', 2, 3, 30, 60, 20, 45),
('水平推', '进阶', 3, 4, 8, 12, 60, 100),
('垂直推', '进阶', 3, 4, 8, 12, 60, 100),
('水平拉', '进阶', 3, 4, 8, 12, 60, 100),
('垂直拉', '进阶', 3, 4, 6, 12, 75, 120),
('下肢蹲', '进阶', 3, 4, 8, 12, 75, 120),
('髋铰链', '进阶', 3, 4, 8, 12, 90, 130),
('单腿训练', '进阶', 3, 4, 8, 12, 60, 100),
('核心稳定', '进阶', 3, 4, 30, 60, 45, 80),
('手臂弯举', '进阶', 3, 4, 10, 15, 45, 80),
('手臂伸展', '进阶', 3, 4, 10, 15, 45, 80),
('灵活恢复', '进阶', 2, 4, 40, 75, 20, 45),
('水平推', '熟练', 4, 5, 6, 12, 75, 120),
('垂直推', '熟练', 4, 5, 6, 12, 75, 120),
('水平拉', '熟练', 4, 5, 6, 12, 75, 120),
('垂直拉', '熟练', 4, 5, 6, 10, 90, 130),
('下肢蹲', '熟练', 4, 5, 6, 12, 90, 140),
('髋铰链', '熟练', 4, 5, 6, 12, 90, 150),
('单腿训练', '熟练', 3, 5, 8, 12, 75, 120),
('核心稳定', '熟练', 4, 5, 35, 70, 40, 80),
('手臂弯举', '熟练', 3, 5, 8, 15, 45, 80),
('手臂伸展', '熟练', 3, 5, 8, 15, 45, 80),
('灵活恢复', '熟练', 2, 4, 45, 90, 15, 40),
('水平推', '资深', 4, 6, 5, 10, 90, 150),
('垂直推', '资深', 4, 6, 5, 10, 90, 150),
('水平拉', '资深', 4, 6, 5, 10, 90, 150),
('垂直拉', '资深', 4, 6, 5, 10, 90, 150),
('下肢蹲', '资深', 4, 6, 5, 10, 100, 160),
('髋铰链', '资深', 4, 6, 5, 10, 100, 170),
('单腿训练', '资深', 4, 6, 6, 12, 90, 140),
('核心稳定', '资深', 4, 6, 40, 90, 35, 75),
('手臂弯举', '资深', 4, 5, 8, 15, 45, 90),
('手臂伸展', '资深', 4, 5, 8, 15, 45, 90),
('灵活恢复', '资深', 2, 4, 45, 90, 15, 35);

INSERT INTO actiondetails (
    actionpattern, equipment, description
) VALUES
('水平推', '徒手', '俯卧撑或跪姿俯卧撑。收紧核心，胸部主动靠近地面，推起时肩膀不要耸起。'),
('垂直推', '徒手', 'Pike 俯卧撑。臀部抬高，把发力重点放在肩部和肱三头肌，保持头颈自然。'),
('水平拉', '徒手', '桌下反向划船。先确认支撑稳固，再用肩胛后收带动胸口靠近桌沿。'),
('垂直拉', '徒手', '无单杠时用毛巾等长拉或弹力带下拉练习肩胛下沉，有单杠时做辅助引体。'),
('下肢蹲', '徒手', '徒手深蹲。脚跟踩稳，膝盖顺着脚尖方向移动，起身时臀腿同时发力。'),
('髋铰链', '徒手', '早安式髋铰链。膝盖微屈，臀部向后推，背部保持自然中立。'),
('单腿训练', '徒手', '反向箭步蹲或分腿蹲。前脚踩稳，身体垂直下降，避免膝盖内扣。'),
('核心稳定', '徒手', '平板支撑、死虫或鸟狗。重点是保持腰腹稳定，而不是追求更长时间。'),
('手臂弯举', '徒手', '毛巾自阻力弯举。用一只手提供阻力，另一只手缓慢屈肘完成动作。'),
('手臂伸展', '徒手', '椅上臂屈伸或窄距俯卧撑。肩膀远离耳朵，肘部轨迹保持稳定。'),
('灵活恢复', '徒手', '肩、胸椎、髋和踝的动态活动。动作轻柔，作为恢复日或训练后的整理。'),
('水平推', '哑铃', '哑铃地板卧推或哑铃卧推。双臂均衡发力，下放时前臂接近垂直。'),
('垂直推', '哑铃', '哑铃肩推。核心收紧，避免腰部后仰，用可控重量完成完整轨迹。'),
('水平拉', '哑铃', '单臂哑铃划船。身体支撑稳定，肘部向髋部方向拉，顶端挤压背部。'),
('下肢蹲', '哑铃', '高脚杯深蹲。哑铃贴近胸前，保持躯干稳定和脚跟受力。'),
('髋铰链', '哑铃', '哑铃罗马尼亚硬拉。重量沿大腿前侧下放，臀腿后侧有拉伸后再站起。'),
('单腿训练', '哑铃', '哑铃反向箭步蹲。负重在身体两侧，步幅稳定，左右侧完成相同组数。'),
('核心稳定', '哑铃', '负重死虫或单侧农夫行走。保持骨盆稳定，避免身体侧倾。'),
('手臂弯举', '哑铃', '哑铃弯举。上臂贴近身体，顶端停顿，下降时保持控制。'),
('手臂伸展', '哑铃', '哑铃过顶臂屈伸。肘部朝前，动作范围以肩肘舒适为准。'),
('水平推', '弹力带', '弹力带胸推。固定弹力带后向前推，注意回程也要控制。'),
('水平拉', '弹力带', '弹力带坐姿或站姿划船。先收肩胛，再拉肘部，避免耸肩。'),
('垂直拉', '弹力带', '弹力带下拉。手臂从上方向身体两侧拉下，练习背阔肌发力。'),
('下肢蹲', '弹力带', '弹力带深蹲。弹力带提供外展提示，帮助膝盖跟随脚尖。'),
('髋铰链', '弹力带', '弹力带髋铰链或拉穿。髋部向后折叠，站起时臀部主动发力。'),
('核心稳定', '弹力带', 'Pallof Press 抗旋转。弹力带从侧面拉扯，身体保持正直稳定。'),
('手臂弯举', '弹力带', '弹力带弯举。脚踩弹力带中段，屈肘时保持上臂稳定。'),
('手臂伸展', '弹力带', '弹力带下压或过顶臂屈伸。肘部固定，专注肱三头肌伸肘。'),
('灵活恢复', '弹力带', '弹力带肩部外旋、胸椎打开和髋部激活。阻力要轻，动作要慢。'),
('水平推', '杠铃', '杠铃卧推。肩胛稳定，脚踩实地面，杠铃下放到胸部可控位置。'),
('垂直推', '杠铃', '杠铃推举。核心和臀部收紧，杠铃沿接近垂直路径上推。'),
('水平拉', '杠铃', '杠铃俯身划船。髋铰链站稳，肘部向后拉，避免借腰甩动。'),
('下肢蹲', '杠铃', '杠铃深蹲。上背收紧，脚跟踩稳，深度以动作稳定为前提。'),
('髋铰链', '杠铃', '杠铃罗马尼亚硬拉。杠铃贴近身体下放，臀腿后侧主导发力。'),
('手臂弯举', '杠铃', '杠铃弯举。握距略宽于肩，上臂固定，避免身体后仰借力。'),
('垂直拉', '单杠', '引体向上或弹力带辅助引体。先肩胛下沉，再用肘部向下拉。'),
('水平拉', '单杠', '低杠反向划船。身体保持直线，胸口靠近杠，肩胛主动后收。'),
('核心稳定', '单杠', '悬垂举膝或悬垂保持。骨盆后倾，避免身体大幅摆动。'),
('水平推', '综合器械', '坐姿推胸机。座椅调到把手接近胸中部，推起和回程都保持控制。'),
('垂直推', '综合器械', '肩推机。背部贴稳靠垫，推起时不要耸肩，回程不要砸重量。'),
('水平拉', '综合器械', '坐姿划船机。先收肩胛再拉手柄，胸口保持打开。'),
('垂直拉', '综合器械', '高位下拉。握距略宽于肩，肘部向身体两侧下拉。'),
('下肢蹲', '综合器械', '腿举机。脚掌踩稳踏板，膝盖跟随脚尖，避免最低点骨盆卷起。'),
('髋铰链', '综合器械', '绳索拉穿或臀腿后侧器械。髋部主导发力，背部保持中立。'),
('核心稳定', '综合器械', '绳索抗旋转推。身体正对前方，手臂推出时躯干不要被拉偏。'),
('手臂弯举', '综合器械', '绳索弯举。肘部固定在身体两侧，保持全程张力。'),
('手臂伸展', '综合器械', '绳索下压。肩膀放松，肘部贴近身体，专注伸肘。');

INSERT INTO exerciseguides (
    actionpattern, actionname, equipment, description, steps, tips
) VALUES
('水平推', '俯卧撑', '徒手', '训练胸、肩前束和肱三头肌的基础推类动作。重点是身体保持一条直线，推起时不要耸肩。', '双手略宽于肩，手掌在胸口两侧|脚尖踩地，收紧腹部和臀部|吸气下降到胸部接近地面|呼气推起，肘部不要完全锁死', '腰不要塌，肩胛保持稳定|如果标准俯卧撑困难，可以先做跪姿俯卧撑|下降速度慢一点，比追求次数更重要'),
('垂直推', 'Pike 俯卧撑', '徒手', '偏向肩部和肱三头肌的垂直推训练，是徒手肩推的基础版本。', '双手撑地，臀部抬高形成倒 V|头部向双手之间下降|手掌推地，身体回到起始位|全程保持肩部稳定和核心收紧', '下降时肘部自然向后外侧|不要把重量全部压到脖子|难度过高可缩短动作幅度'),
('水平拉', '桌下反向划船', '徒手', '训练背部、肩胛控制和手臂拉力，是徒手水平拉的经典动作。', '仰卧在稳固桌边下方，双手抓住桌沿|身体保持直线，脚跟踩地|肩胛先向后收，再把胸口拉向桌沿|慢慢下降回到起始位', '桌子必须稳固可靠|不要耸肩拉脖子|身体越水平难度越高，可调整脚的位置'),
('垂直拉', '辅助引体向上', '徒手', '用引体向上动作理解垂直拉路径；没有单杠时可用弹力带或毛巾模拟肩胛下沉和肘部下拉。', '先让肩膀远离耳朵，肩胛向下|肘部沿身体两侧向下拉，不要耸肩|有单杠时做辅助引体向上或离心下放|无单杠时用弹力带下拉或毛巾等长拉保持张力', '先找背阔肌收紧和肩胛下沉感觉|不要用腰后仰代偿|动作质量优先，宁可减少次数'),
('下肢蹲', '徒手深蹲', '徒手', '训练股四头肌、臀部和基础下肢控制能力。适合大多数训练计划作为下肢主动作。', '双脚与肩同宽，脚尖自然外展|臀部向后向下坐，膝盖跟随脚尖方向|下蹲到能稳定控制的位置|脚跟踩稳，呼气站起并收紧臀部', '膝盖不要内扣|背部保持自然挺直|宁可深度浅一点，也不要失去控制'),
('髋铰链', '髋铰链 / 罗马尼亚硬拉', '徒手', '练习髋关节主导的发力模式，强化臀腿后侧和硬拉动作基础。', '双脚与髋同宽，双手可放胸前|膝盖微屈，臀部向后推|背部保持中立，躯干前倾|感到臀腿后侧拉伸后，用臀部带动站起', '不是弯腰，而是髋向后折叠|全程保持背部自然平直|先练动作轨迹，再增加负重'),
('单腿训练', '反向箭步蹲', '徒手', '训练单侧下肢力量、平衡和髋膝控制，适合补足左右差异。', '自然站立，向后迈一步|身体垂直下降，前脚脚跟踩稳|前腿发力回到起始位|左右两侧完成相同次数', '步幅不要过小|前膝跟随脚尖方向|先扶墙保持平衡也可以'),
('核心稳定', '平板支撑', '徒手', '训练腹部抗伸展能力，帮助稳定腰椎和提升整体动作质量。', '手肘位于肩膀正下方|脚尖踩地，身体从头到脚成一直线|收紧腹部和臀部|保持均匀呼吸直到规定时间', '不要塌腰或撅臀|脖子保持自然，不要抬头|腰酸明显时先缩短时间'),
('手臂弯举', '毛巾自阻力弯举', '徒手', '用毛巾或双手对抗制造阻力，训练肱二头肌控制。', '双手抓住毛巾两端，一手提供向下阻力|另一手做弯举动作，上臂尽量贴近身体|顶端停顿一秒|慢慢放下并保持张力，左右手轮换', '阻力由自己控制，不要突然发力|上臂尽量固定在身体两侧|左右手都要完成同样组数'),
('手臂伸展', '椅上臂屈伸', '徒手', '训练肱三头肌和肩部稳定能力，适合作为上肢推训练的辅助动作。', '双手撑在稳固椅沿，手指朝前|双脚踩地，身体靠近椅子|肘部向后弯曲让身体下降|手掌发力推回起始位', '肩膀远离耳朵|下降幅度以肩部舒服为准|椅子必须稳固，避免滑动'),
('灵活恢复', '肩髋动态活动', '徒手', '用于恢复日或训练结束后的整理，改善肩、胸椎、髋和踝的活动感。', '做肩绕环和胸椎旋转各 30 秒|做髋屈伸动态拉伸各 30 秒|做踝关节前后活动各 30 秒|全程保持轻松呼吸', '恢复训练不追求酸痛|动作范围逐渐打开|任何关节刺痛都应停止该动作'),
('水平推', '哑铃地板卧推', '哑铃', '用哑铃完成水平推，地板版本更容易控制肩部范围。', '仰卧，双脚踩地|哑铃位于胸口两侧，前臂接近垂直|呼气推起到手臂接近伸直|缓慢下放到上臂接触地面', '手腕保持中立|左右重量同步|不要用腰部拱起代偿'),
('垂直推', '哑铃肩推', '哑铃', '用哑铃完成垂直推训练，重点是肩部稳定、核心收紧和手臂向上推举路径。', '坐姿或站姿保持躯干稳定|哑铃位于肩膀两侧，手腕保持中立|呼气向上推起到手臂接近伸直|缓慢下放回肩部高度，保持控制', '不要耸肩顶脖子|腰不要过度后仰|重量宁轻勿乱，先保证轨迹稳定'),
('水平拉', '单臂哑铃划船', '哑铃', '强化背部和肩胛控制，适合与卧推动作配对。', '一手扶稳支撑面，背部保持平直|另一手持哑铃自然下垂|肘部向髋部方向拉起|顶端停顿后缓慢下放', '不要耸肩|躯干不要旋转过多|先收肩胛再拉手臂'),
('下肢蹲', '高脚杯深蹲', '哑铃', '用单只哑铃增加深蹲负荷，同时帮助躯干保持直立。', '双手托住哑铃靠近胸前|双脚略宽于髋，脚尖自然外展|臀部向下坐到稳定深度|脚跟发力站起', '哑铃贴近身体|膝盖跟随脚尖|不要在底部放松核心'),
('髋铰链', '哑铃罗马尼亚硬拉', '哑铃', '训练臀腿后侧和髋铰链能力，是下肢后链的基础动作。', '双手持哑铃放在大腿前侧|膝盖微屈，臀部向后推|哑铃沿腿部下放|臀部发力带动身体站起', '背部保持中立|哑铃不要离身体太远|不要追求过低深度'),
('单腿训练', '哑铃反向箭步蹲', '哑铃', '在徒手箭步蹲基础上增加负重，提升单侧稳定和力量。', '双手持哑铃自然下垂|一侧向后撤步|身体垂直下降后站起|左右两侧交替完成', '前脚踩稳|身体不要前后晃动|先用轻重量熟悉节奏'),
('手臂弯举', '哑铃弯举', '哑铃', '训练肱二头肌和屈肘控制，适合作为上肢拉训练后的辅助动作。', '双手持哑铃自然下垂|上臂贴近身体两侧|屈肘把哑铃举到肩前|顶端停顿后慢慢下放', '不要借腰后仰|手腕保持稳定|下放阶段也要控制'),
('手臂伸展', '哑铃过顶臂屈伸', '哑铃', '训练肱三头肌长头，适合上肢推训练后的辅助动作。', '双手持一只哑铃举过头顶|肘部朝前并尽量固定|弯曲肘部让哑铃下降到头后|伸肘回到起始位', '重量不要过大|肩部不适时缩小幅度|动作全程慢一点'),
('水平推', '弹力带胸推', '弹力带', '弹力带提供渐增阻力，适合家庭训练中的水平推。', '把弹力带固定在身后胸口高度|双手握住两端，身体站稳|向前推到手臂接近伸直|缓慢回到起始位', '固定点必须可靠|回程不要被弹力带拉回|肩膀不要耸起'),
('水平拉', '弹力带划船', '弹力带', '训练背部和肩胛后收，适合替代器械划船。', '弹力带固定在前方|双手握住两端，手臂伸直|先收肩胛，再把肘部拉向身体两侧|慢慢还原', '身体保持直立|不要耸肩|顶端停顿一秒'),
('垂直拉', '弹力带下拉', '弹力带', '模拟高位下拉路径，帮助学习背阔肌发力。', '弹力带固定在头顶上方|双手握住两端|肩胛下沉，肘部向下拉|缓慢回到起始位', '不要后仰借力|肘部向身体两侧靠近|保持颈部放松'),
('核心稳定', 'Pallof Press 抗旋转', '弹力带', '训练核心抗旋转能力，适合保护躯干稳定。', '弹力带固定在身体侧面|双手握住弹力带放在胸前|手臂向前推出并保持身体不转动|收回到胸前重复', '骨盆和胸口保持正对前方|阻力不要大到身体变形|左右两侧都要做'),
('手臂弯举', '弹力带弯举', '弹力带', '用弹力带训练肱二头肌，全程保持张力。', '脚踩弹力带中段|双手握住两端|上臂固定，屈肘向上弯举|慢慢下放到起始位', '脚下固定要稳|不要甩动身体|顶端停顿一秒'),
('手臂伸展', '弹力带下压', '弹力带', '用弹力带训练肱三头肌伸肘能力。', '弹力带固定在头顶上方|肘部贴近身体两侧|向下伸肘直到手臂接近伸直|慢慢回到起始位', '肘部不要前后摆动|肩膀保持放松|回程也要控制'),
('水平推', '杠铃卧推', '杠铃', '经典水平推力量动作，适合有基础和保护条件的训练者。', '躺在卧推凳上，脚踩实地面|肩胛后收下沉，握距略宽于肩|杠铃下放到胸部可控位置|向上推回起始位', '需要安全架或保护者|不要弹胸|肩部不适时降低重量或换动作'),
('水平拉', '杠铃俯身划船', '杠铃', '训练背部厚度和髋铰链稳定能力。', '双脚与髋同宽，髋铰链前倾|杠铃自然下垂|肘部向后拉，杠铃靠近下腹|缓慢下放', '背部保持中立|不要用腰甩动|重量以能控制为准'),
('下肢蹲', '杠铃深蹲', '杠铃', '下肢主力量动作，训练股四头肌、臀部和整体稳定。', '杠铃置于上背稳定位置|双脚站稳，核心收紧|臀部向下坐到可控深度|脚跟发力站起', '需要熟悉架上取杠|膝盖跟随脚尖|动作变形时先降重量'),
('髋铰链', '杠铃罗马尼亚硬拉', '杠铃', '强化臀腿后侧，是髋铰链的主要负重版本。', '双手握杠，杠铃贴近大腿|膝盖微屈，臀部向后推|杠铃沿腿部下放|臀部发力站起', '背部保持中立|杠铃不要离身|不要用下背硬拉'),
('手臂弯举', '杠铃弯举', '杠铃', '适合有基础的手臂弯举版本，容易统一左右负荷。', '双手反握或正握杠铃站立|上臂贴近身体|屈肘把杠铃举到胸前|慢慢下放', '不要后仰借力|手腕保持自然|重量不能影响动作轨迹'),
('垂直拉', '引体向上', '单杠', '训练背阔肌、上背和手臂拉力，是垂直拉的代表动作。', '双手握住单杠，身体悬垂|肩胛先下沉|肘部向下拉，让胸口靠近杠|缓慢下放到可控位置', '可以用弹力带辅助|不要用摆动借力|先保证肩胛控制'),
('水平拉', '低杠反向划船', '单杠', '用较低单杠完成水平拉，难度可通过身体角度调整。', '双手握住低杠，身体在杠下保持直线|脚跟踩地|胸口拉向单杠|慢慢下降回起始位', '杠必须固定可靠|身体越水平越难|不要耸肩拉脖子'),
('核心稳定', '悬垂举膝', '单杠', '训练核心抗摆动和髋屈控制，适合有一定握力基础的训练者。', '双手握单杠自然悬垂|先稳定肩胛和身体摆动|屈髋屈膝把膝盖抬起|缓慢放下回到悬垂', '不要大幅摆腿|握力不足时先练悬垂保持|腰部不适时换成平板支撑'),
('水平推', '坐姿推胸机', '综合器械', '稳定器械版本的水平推，适合学习胸肩推的发力路径。', '调整座椅让把手接近胸中部|背部贴稳靠垫|向前推到手臂接近伸直|慢慢回到起始位', '不要耸肩|回程不要让重量片碰撞|左右手同步发力'),
('垂直拉', '高位下拉', '综合器械', '用器械训练垂直拉，适合不能完成引体向上的用户。', '握住下拉杆，身体坐稳|肩胛下沉后肘部向下拉|下拉到上胸附近|缓慢上放到手臂伸展', '不要大幅后仰|脖子保持放松|优先感受背部发力'),
('水平拉', '坐姿划船机', '综合器械', '训练水平拉和肩胛后收，动作路径清晰稳定。', '双脚踩稳，膝盖微屈|手臂伸直，背部保持直立|先收肩胛，再拉手柄靠近腹部|慢慢还原', '不要含胸|不要用下背猛拉|顶端停顿一秒'),
('下肢蹲', '腿举机', '综合器械', '器械下肢推蹬动作，适合增加腿部训练容量。', '背部和臀部贴稳靠垫|双脚踩在踏板中部|缓慢下放到可控深度|脚掌均匀发力推回', '膝盖跟随脚尖|最低点不要骨盆卷起|不要锁死膝盖'),
('核心稳定', '绳索抗旋转推', '综合器械', '训练核心抗旋转能力，适合提升躯干稳定。', '绳索调到胸口高度，身体侧对器械|双手握把放在胸前|向前推出并保持躯干不转|收回后重复，左右换边', '重量不宜过大|骨盆保持正对前方|动作慢而稳'),
('手臂伸展', '绳索下压', '综合器械', '肱三头肌孤立训练，适合作为上肢推辅助。', '站在绳索前，肘部贴近身体|握住绳索或直杆|向下伸肘到手臂接近伸直|慢慢回到起始位', '肩膀不要耸起|肘部不要前后摆动|全程保持张力'),
('核心稳定', '负重死虫', '哑铃', '用轻哑铃增加核心稳定挑战，适合已经能稳定完成死虫动作的用户。', '仰卧，双手持轻哑铃举向天花板|腰腹收紧，保持下背稳定|一侧腿缓慢伸出再收回|左右交替完成规定次数', '重量要轻，腰不能离地|动作慢于次数|脖子和肩膀保持放松'),
('下肢蹲', '弹力带深蹲', '弹力带', '弹力带提供外展提示，帮助深蹲时膝盖更稳定。', '弹力带套在膝盖上方|双脚与肩同宽站稳|下蹲时膝盖轻轻向外顶住弹力带|脚跟发力站起', '不要让膝盖被弹力带拉向内侧|阻力不宜过大|保持躯干稳定'),
('髋铰链', '弹力带拉穿', '弹力带', '用弹力带学习髋部后移和臀部发力，适合家庭训练后链。', '弹力带固定在身后较低位置|双脚站稳，双手握住弹力带|臀部向后推形成髋铰链|臀部发力站起并夹紧', '动作来自髋部，不是弯腰|背部保持中立|站起时不要过度后仰'),
('灵活恢复', '弹力带肩髋激活', '弹力带', '用轻阻力做肩部外旋、髋部激活和动态拉伸，适合恢复日。', '弹力带肩外旋 30 秒|弹力带拉伸胸背 30 秒|弹力带侧向走或髋外展 30 秒|放慢节奏并保持呼吸', '阻力要轻|不要追求疲劳感|关节不适时减少幅度'),
('垂直推', '杠铃推举', '杠铃', '杠铃垂直推训练肩部、肱三头肌和躯干稳定，适合有基础的用户。', '杠铃放在锁骨前方，手腕保持稳定|核心和臀部收紧|向上推起，让杠铃经过面前后到头顶|缓慢下放回起始位', '不要用腰后仰代偿|杠铃路径接近垂直|重量必须可控'),
('垂直推', '肩推机', '综合器械', '器械版本的垂直推，路径稳定，适合学习肩部推举。', '调整座椅，让把手在肩部附近|背部贴稳靠垫|向上推到手臂接近伸直|缓慢回到起始位', '不要耸肩|回程不要砸重量|肩部不适时缩小幅度'),
('髋铰链', '绳索拉穿', '综合器械', '绳索拉穿用于训练髋铰链和臀部发力，动作压力相对可控。', '绳索调到低位，背对器械站立|双手从胯下握住绳索|臀部向后推，躯干前倾|臀部发力站起', '手臂只是连接绳索|背部保持中立|站起时不要过度后仰'),
('手臂弯举', '绳索弯举', '综合器械', '绳索弯举能保持持续张力，适合作为上肢拉训练后的辅助动作。', '面对绳索站稳，握住直杆或绳索|上臂贴近身体两侧|屈肘把握把拉向胸前|缓慢下放到手臂接近伸直', '不要用身体晃动借力|肘部位置固定|下放阶段也保持张力');

UPDATE exerciseguides SET primarymuscles = '胸大肌、三角肌前束、肱三头肌', secondarymuscles = '核心、前锯肌、肩胛稳定肌', difficulty = '新手', contraindications = '肩、肘、手腕疼痛明显时先改跪姿或上斜版本。', commonmistakes = '塌腰、耸肩、手掌离身体过远、下降过快。', suitablefor = '需要建立基础上肢推力、核心控制和家庭训练习惯的用户。', alternatives = '跪姿俯卧撑、上斜俯卧撑、弹力带胸推、哑铃地板卧推。' WHERE actionname = '俯卧撑' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '三角肌前束、三角肌中束、肱三头肌', secondarymuscles = '上胸、斜方肌下束、核心', difficulty = '进阶', contraindications = '肩部上举疼痛、腕关节不适或高血压不适者先避开倒置姿势。', commonmistakes = '头部直线下砸、腰背过度弯曲、肘部过度外展、重量压到脖子。', suitablefor = '已经能稳定俯卧撑并希望提升徒手肩推能力的用户。', alternatives = '哑铃肩推、杠铃推举、肩推机、斜板 Pike 俯卧撑。' WHERE actionname = 'Pike 俯卧撑' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '背阔肌、菱形肌、斜方肌中束', secondarymuscles = '肱二头肌、后三角肌、核心', difficulty = '新手', contraindications = '桌面不稳、握力不足或肩部拉扯痛时不要执行。', commonmistakes = '桌子晃动、耸肩、身体塌腰、只用手臂硬拉。', suitablefor = '缺少器械但需要补足水平拉和肩胛后收能力的用户。', alternatives = '低杠反向划船、弹力带划船、单臂哑铃划船、坐姿划船机。' WHERE actionname = '桌下反向划船' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '背阔肌、大圆肌、肱二头肌', secondarymuscles = '下斜方肌、握力、核心', difficulty = '进阶', contraindications = '肩部上举疼痛、肘部不适或握力无法维持时先用下拉替代。', commonmistakes = '耸肩起拉、身体摆动、只弯手臂、离心下放失控。', suitablefor = '正在学习引体向上路径或需要提升垂直拉能力的用户。', alternatives = '弹力带下拉、高位下拉、引体向上离心、单杠悬垂。' WHERE actionname = '辅助引体向上' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '股四头肌、臀大肌', secondarymuscles = '腘绳肌、小腿、核心', difficulty = '新手', contraindications = '膝髋疼痛明显或下蹲无法保持平衡时先缩小幅度。', commonmistakes = '膝盖内扣、脚跟离地、含胸弯腰、底部放松核心。', suitablefor = '需要建立下肢基础力量、髋膝控制和日常蹲起能力的用户。', alternatives = '箱式深蹲、高脚杯深蹲、弹力带深蹲、腿举机。' WHERE actionname = '徒手深蹲' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '臀大肌、腘绳肌', secondarymuscles = '竖脊肌、核心、背阔肌', difficulty = '新手', contraindications = '下背疼痛、无法保持脊柱中立或后侧链拉伸刺痛时停止。', commonmistakes = '弯腰代替髋折叠、膝盖过度前移、站起时腰后仰。', suitablefor = '需要学习硬拉基础轨迹和臀腿后侧发力的用户。', alternatives = '弹力带拉穿、哑铃罗马尼亚硬拉、绳索拉穿、杠铃罗马尼亚硬拉。' WHERE actionname = '髋铰链 / 罗马尼亚硬拉' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '股四头肌、臀大肌、臀中肌', secondarymuscles = '腘绳肌、小腿、核心', difficulty = '新手', contraindications = '膝踝不稳、平衡差或前膝疼痛时先扶墙降低幅度。', commonmistakes = '后撤步过短、前膝内扣、身体前后晃动、后脚蹬地过多。', suitablefor = '需要改善单侧下肢力量、左右差异和平衡控制的用户。', alternatives = '原地分腿蹲、台阶上步、哑铃反向箭步蹲、腿举机单腿版本。' WHERE actionname = '反向箭步蹲' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '腹横肌、腹直肌、腹斜肌', secondarymuscles = '臀大肌、肩胛稳定肌、股四头肌', difficulty = '新手', contraindications = '腰痛明显、肩肘支撑不适或无法控制骨盆时先缩短时间。', commonmistakes = '塌腰、撅臀、憋气、头部抬太高、肩膀耸起。', suitablefor = '需要提升躯干抗伸展、基础核心稳定和动作控制的用户。', alternatives = '跪姿平板、死虫、鸟狗、Pallof Press 抗旋转。' WHERE actionname = '平板支撑' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '肱二头肌、肱肌', secondarymuscles = '前臂屈肌、肩部稳定肌', difficulty = '新手', contraindications = '肘腕疼痛或无法稳定控制阻力时减少对抗力度。', commonmistakes = '阻力突然变化、上臂离开身体、耸肩、下放完全放松。', suitablefor = '没有器械但想补足手臂屈肘力量和肌肉感知的用户。', alternatives = '弹力带弯举、哑铃弯举、杠铃弯举、绳索弯举。' WHERE actionname = '毛巾自阻力弯举' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '肱三头肌、三角肌前束', secondarymuscles = '胸大肌下束、肩胛稳定肌', difficulty = '进阶', contraindications = '肩前侧夹挤感、肘痛或腕部支撑不适时改用下压类动作。', commonmistakes = '身体离椅子太远、肩膀耸起、下降过深、椅子不稳。', suitablefor = '需要徒手强化肱三头肌和推类辅助力量的用户。', alternatives = '窄距俯卧撑、弹力带下压、哑铃过顶臂屈伸、绳索下压。' WHERE actionname = '椅上臂屈伸' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '肩关节活动肌群、髋屈肌、臀中肌', secondarymuscles = '胸椎旋转肌群、踝关节稳定肌、核心', difficulty = '新手', contraindications = '关节刺痛、急性扭伤或拉伸疼痛明显时停止。', commonmistakes = '动作过快、追求酸痛、呼吸屏住、左右活动范围差异过大仍强压。', suitablefor = '训练前热身、训练后整理、久坐后恢复和轻量恢复日。', alternatives = '肩绕环、胸椎开书、髋屈伸动态拉伸、弹力带肩髋激活。' WHERE actionname = '肩髋动态活动' AND equipment = '徒手';
UPDATE exerciseguides SET primarymuscles = '胸大肌、肱三头肌', secondarymuscles = '三角肌前束、肩胛稳定肌、核心', difficulty = '新手', contraindications = '肩部卧推疼痛或肘部触地不适时减少幅度或换弹力带胸推。', commonmistakes = '手肘过度外展、手腕后折、左右不同步、腰部过度拱起。', suitablefor = '有哑铃且希望安全学习卧推动作路径的用户。', alternatives = '俯卧撑、哑铃卧推、弹力带胸推、坐姿推胸机。' WHERE actionname = '哑铃地板卧推' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '三角肌前束、三角肌中束、肱三头肌', secondarymuscles = '上胸、核心、斜方肌下束', difficulty = '进阶', contraindications = '肩部撞击感、腰椎代偿明显或无法稳定举过头顶时降低重量。', commonmistakes = '腰后仰、耸肩、哑铃轨迹过宽、下放失控。', suitablefor = '需要提升肩部力量、左右稳定和垂直推能力的用户。', alternatives = 'Pike 俯卧撑、杠铃推举、肩推机、地雷管推举。' WHERE actionname = '哑铃肩推' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '背阔肌、菱形肌、斜方肌中束', secondarymuscles = '后三角肌、肱二头肌、核心抗旋转肌群', difficulty = '新手', contraindications = '下背无法稳定、支撑侧肩腕疼痛或躯干旋转失控时降低重量。', commonmistakes = '耸肩、拉向胸口、身体旋转过多、哑铃下放太快。', suitablefor = '需要补足单侧背部力量、肩胛控制和左右差异的用户。', alternatives = '弹力带划船、坐姿划船机、杠铃俯身划船、低杠反向划船。' WHERE actionname = '单臂哑铃划船' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '股四头肌、臀大肌', secondarymuscles = '内收肌、腘绳肌、核心', difficulty = '新手', contraindications = '膝髋疼痛、无法保持躯干直立或负重导致下背不适时降低重量。', commonmistakes = '哑铃离身体太远、膝盖内扣、脚跟离地、底部松掉核心。', suitablefor = '需要从徒手深蹲过渡到负重深蹲的用户。', alternatives = '徒手深蹲、弹力带深蹲、杠铃深蹲、腿举机。' WHERE actionname = '高脚杯深蹲' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '腘绳肌、臀大肌', secondarymuscles = '竖脊肌、背阔肌、前臂握力', difficulty = '进阶', contraindications = '下背疼痛、腘绳肌拉伤恢复期或无法保持中立位时先练徒手髋铰链。', commonmistakes = '哑铃离腿太远、膝盖锁死、追求过低深度、站起时后仰。', suitablefor = '需要强化臀腿后侧、硬拉基础和髋铰链负重能力的用户。', alternatives = '徒手髋铰链、弹力带拉穿、绳索拉穿、杠铃罗马尼亚硬拉。' WHERE actionname = '哑铃罗马尼亚硬拉' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '股四头肌、臀大肌、臀中肌', secondarymuscles = '腘绳肌、小腿、核心', difficulty = '进阶', contraindications = '膝踝不稳、平衡不足或负重后膝痛时先做徒手版本。', commonmistakes = '步幅忽大忽小、身体前倾、前脚跟离地、左右重量不均。', suitablefor = '需要提升单侧负重能力、髋膝稳定和下肢训练容量的用户。', alternatives = '反向箭步蹲、保加利亚分腿蹲、台阶上步、腿举机单腿版本。' WHERE actionname = '哑铃反向箭步蹲' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '肱二头肌、肱肌', secondarymuscles = '肱桡肌、前臂屈肌、肩部稳定肌', difficulty = '新手', contraindications = '肘腕疼痛或肩部代偿明显时降低重量或改弹力带。', commonmistakes = '身体后仰借力、肘部前移、下放太快、手腕弯折。', suitablefor = '需要补足手臂围度、屈肘力量和上肢拉辅助能力的用户。', alternatives = '弹力带弯举、杠铃弯举、绳索弯举、毛巾自阻力弯举。' WHERE actionname = '哑铃弯举' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '肱三头肌长头', secondarymuscles = '肱三头肌外侧头、核心、肩部稳定肌', difficulty = '进阶', contraindications = '肩部上举不适、肘痛或颈肩紧张明显时换下压动作。', commonmistakes = '肘部外翻、腰后仰、哑铃下降过深、动作速度过快。', suitablefor = '需要重点加强肱三头肌长头和推类辅助力量的用户。', alternatives = '弹力带下压、绳索下压、椅上臂屈伸、窄距俯卧撑。' WHERE actionname = '哑铃过顶臂屈伸' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '胸大肌、三角肌前束、肱三头肌', secondarymuscles = '前锯肌、核心、肩胛稳定肌', difficulty = '新手', contraindications = '固定点不稳、肩前侧疼痛或弹力带老化时不要执行。', commonmistakes = '固定点过低或过高、回程被拉回、耸肩、身体前倾借力。', suitablefor = '家庭训练中需要可调阻力水平推的用户。', alternatives = '俯卧撑、哑铃地板卧推、杠铃卧推、坐姿推胸机。' WHERE actionname = '弹力带胸推' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '背阔肌、菱形肌、斜方肌中束', secondarymuscles = '后三角肌、肱二头肌、核心', difficulty = '新手', contraindications = '固定点不可靠、肩部拉扯痛或弹力带回弹失控时停止。', commonmistakes = '耸肩、身体后仰借力、手腕折叠、回程完全放松。', suitablefor = '家庭训练中需要补足水平拉和体态改善的用户。', alternatives = '桌下反向划船、单臂哑铃划船、低杠反向划船、坐姿划船机。' WHERE actionname = '弹力带划船' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '背阔肌、大圆肌', secondarymuscles = '肱二头肌、下斜方肌、核心', difficulty = '新手', contraindications = '固定点不稳、肩部上举疼痛或颈肩紧张明显时降低阻力。', commonmistakes = '后仰借力、耸肩、肘部向后乱跑、回程失控。', suitablefor = '无法做引体向上但需要学习垂直拉发力的用户。', alternatives = '辅助引体向上、高位下拉、单杠悬垂、引体向上离心。' WHERE actionname = '弹力带下拉' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '腹斜肌、腹横肌', secondarymuscles = '臀中肌、肩胛稳定肌、胸大肌', difficulty = '新手', contraindications = '腰痛急性期、站姿无法稳定或固定点不可靠时先降低阻力。', commonmistakes = '身体跟着旋转、骨盆偏移、手臂推出过快、耸肩。', suitablefor = '需要提升核心抗旋转、保护腰背和改善左右稳定的用户。', alternatives = '死虫、平板支撑、绳索抗旋转推、鸟狗。' WHERE actionname = 'Pallof Press 抗旋转' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '肱二头肌、肱肌', secondarymuscles = '肱桡肌、前臂屈肌', difficulty = '新手', contraindications = '脚下固定不稳、肘腕疼痛或弹力带回弹难控制时降低阻力。', commonmistakes = '身体晃动、肘部前移、顶端耸肩、下放失控。', suitablefor = '需要低门槛补足手臂训练和全程张力控制的用户。', alternatives = '哑铃弯举、杠铃弯举、绳索弯举、毛巾自阻力弯举。' WHERE actionname = '弹力带弯举' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '肱三头肌外侧头、内侧头', secondarymuscles = '肩部稳定肌、前臂', difficulty = '新手', contraindications = '肘部疼痛、固定点不稳或肩颈紧张明显时降低阻力。', commonmistakes = '肘部前后摆动、肩膀耸起、身体下压借力、回程过快。', suitablefor = '家庭训练中需要安全补足肱三头肌和推类辅助力量的用户。', alternatives = '绳索下压、哑铃过顶臂屈伸、窄距俯卧撑、椅上臂屈伸。' WHERE actionname = '弹力带下压' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '胸大肌、肱三头肌、三角肌前束', secondarymuscles = '背阔肌稳定、前锯肌、核心', difficulty = '熟练', contraindications = '肩部不适、无安全架或无人保护时不要冲击大重量。', commonmistakes = '肩胛松散、弹胸、手腕后折、杠铃路径漂移。', suitablefor = '有基础并具备安全保护条件、需要提升水平推力量的用户。', alternatives = '哑铃地板卧推、坐姿推胸机、俯卧撑、弹力带胸推。' WHERE actionname = '杠铃卧推' AND equipment = '杠铃';
UPDATE exerciseguides SET primarymuscles = '背阔肌、菱形肌、斜方肌中下束', secondarymuscles = '腘绳肌、臀大肌、竖脊肌、肱二头肌', difficulty = '熟练', contraindications = '下背疼痛、髋铰链无法稳定或腘绳肌拉伤恢复期先避开。', commonmistakes = '腰背弓起、用髋部猛甩、杠铃离身体太远、耸肩。', suitablefor = '有基础并需要提升背部厚度和后链稳定的用户。', alternatives = '单臂哑铃划船、坐姿划船机、弹力带划船、低杠反向划船。' WHERE actionname = '杠铃俯身划船' AND equipment = '杠铃';
UPDATE exerciseguides SET primarymuscles = '股四头肌、臀大肌', secondarymuscles = '内收肌、腘绳肌、竖脊肌、核心', difficulty = '熟练', contraindications = '无深蹲架、安全保护不足、膝髋腰疼痛或动作模式不稳定时不要负重。', commonmistakes = '膝盖内扣、下背放松、重心前移、底部反弹。', suitablefor = '有基础并需要系统提升下肢最大力量和全身稳定的用户。', alternatives = '高脚杯深蹲、腿举机、弹力带深蹲、箱式深蹲。' WHERE actionname = '杠铃深蹲' AND equipment = '杠铃';
UPDATE exerciseguides SET primarymuscles = '腘绳肌、臀大肌', secondarymuscles = '竖脊肌、背阔肌、握力、核心', difficulty = '熟练', contraindications = '下背疼痛、髋铰链不熟或无法让杠铃贴身时降低重量。', commonmistakes = '杠铃离腿、膝盖锁死、背部弯曲、追求过低深度。', suitablefor = '需要系统强化臀腿后侧、硬拉基础和后链力量的用户。', alternatives = '哑铃罗马尼亚硬拉、绳索拉穿、弹力带拉穿、徒手髋铰链。' WHERE actionname = '杠铃罗马尼亚硬拉' AND equipment = '杠铃';
UPDATE exerciseguides SET primarymuscles = '肱二头肌、肱肌', secondarymuscles = '肱桡肌、前臂屈肌、核心抗摆动', difficulty = '进阶', contraindications = '肘腕疼痛或重量导致身体明显后仰时降低重量。', commonmistakes = '甩腰借力、肘部漂移、手腕折叠、下放半程。', suitablefor = '有基础并希望统一左右负荷、提升手臂力量的用户。', alternatives = '哑铃弯举、弹力带弯举、绳索弯举、牧师凳弯举。' WHERE actionname = '杠铃弯举' AND equipment = '杠铃';
UPDATE exerciseguides SET primarymuscles = '背阔肌、大圆肌、肱二头肌', secondarymuscles = '下斜方肌、菱形肌、握力、核心', difficulty = '熟练', contraindications = '肩部上举疼痛、肘痛或握力无法维持悬垂时先用辅助版本。', commonmistakes = '摆动借力、耸肩、下巴前伸、下放不完整。', suitablefor = '具备基础拉力并希望提升自重背部力量的用户。', alternatives = '辅助引体向上、弹力带下拉、高位下拉、离心引体。' WHERE actionname = '引体向上' AND equipment = '单杠';
UPDATE exerciseguides SET primarymuscles = '背阔肌、菱形肌、斜方肌中束', secondarymuscles = '后三角肌、肱二头肌、核心', difficulty = '新手', contraindications = '单杠固定不稳、肩部拉扯痛或握力不足时不要执行。', commonmistakes = '身体松散、耸肩、胸口不靠近杠、下降失控。', suitablefor = '需要比桌下划船更稳定、更可调难度的自重水平拉用户。', alternatives = '桌下反向划船、弹力带划船、单臂哑铃划船、坐姿划船机。' WHERE actionname = '低杠反向划船' AND equipment = '单杠';
UPDATE exerciseguides SET primarymuscles = '腹直肌下部、髂腰肌', secondarymuscles = '腹斜肌、握力、背阔肌稳定', difficulty = '进阶', contraindications = '握力不足、肩部悬垂不适或腰椎屈曲疼痛时改地面核心动作。', commonmistakes = '大幅摆腿、耸肩、用惯性抬腿、下放失控。', suitablefor = '有悬垂基础并需要提升核心抗摆动和髋屈控制的用户。', alternatives = '平板支撑、负重死虫、仰卧举腿、Pallof Press 抗旋转。' WHERE actionname = '悬垂举膝' AND equipment = '单杠';
UPDATE exerciseguides SET primarymuscles = '胸大肌、肱三头肌、三角肌前束', secondarymuscles = '前锯肌、肩胛稳定肌', difficulty = '新手', contraindications = '肩部推举疼痛或器械轨迹不适合身体比例时调整座椅或换动作。', commonmistakes = '座椅高度错误、耸肩、回程砸重量、左右发力不均。', suitablefor = '需要稳定轨迹学习水平推或控制训练容量的用户。', alternatives = '俯卧撑、弹力带胸推、哑铃地板卧推、杠铃卧推。' WHERE actionname = '坐姿推胸机' AND equipment = '综合器械';
UPDATE exerciseguides SET primarymuscles = '背阔肌、大圆肌', secondarymuscles = '肱二头肌、下斜方肌、菱形肌', difficulty = '新手', contraindications = '肩部上举疼痛、颈肩紧张明显或下拉到颈后时停止调整。', commonmistakes = '大幅后仰、拉到颈后、耸肩、放回过快。', suitablefor = '无法完成引体向上但需要稳定训练垂直拉的用户。', alternatives = '弹力带下拉、辅助引体向上、引体向上、单杠悬垂。' WHERE actionname = '高位下拉' AND equipment = '综合器械';
UPDATE exerciseguides SET primarymuscles = '背阔肌、菱形肌、斜方肌中束', secondarymuscles = '后三角肌、肱二头肌、竖脊肌', difficulty = '新手', contraindications = '下背不适、肩部拉扯痛或器械座位无法调稳时降低重量。', commonmistakes = '含胸、用下背猛拉、耸肩、手柄回放撞击。', suitablefor = '需要稳定轨迹训练水平拉、改善体态和背部控制的用户。', alternatives = '弹力带划船、单臂哑铃划船、低杠反向划船、杠铃俯身划船。' WHERE actionname = '坐姿划船机' AND equipment = '综合器械';
UPDATE exerciseguides SET primarymuscles = '股四头肌、臀大肌', secondarymuscles = '腘绳肌、内收肌、小腿', difficulty = '新手', contraindications = '膝髋疼痛、腰椎无法贴稳靠垫或器械调节不合适时降低深度。', commonmistakes = '最低点骨盆卷起、膝盖内扣、锁死膝盖、脚掌位置过低。', suitablefor = '需要增加下肢训练容量但暂不适合自由深蹲负重的用户。', alternatives = '高脚杯深蹲、杠铃深蹲、弹力带深蹲、徒手深蹲。' WHERE actionname = '腿举机' AND equipment = '综合器械';
UPDATE exerciseguides SET primarymuscles = '腹斜肌、腹横肌', secondarymuscles = '臀中肌、肩胛稳定肌、胸大肌', difficulty = '新手', contraindications = '腰痛急性期、站姿无法稳定或重量导致身体旋转时降低重量。', commonmistakes = '骨盆跟着转、手臂推出过快、耸肩、重量过大。', suitablefor = '需要更稳定负荷训练核心抗旋转和躯干控制的用户。', alternatives = 'Pallof Press 抗旋转、死虫、平板支撑、鸟狗。' WHERE actionname = '绳索抗旋转推' AND equipment = '综合器械';
UPDATE exerciseguides SET primarymuscles = '肱三头肌外侧头、内侧头', secondarymuscles = '前臂、肩部稳定肌', difficulty = '新手', contraindications = '肘痛、腕部不适或肩膀无法放松时降低重量或换弹力带。', commonmistakes = '肘部离身、身体下压借力、肩膀耸起、回程失控。', suitablefor = '需要稳定孤立训练肱三头肌、补足推类辅助力量的用户。', alternatives = '弹力带下压、哑铃过顶臂屈伸、窄距俯卧撑、椅上臂屈伸。' WHERE actionname = '绳索下压' AND equipment = '综合器械';
UPDATE exerciseguides SET primarymuscles = '腹横肌、腹直肌、髂腰肌控制', secondarymuscles = '肩胛稳定肌、股四头肌、臀部稳定肌', difficulty = '进阶', contraindications = '下背无法贴稳地面、颈肩紧张或负重导致腰拱起时去掉负重。', commonmistakes = '腰离地、动作太快、哑铃过重、脖子用力。', suitablefor = '已能稳定完成死虫并希望增加核心控制难度的用户。', alternatives = '死虫、平板支撑、Pallof Press 抗旋转、悬垂举膝。' WHERE actionname = '负重死虫' AND equipment = '哑铃';
UPDATE exerciseguides SET primarymuscles = '股四头肌、臀大肌、臀中肌', secondarymuscles = '腘绳肌、小腿、核心', difficulty = '新手', contraindications = '膝髋疼痛或弹力带导致膝盖不适时换徒手版本。', commonmistakes = '被弹力带拉到膝内扣、脚跟离地、躯干前倒、底部松掉。', suitablefor = '需要利用弹力带提示膝盖外展、改善深蹲稳定的用户。', alternatives = '徒手深蹲、高脚杯深蹲、腿举机、杠铃深蹲。' WHERE actionname = '弹力带深蹲' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '臀大肌、腘绳肌', secondarymuscles = '竖脊肌、核心、背阔肌', difficulty = '新手', contraindications = '固定点不稳、下背疼痛或髋铰链轨迹不清时降低阻力。', commonmistakes = '弯腰拉带、站起时后仰、膝盖过度前移、手臂主动拉。', suitablefor = '家庭训练中学习髋铰链和臀腿后侧发力的用户。', alternatives = '徒手髋铰链、哑铃罗马尼亚硬拉、绳索拉穿、杠铃罗马尼亚硬拉。' WHERE actionname = '弹力带拉穿' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '肩袖肌群、臀中肌、髋外旋肌群', secondarymuscles = '胸椎活动肌群、核心稳定肌、踝关节稳定肌', difficulty = '新手', contraindications = '肩髋急性疼痛、弹力带阻力过大或关节刺痛时停止。', commonmistakes = '阻力过大、动作追求疲劳、速度太快、忽略左右差异。', suitablefor = '训练前激活、恢复日活动和久坐后的肩髋活动维护。', alternatives = '肩髋动态活动、肩袖外旋、侧向弹力带走、胸椎旋转。' WHERE actionname = '弹力带肩髋激活' AND equipment = '弹力带';
UPDATE exerciseguides SET primarymuscles = '三角肌前束、三角肌中束、肱三头肌', secondarymuscles = '上胸、核心、臀部、斜方肌上束', difficulty = '熟练', contraindications = '肩部上举疼痛、腰椎过伸代偿或腕部无法承托杠铃时降低重量。', commonmistakes = '腰后仰、杠铃绕远路、耸肩过早、核心放松。', suitablefor = '有基础并希望提升站姿垂直推力量和全身稳定的用户。', alternatives = '哑铃肩推、肩推机、Pike 俯卧撑、地雷管推举。' WHERE actionname = '杠铃推举' AND equipment = '杠铃';
UPDATE exerciseguides SET primarymuscles = '三角肌前束、三角肌中束、肱三头肌', secondarymuscles = '上胸、肩胛稳定肌', difficulty = '新手', contraindications = '器械轨迹引发肩部夹挤或座椅无法调到合适高度时换动作。', commonmistakes = '座椅过低、耸肩、回程砸重量、手肘过度后拉。', suitablefor = '需要稳定轨迹学习肩推或控制肩部训练容量的用户。', alternatives = '哑铃肩推、杠铃推举、Pike 俯卧撑、弹力带肩推。' WHERE actionname = '肩推机' AND equipment = '综合器械';
UPDATE exerciseguides SET primarymuscles = '臀大肌、腘绳肌', secondarymuscles = '竖脊肌、核心、背阔肌', difficulty = '进阶', contraindications = '下背疼痛、绳索路径无法稳定或髋铰链不熟时降低重量。', commonmistakes = '手臂主动拉、站起后仰、背部弯曲、绳索摩擦身体导致姿势变形。', suitablefor = '需要用稳定阻力强化臀腿后侧和髋伸能力的用户。', alternatives = '弹力带拉穿、哑铃罗马尼亚硬拉、杠铃罗马尼亚硬拉、徒手髋铰链。' WHERE actionname = '绳索拉穿' AND equipment = '综合器械';
UPDATE exerciseguides SET primarymuscles = '肱二头肌、肱肌', secondarymuscles = '肱桡肌、前臂屈肌', difficulty = '新手', contraindications = '肘腕疼痛或绳索高度导致肩部不适时调整握把和重量。', commonmistakes = '身体晃动、肘部前移、肩膀耸起、下放失控。', suitablefor = '需要持续张力训练肱二头肌并减少自由重量摆动的用户。', alternatives = '弹力带弯举、哑铃弯举、杠铃弯举、毛巾自阻力弯举。' WHERE actionname = '绳索弯举' AND equipment = '综合器械';

INSERT INTO fitnesscheckins (
    username, checkin_date, plan_day, duration_minutes, mood, note, created_at
) VALUES
('demo', CURRENT_DATE, 1, 35, '状态不错', '完成了上肢训练，俯卧撑比上周稳定。', CURRENT_TIMESTAMP),
('demo', CURRENT_DATE - INTERVAL '1 day', NULL, 25, '轻松', '做了核心训练和拉伸。', CURRENT_TIMESTAMP - INTERVAL '1 day'),
('demo', CURRENT_DATE - INTERVAL '2 days', NULL, 40, '有挑战', '下肢训练比较累，休息时间需要拉长。', CURRENT_TIMESTAMP - INTERVAL '2 days');

INSERT INTO plantaskrecords (
    username, plan_date, daytime, action_index, actionpattern, actionname, equipment, completed, actual_sets, actual_reps, difficulty_score, note, updated_at
) VALUES
('demo', CURRENT_DATE, 1, 0, '水平推', '俯卧撑', '徒手', TRUE, 3, 12, 6, '动作稳定，最后一组略累。', CURRENT_TIMESTAMP),
('demo', CURRENT_DATE, 1, 1, '水平拉', '桌下反向划船', '徒手', TRUE, 3, 10, 7, '肩胛控制比上周好。', CURRENT_TIMESTAMP),
('demo', CURRENT_DATE, 1, 2, '核心稳定', '平板支撑', '徒手', FALSE, 2, 30, 5, '准备晚些补做。', CURRENT_TIMESTAMP);

INSERT INTO nutritionrecommendationhistories (
    username, targetcalories, proteingrams, carbohydrategrams, fatgrams, preferencesummary, summary, created_at
) VALUES
('demo', 1960, 93, 260, 50, '偏好「均衡饮食」，预算「中等」，外食频率「偶尔外食」。', '当前目标「保持健康」建议日摄入约 1960 kcal，优先保证蛋白质和规律三餐。', CURRENT_TIMESTAMP);

INSERT INTO trainingcycles (
    username, cycle_week, stage, cycle_goal, recovery_day, start_date, end_date, status
) VALUES
('demo', 1, '基础适应期', '围绕「保持健康」完成每周 3 次训练，并保证动作质量记录。', 4, CURRENT_DATE, CURRENT_DATE + INTERVAL '27 days', 'active');

INSERT INTO planadjustmentrecords (
    username, reason, adjustment_type, summary, created_at
) VALUES
('demo', '本周动作记录已开始形成', '保持计划', '先保持当前每周 3 次节奏，连续两周稳定后再给主动作加一组。', CURRENT_TIMESTAMP);

INSERT INTO exercisealternatives(source_guide_id, target_guide_id, reason, priority)
SELECT s.id, t.id, '同为水平推，可根据器材和强度替换。', 1
FROM exerciseguides s, exerciseguides t
WHERE s.actionname = '俯卧撑' AND t.actionname = '弹力带胸推';

INSERT INTO exercisealternatives(source_guide_id, target_guide_id, reason, priority)
SELECT s.id, t.id, '同为水平拉，适合在徒手和弹力带条件之间切换。', 1
FROM exerciseguides s, exerciseguides t
WHERE s.actionname = '桌下反向划船' AND t.actionname = '弹力带划船';

INSERT INTO exercisealternatives(source_guide_id, target_guide_id, reason, priority)
SELECT s.id, t.id, '同为下肢蹲，弹力带版本更适合膝盖轨迹提示。', 1
FROM exerciseguides s, exerciseguides t
WHERE s.actionname = '徒手深蹲' AND t.actionname = '弹力带深蹲';

INSERT INTO fooditems (
    name, category, calories_per100g, protein_per100g, carbohydrate_per100g, fat_per100g, tags, scene
) VALUES
('鸡蛋', '低脂蛋白', 143, 12.6, 1.1, 9.5, '早餐,便利店,高蛋白', '通用'),
('鸡胸肉', '低脂蛋白', 165, 31.0, 0.0, 3.6, '增肌,减脂,备餐', '家庭做饭'),
('北豆腐', '低脂蛋白', 116, 10.9, 3.0, 6.8, '素食友好,经济', '通用'),
('希腊酸奶', '低脂蛋白', 95, 9.0, 3.8, 4.0, '加餐,训练后', '便利店'),
('米饭', '高蛋白主食', 116, 2.6, 25.9, 0.3, '主食,食堂', '通用'),
('燕麦', '高蛋白主食', 389, 16.9, 66.3, 6.9, '早餐,控糖友好', '家庭做饭'),
('红薯', '高蛋白主食', 86, 1.6, 20.1, 0.1, '减脂,饱腹', '通用'),
('西兰花', '蔬菜', 34, 2.8, 6.6, 0.4, '高纤维,低热量', '通用'),
('香蕉', '水果', 89, 1.1, 22.8, 0.3, '训练前,便利店', '便利店'),
('无糖豆浆', '饮品', 31, 3.0, 1.2, 1.6, '早餐,素食友好', '便利店');

INSERT INTO mealtemplates (
    name, meal_type, goal, scene, target_calories, description, foods
) VALUES
('稳定早餐', '早餐', '通用', '通用', 430, '提供上午能量和基础蛋白，适合训练日与工作日。', '燕麦 + 鸡蛋 + 无糖豆浆 + 香蕉'),
('食堂减脂午餐', '午餐', '减脂', '食堂', 620, '控制油脂和酱汁，保留主食保证下午状态。', '半份米饭 + 鸡胸/鱼虾 + 两份蔬菜 + 清汤'),
('增肌训练后晚餐', '晚餐', '增肌', '家庭做饭', 760, '训练后补足蛋白和主食，帮助恢复和增肌。', '米饭 + 鸡胸肉/牛肉 + 西兰花 + 酸奶'),
('便利店加餐', '加餐', '通用', '便利店', 280, '无法正餐时先补蛋白和易消化碳水。', '希腊酸奶 + 香蕉 + 茶叶蛋'),
('外卖均衡套餐', '午餐', '通用', '外卖', 680, '外卖场景优先选择清淡蛋白和可控主食。', '少油盖饭 + 加蔬菜 + 无糖饮料');

INSERT INTO foodreplacements (
    source_food, replacement_food, reason, category
) VALUES
('鸡胸肉', '鱼虾、瘦牛肉、鸡蛋、北豆腐', '同属优质蛋白，可按预算和口味替换。', '低脂蛋白'),
('米饭', '燕麦、红薯、土豆、玉米', '同属主食，训练日前后可优先保留。', '高蛋白主食'),
('奶制品', '无糖豆浆、北豆腐、鸡蛋', '乳糖不耐时用豆制品或鸡蛋补足蛋白。', '低脂蛋白'),
('含糖饮料', '水、无糖茶、黑咖啡', '减少液体热量，优先保证饮水。', '饮品');

INSERT INTO eatingscenarios (
    name, goal, strategy, avoid, example
) VALUES
('食堂', '通用', '先选蛋白，再选蔬菜，主食按训练日保留半份到一份。', '油炸主菜、浓稠酱汁、含糖饮料。', '米饭半份 + 鸡蛋/鱼虾 + 两份蔬菜 + 清汤'),
('便利店', '通用', '组合蛋白、主食和水果，避免只吃零食。', '甜面包、炸鸡、奶茶、薯片。', '茶叶蛋 + 希腊酸奶 + 香蕉 + 无糖豆浆'),
('外卖', '减脂', '选择少油蛋白套餐，备注少油少酱，额外加蔬菜。', '炸物、重油拌饭、双主食套餐。', '鸡胸/牛肉饭 + 半份饭 + 加青菜 + 无糖饮料'),
('家庭做饭', '增肌', '提前备好蛋白和主食，训练后不用临时点高油外卖。', '训练后空腹太久、高油夜宵。', '米饭 + 鸡胸/牛肉 + 西兰花 + 酸奶');

SELECT setval(pg_get_serial_sequence('notices', 'id'), COALESCE((SELECT MAX(id) FROM notices), 1), TRUE);
SELECT setval(pg_get_serial_sequence('articles', 'id'), COALESCE((SELECT MAX(id) FROM articles), 1), TRUE);
SELECT setval(pg_get_serial_sequence('plantaskrecords', 'id'), COALESCE((SELECT MAX(id) FROM plantaskrecords), 1), TRUE);
SELECT setval(pg_get_serial_sequence('nutritionrecommendationhistories', 'id'), COALESCE((SELECT MAX(id) FROM nutritionrecommendationhistories), 1), TRUE);
SELECT setval(pg_get_serial_sequence('trainingcycles', 'id'), COALESCE((SELECT MAX(id) FROM trainingcycles), 1), TRUE);
SELECT setval(pg_get_serial_sequence('planadjustmentrecords', 'id'), COALESCE((SELECT MAX(id) FROM planadjustmentrecords), 1), TRUE);
SELECT setval(pg_get_serial_sequence('exercisealternatives', 'id'), COALESCE((SELECT MAX(id) FROM exercisealternatives), 1), TRUE);
SELECT setval(pg_get_serial_sequence('fooditems', 'id'), COALESCE((SELECT MAX(id) FROM fooditems), 1), TRUE);
SELECT setval(pg_get_serial_sequence('mealtemplates', 'id'), COALESCE((SELECT MAX(id) FROM mealtemplates), 1), TRUE);
SELECT setval(pg_get_serial_sequence('foodreplacements', 'id'), COALESCE((SELECT MAX(id) FROM foodreplacements), 1), TRUE);
SELECT setval(pg_get_serial_sequence('eatingscenarios', 'id'), COALESCE((SELECT MAX(id) FROM eatingscenarios), 1), TRUE);

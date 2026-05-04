CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    nickname VARCHAR(50),
    password VARCHAR(100) NOT NULL,
    userpic VARCHAR(1000),
    createtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    registered BOOLEAN DEFAULT FALSE,
    identity VARCHAR(20) DEFAULT 'user',
    specialty VARCHAR(200),
    height DOUBLE,
    weight DOUBLE
);

CREATE TABLE IF NOT EXISTS userprofiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    fitnessgoal VARCHAR(50) NOT NULL,
    weeklyfrequency INT NOT NULL,
    equipment VARCHAR(50) NOT NULL,
    exlevel VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS notices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    author VARCHAR(50),
    noticetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tempnotices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    author VARCHAR(50),
    tempnoticetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    author VARCHAR(50),
    topic VARCHAR(100),
    articletime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fitnessplanrules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fitnessgoal VARCHAR(50) NOT NULL,
    weeklyfrequency INT NOT NULL,
    splitmode TEXT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_fitnessplanrules_goal_frequency
    ON fitnessplanrules(fitnessgoal, weeklyfrequency);

CREATE TABLE IF NOT EXISTS daytrains (
    id INT AUTO_INCREMENT PRIMARY KEY,
    splitmode VARCHAR(100) NOT NULL UNIQUE,
    actionpattern TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS trainingparams (
    id INT AUTO_INCREMENT PRIMARY KEY,
    actionpattern VARCHAR(100) NOT NULL,
    exlevel VARCHAR(50) NOT NULL,
    minsets INT NOT NULL,
    maxsets INT NOT NULL,
    minreps INT NOT NULL,
    maxreps INT NOT NULL,
    minrestseconds INT NOT NULL,
    maxrestseconds INT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_trainingparams_pattern_level
    ON trainingparams(actionpattern, exlevel);

CREATE TABLE IF NOT EXISTS actiondetails (
    id INT AUTO_INCREMENT PRIMARY KEY,
    actionpattern VARCHAR(100) NOT NULL,
    equipment VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_actiondetails_pattern_equipment
    ON actiondetails(actionpattern, equipment);

CREATE TABLE IF NOT EXISTS exerciseguides (
    id INT AUTO_INCREMENT PRIMARY KEY,
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

ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS actionname VARCHAR(100);
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS steps TEXT;
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS tips TEXT;
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS imagecredit VARCHAR(200);
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS imagesourceurl VARCHAR(1000);
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS primarymuscles VARCHAR(200);
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS secondarymuscles VARCHAR(200);
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS difficulty VARCHAR(50);
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS contraindications TEXT;
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS commonmistakes TEXT;
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS suitablefor TEXT;
ALTER TABLE exerciseguides ADD COLUMN IF NOT EXISTS alternatives TEXT;

CREATE UNIQUE INDEX IF NOT EXISTS uk_exerciseguides_pattern_equipment
    ON exerciseguides(actionpattern, equipment);

CREATE TABLE IF NOT EXISTS fitnesscheckins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    checkin_date DATE NOT NULL,
    plan_day INT,
    duration_minutes INT DEFAULT 0,
    mood VARCHAR(50),
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE fitnesscheckins ADD COLUMN IF NOT EXISTS plan_day INT;

CREATE UNIQUE INDEX IF NOT EXISTS uk_fitnesscheckins_username_date
    ON fitnesscheckins(username, checkin_date);

CREATE TABLE IF NOT EXISTS plantaskrecords (
    id INT AUTO_INCREMENT PRIMARY KEY,
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

CREATE UNIQUE INDEX IF NOT EXISTS uk_plantaskrecords_key
    ON plantaskrecords(username, plan_date, daytime, action_index);

CREATE TABLE IF NOT EXISTS trainingcycles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    cycle_week INT DEFAULT 1,
    stage VARCHAR(80),
    cycle_goal TEXT,
    recovery_day INT DEFAULT 4,
    start_date DATE,
    end_date DATE,
    status VARCHAR(30) DEFAULT 'active'
);

CREATE INDEX IF NOT EXISTS idx_trainingcycles_username_status
    ON trainingcycles(username, status);

CREATE TABLE IF NOT EXISTS planadjustmentrecords (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    reason VARCHAR(200),
    adjustment_type VARCHAR(80),
    summary TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_planadjustmentrecords_username_time
    ON planadjustmentrecords(username, created_at);

CREATE TABLE IF NOT EXISTS exercisealternatives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    source_guide_id INT NOT NULL,
    target_guide_id INT NOT NULL,
    reason VARCHAR(300),
    priority INT DEFAULT 10
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_exercisealternatives_pair
    ON exercisealternatives(source_guide_id, target_guide_id);

CREATE TABLE IF NOT EXISTS nutritionpreferences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    diettype VARCHAR(50) DEFAULT '均衡饮食',
    allergies VARCHAR(300),
    budgetlevel VARCHAR(50) DEFAULT '中等',
    eatingoutfrequency VARCHAR(50) DEFAULT '偶尔外食',
    mealcount INT DEFAULT 4,
    tastepreference VARCHAR(100) DEFAULT '清淡'
);

CREATE TABLE IF NOT EXISTS nutritionrecommendationhistories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    targetcalories INT,
    proteingrams INT,
    carbohydrategrams INT,
    fatgrams INT,
    preferencesummary TEXT,
    summary TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fooditems (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    calories_per100g INT,
    protein_per100g DOUBLE,
    carbohydrate_per100g DOUBLE,
    fat_per100g DOUBLE,
    tags VARCHAR(300),
    scene VARCHAR(80)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_fooditems_name
    ON fooditems(name);

CREATE TABLE IF NOT EXISTS mealtemplates (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    meal_type VARCHAR(50) NOT NULL,
    goal VARCHAR(50) DEFAULT '通用',
    scene VARCHAR(80) DEFAULT '通用',
    target_calories INT,
    description TEXT,
    foods TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_mealtemplates_identity
    ON mealtemplates(name, meal_type, goal, scene);

CREATE TABLE IF NOT EXISTS foodreplacements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    source_food VARCHAR(100) NOT NULL,
    replacement_food VARCHAR(200) NOT NULL,
    reason TEXT,
    category VARCHAR(50)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_foodreplacements_pair
    ON foodreplacements(source_food, replacement_food);

CREATE TABLE IF NOT EXISTS eatingscenarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    goal VARCHAR(50) DEFAULT '通用',
    strategy TEXT,
    avoid TEXT,
    example TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_eatingscenarios_identity
    ON eatingscenarios(name, goal);

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

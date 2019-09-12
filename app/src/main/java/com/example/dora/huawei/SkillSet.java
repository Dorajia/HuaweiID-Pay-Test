package com.example.dora.huawei;

/**
 * Created by Dora on 3/12/17.
 */

public class SkillSet {
        private String skill;
        private String level;

        public SkillSet() {
        }

        public SkillSet(String skill, String level) {
            this.skill = skill;
            this.level = level;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }
}

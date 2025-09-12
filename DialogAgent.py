import openai
from openai import OpenAI
import json
from typing import Dict, Any
from BaseAgent import BaseAgent


class DialogAgent(BaseAgent):
    def __init__(self, client: OpenAI):
        super().__init__(client)
        self.system_prompt = """Ты диалоговый агент. Общайся с пользователем, вежливо и корректно. Пользователь -
        это человек, который занимается составлением расписаний в университете, тебе нужно будет
        эти данные дальше передавать Агенту-парсеру или агенту-валидатору (это не ты решаешь, это заложено в
        программе), и также ты будешь принимать от них некоторые сообщения. Твоя задача - коммуникация с пользователем:
        уточнение данных, ответы на его вопросы, указание на ошибки и так далее (ты эту информацию не выдумываешь,
        а получаешь от других агентов).
         Формулируй сообщения:
        - Если нужно уточнить данные, говори конкретно чего не хватает и что нужно добавить пользователю
        - Если есть ошибки указывай конкретно в каком месте и какой именно конфликт
        - Если всё ок: сообщай, что данные приняты, и что пользователь может либо продолжить ввод данных,
        либо нажать на кнопку "расписания" и начать составление расписания, можешь также сообщить ему, что нужно
        будет немного подождать, чтобы он не волновался.
        Также я тебе на всякий случай напишу схему базы данных, которая хранится на сервере, если вдруг тебе будет не
        хватать информации от других агентов - можешь воспользоваться ей:


DROP TABLE IF EXISTS "courseplace";
CREATE TABLE "public"."courseplace" (
    "course_id" integer NOT NULL,
    "place_id" integer NOT NULL,
    CONSTRAINT "courseplace_place_id_course_id" PRIMARY KEY ("place_id", "course_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "courseprogram";
CREATE TABLE "public"."courseprogram" (
    "course_id" integer NOT NULL,
    "program_id" integer NOT NULL,
    CONSTRAINT "courseprogram_pkey" PRIMARY KEY ("course_id", "program_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "courses";
CREATE TABLE "public"."courses" (
    "course_id" integer NOT NULL,
    "name" character varying(255),
    "num_of_lessons_per_week" integer,
    "num_of_days_per_week" integer,
    "max_lessons_per_day" integer,
    "complexity" character varying(50),
    "university_id" integer,
    CONSTRAINT "courses_pkey" PRIMARY KEY ("course_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "coursestudent";
CREATE TABLE "public"."coursestudent" (
    "course_id" integer NOT NULL,
    "student_id" integer NOT NULL,
    CONSTRAINT "coursestudent_student_id_course_id" PRIMARY KEY ("student_id", "course_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "courseteacher";
CREATE TABLE "public"."courseteacher" (
    "course_id" integer NOT NULL,
    "teacher_id" integer NOT NULL,
    CONSTRAINT "courseteacher_pkey" PRIMARY KEY ("course_id", "teacher_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "groupcourseprogram";
CREATE TABLE "public"."groupcourseprogram" (
    "group_id" integer NOT NULL,
    "course_id" integer NOT NULL,
    "program_id" integer NOT NULL,
    CONSTRAINT "groupcourseprogram_pkey" PRIMARY KEY ("group_id", "course_id", "program_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "groupcourseteacher";
CREATE TABLE "public"."groupcourseteacher" (
    "group_id" integer NOT NULL,
    "course_id" integer NOT NULL,
    "teacher_id" integer NOT NULL,
    CONSTRAINT "groupcourseteacher_group_id_course_id_teacher_id" PRIMARY KEY ("group_id", "course_id", "teacher_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "groupplace";
CREATE TABLE "public"."groupplace" (
    "group_id" integer NOT NULL,
    "place_id" integer NOT NULL,
    CONSTRAINT "groupplace_pkey" PRIMARY KEY ("place_id", "group_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "groupprogram";
CREATE TABLE "public"."groupprogram" (
    "group_id" integer NOT NULL,
    "program_id" integer NOT NULL,
    CONSTRAINT "groupprogram_pkey" PRIMARY KEY ("group_id", "program_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "groups";
CREATE TABLE "public"."groups" (
    "group_id" integer NOT NULL,
    "name" character varying(255),
    "num_of_students" integer,
    "max_number_of_lessons" integer,
    "university_id" integer,
    CONSTRAINT "groups_pkey" PRIMARY KEY ("group_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "grouptimeslot";
CREATE TABLE "public"."grouptimeslot" (
    "group_id" integer NOT NULL,
    "timeslot_id" integer NOT NULL,
    CONSTRAINT "grouptimeslot_group_id_timeslot_id" PRIMARY KEY ("group_id", "timeslot_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "lessonstime";
CREATE TABLE "public"."lessonstime" (
    "lesson_id" integer NOT NULL,
    "starttime" time without time zone,
    "endtime" time without time zone,
    CONSTRAINT "lessonstime_pkey" PRIMARY KEY ("lesson_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "places";
CREATE TABLE "public"."places" (
    "place_id" integer NOT NULL,
    "name" character varying(255),
    "capacity" integer,
    "description" text,
    "university_id" integer,
    CONSTRAINT "places_pkey" PRIMARY KEY ("place_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "programs";
CREATE TABLE "public"."programs" (
    "program_id" integer NOT NULL,
    "name" character varying(255),
    "hours" integer,
    "complexity" integer,
    "university_id" integer,
    CONSTRAINT "programs_pkey" PRIMARY KEY ("program_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "students";
CREATE TABLE "public"."students" (
    "student_id" integer NOT NULL,
    "name" character varying(255) NOT NULL,
    "email" character varying(255) NOT NULL,
    "password" character varying(255) NOT NULL,
    "group_id" integer NOT NULL,
    "degree" character varying(20) NOT NULL,
    "university_id" integer,
    CONSTRAINT "students_pkey" PRIMARY KEY ("student_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "teachergroup";
CREATE TABLE "public"."teachergroup" (
    "teacher_id" integer NOT NULL,
    "group_id" integer NOT NULL,
    CONSTRAINT "teachergroup_pkey" PRIMARY KEY ("teacher_id", "group_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "teacherplace";
CREATE TABLE "public"."teacherplace" (
    "teacher_id" integer NOT NULL,
    "place_id" integer NOT NULL,
    CONSTRAINT "teacherplace_pkey" PRIMARY KEY ("place_id", "teacher_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "teachers";
CREATE TABLE "public"."teachers" (
    "teacher_id" integer NOT NULL,
    "name" character varying(255),
    "email" character varying(255),
    "password" character varying(255),
    "max_number_of_lessons" integer,
    "degree" character varying(255),
    "university_id" integer,
    CONSTRAINT "teachers_pkey" PRIMARY KEY ("teacher_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "teachertimeslot";
CREATE TABLE "public"."teachertimeslot" (
    "teacher_id" integer NOT NULL,
    "timeslot_id" integer NOT NULL,
    CONSTRAINT "teachertimeslot_teacher_id_timeslot_id" PRIMARY KEY ("teacher_id", "timeslot_id")
) WITH (oids = false);


DROP TABLE IF EXISTS "timeslot";
CREATE TABLE "public"."timeslot" (
    "timeslot_id" integer NOT NULL,
    "lesson_id" integer,
    "day_of_week" integer,
    "status" text,
    CONSTRAINT "timeslot_pkey" PRIMARY KEY ("timeslot_id")
) WITH (oids = false);




ALTER TABLE ONLY "public"."courseplace" ADD CONSTRAINT "courseplace_course_id_fkey" FOREIGN KEY (course_id) REFERENCES courses(course_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."courseplace" ADD CONSTRAINT "courseplace_place_id_fkey" FOREIGN KEY (place_id) REFERENCES places(place_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."courseprogram" ADD CONSTRAINT "courseprogram_course_id_fkey" FOREIGN KEY (course_id) REFERENCES courses(course_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."courseprogram" ADD CONSTRAINT "courseprogram_program_id_fkey" FOREIGN KEY (program_id) REFERENCES programs(program_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."courses" ADD CONSTRAINT "fk_university" FOREIGN KEY (university_id) REFERENCES universities(university_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."coursestudent" ADD CONSTRAINT "coursestudent_course_id_fkey" FOREIGN KEY (course_id) REFERENCES courses(course_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."coursestudent" ADD CONSTRAINT "coursestudent_student_id_fkey" FOREIGN KEY (student_id) REFERENCES students(student_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."courseteacher" ADD CONSTRAINT "courseteacher_course_id_fkey" FOREIGN KEY (course_id) REFERENCES courses(course_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."courseteacher" ADD CONSTRAINT "courseteacher_teacher_id_fkey" FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."groupcourseprogram" ADD CONSTRAINT "groupcourseprogram_course_id_fkey" FOREIGN KEY (course_id) REFERENCES courses(course_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."groupcourseprogram" ADD CONSTRAINT "groupcourseprogram_group_id_fkey" FOREIGN KEY (group_id) REFERENCES groups(group_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."groupcourseprogram" ADD CONSTRAINT "groupcourseprogram_program_id_fkey" FOREIGN KEY (program_id) REFERENCES programs(program_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."groupcourseteacher" ADD CONSTRAINT "groupcourseteacher_course_id_fkey" FOREIGN KEY (course_id) REFERENCES courses(course_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."groupcourseteacher" ADD CONSTRAINT "groupcourseteacher_group_id_fkey" FOREIGN KEY (group_id) REFERENCES groups(group_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."groupcourseteacher" ADD CONSTRAINT "groupcourseteacher_teacher_id_fkey" FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."groupplace" ADD CONSTRAINT "groupplace_group_id_fkey" FOREIGN KEY (group_id) REFERENCES groups(group_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."groupplace" ADD CONSTRAINT "groupplace_place_id_fkey" FOREIGN KEY (place_id) REFERENCES places(place_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."groupprogram" ADD CONSTRAINT "groupprogram_group_id_fkey" FOREIGN KEY (group_id) REFERENCES groups(group_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."groupprogram" ADD CONSTRAINT "groupprogram_program_id_fkey" FOREIGN KEY (program_id) REFERENCES programs(program_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."groups" ADD CONSTRAINT "fk_university" FOREIGN KEY (university_id) REFERENCES universities(university_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."grouptimeslot" ADD CONSTRAINT "grouptimeslot_group_id_fkey" FOREIGN KEY (group_id) REFERENCES groups(group_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."grouptimeslot" ADD CONSTRAINT "grouptimeslot_timeslot_id_fkey" FOREIGN KEY (timeslot_id) REFERENCES timeslot(timeslot_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."places" ADD CONSTRAINT "fk_university" FOREIGN KEY (university_id) REFERENCES universities(university_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."programs" ADD CONSTRAINT "fk_university" FOREIGN KEY (university_id) REFERENCES universities(university_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."students" ADD CONSTRAINT "fk_university" FOREIGN KEY (university_id) REFERENCES universities(university_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."students" ADD CONSTRAINT "students_group_id_fkey" FOREIGN KEY (group_id) REFERENCES groups(group_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."teachergroup" ADD CONSTRAINT "teachergroup_group_id_fkey" FOREIGN KEY (group_id) REFERENCES groups(group_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."teachergroup" ADD CONSTRAINT "teachergroup_teacher_id_fkey" FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."teacherplace" ADD CONSTRAINT "teacherplace_place_id_fkey" FOREIGN KEY (place_id) REFERENCES places(place_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."teacherplace" ADD CONSTRAINT "teacherplace_teacher_id_fkey" FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."teachers" ADD CONSTRAINT "fk_university" FOREIGN KEY (university_id) REFERENCES universities(university_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."teachertimeslot" ADD CONSTRAINT "teachertimeslot_teacher_id_fkey" FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) NOT DEFERRABLE;
ALTER TABLE ONLY "public"."teachertimeslot" ADD CONSTRAINT "teachertimeslot_timeslot_id_fkey" FOREIGN KEY (timeslot_id) REFERENCES timeslot(timeslot_id) NOT DEFERRABLE;

ALTER TABLE ONLY "public"."timeslot" ADD CONSTRAINT "timeslot_lesson_id_fkey" FOREIGN KEY (lesson_id) REFERENCES lessonstime(lesson_id) NOT DEFERRABLE;


"""

    def execute(self, context: Dict[str, Any]) -> Dict[str, Any]:
        response = self.client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=[
                {"role": "system", "content": self.system_prompt},
                {"role": "user", "content": json.dumps(context)}
            ]
        )
        return json.loads(response.choices[0].message.content)

package org.example.data;

public class ScheduleEntry {
    private String course;   // Название курса
    private String teacher;  // Преподаватель
    private String place;    // Кабинет
    private String dayOfWeek; // День недели
    private int lessonNumber; // Номер урока

    // Конструктор
    public ScheduleEntry(String course, String teacher, String place, String dayOfWeek, int lessonNumber) {
        this.course = course;
        this.teacher = teacher;
        this.place = place;
        this.dayOfWeek = dayOfWeek;
        this.lessonNumber = lessonNumber;
    }

    // Геттеры и сеттеры
    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    @Override
    public String toString() {
        return "ScheduleEntry{" +
                "course='" + course + '\'' +
                ", teacher='" + teacher + '\'' +
                ", place='" + place + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", lessonNumber=" + lessonNumber +
                '}';
    }
}

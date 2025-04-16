package algorithms;

import schedule.Schedule;

public class LSMoveImpl implements LSMove{
    private final int i;
    private final int j;

    public LSMoveImpl(int i, int j) {
        this.i = i;
        this.j = j;
    }

    @Override
    public Schedule makeMove(Schedule schedule) {
        //Lesson l1 = schedule.getAllLessons().toList().get(i);
        //Lesson l2 = schedule.getAllLessons().toList().get(j);
        //Schedule scheduleNew = new ScheduleImpl(newLessons);

        return schedule;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LSMoveImpl move = (LSMoveImpl) obj;
        return i == move.i && j == move.j;
    }

    @Override
    public int hashCode() {
        return 31 * i + j;
    }
}

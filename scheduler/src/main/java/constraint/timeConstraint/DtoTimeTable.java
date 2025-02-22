package constraint.timeConstraint;

import time.DtoWeeklyTimeSlot;

import javax.swing.event.ListDataEvent;
import java.util.ArrayList;
import java.util.List;

public class DtoTimeTable {
    public String objName;
    public List<DtoWeeklyTimeSlot> prohibitedSlots = new ArrayList<>();
    public List<DtoWeeklyTimeSlot> preferredSlots = new ArrayList<>();
    public List<DtoWeeklyTimeSlot> undesirableSlots = new ArrayList<>();
}

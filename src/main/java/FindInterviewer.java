import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Lets capture actors in this system
class Person {
    static int id_counter = 0;
    String name;
    int id;
    public Person(String name) {this.name = name; this.id = ++id_counter; }
}
class Skill {
    int id;
    static int id_counter = 0;
    String skillName;
    public Skill(String skill) {
        this.skillName = skill;
        this.id = ++id_counter;
    }
}

//TODO: Define Job Description as two collections: Mandatory, Desirable
//   .. this JD will be queried for mandatory skills to find an interviewer
//   .. desirable skills can be used to enhance the match

//TODO: Add order of preference in three classes 1 - Lowest, 3 - Highest
//TODO: Add timeslot using the real time and optionally specifying start time and duration of interview
class TimeSlot {
    enum Preference {
        LOW,
        MEDIUM,
        HIGH
    }
    static int id_counter = 0;
    int slot_id;
    public TimeSlot(int slot_id) {
        this.slot_id = slot_id;
        this.slot_id = ++id_counter;
    }
}

class Engineer extends Person {
    List<TimeSlot> available_slots;
    List<Skill> skills;
    public Engineer(String name) {
        super(name);
        available_slots = new ArrayList<TimeSlot>();
        skills = new ArrayList<Skill>();
    }
    public Engineer addSlot(TimeSlot slot) {
        available_slots.add(slot);
        return this;
    }
    public Engineer addSkill(Skill skill) {
        skills.add(skill);
        return this;
    }
}
class CalendarManager { // need helper class to ensure TimeSlots are unique
    Map<TimeSlot, Candidate> scheduled_interviews;
    List<TimeSlot> available_slots;
    public CalendarManager(List<TimeSlot> available_slots) {
        this.available_slots = available_slots;
        scheduled_interviews = new HashMap<TimeSlot, Candidate>();
    }
    public boolean scheduleSlot(Candidate candidate, TimeSlot slot) {
        for(TimeSlot t:available_slots) {
            if(t.slot_id == slot.slot_id) {
                available_slots.remove(t);
                scheduled_interviews.put(t, candidate);
                return true;
            }
        }
        return false;
    }

    //TODO: Look for the best matching slot
    public List<TimeSlot> findMatchingFreeSlots(List<TimeSlot> slots) {
        List<TimeSlot> matching_slots = new ArrayList<TimeSlot>();
        for(TimeSlot t:available_slots) {
            for(TimeSlot ct:slots) {
                if(t.slot_id == ct.slot_id) {
                    matching_slots.add(t);
                    break;
                }
            }
        }
        return matching_slots;
    }
}
class Interviewer extends Engineer {
    CalendarManager calendarManager;
    public Interviewer(String name) {
        super(name);
        calendarManager = new CalendarManager(available_slots);
    }
    public TimeSlot canTakeInterview(List<Skill> caps, List<TimeSlot> c_slots) {
        int match_score = match(caps);
        if(match_score > 80) {
            List<TimeSlot> a_slots = calendarManager.findMatchingFreeSlots(c_slots);
            if(a_slots.size() > 0) return a_slots.get(0); // return first slot
        }
        return null;
    }
    public int match(List<Skill> caps) {
        int match_count = 0, total_count = caps.size();
        for(Skill s:skills) {
            for(Skill cs : caps) {
                if(s.id == cs.id) {
                    match_count++;
                    break;
                }
            }
        }
        float score = match_count * 100 / total_count;
        return Math.round(score);
    }
}

class Candidate extends Engineer {
    Candidate(String name) {
        super(name);
    }
}
public class FindInterviewer {
    public static void main(String[] args) {
        Skill[] skills_collection = {new Skill("C"), new Skill("Java"), new Skill("DevOps")};
        TimeSlot[] time_slots = {new TimeSlot(1), new TimeSlot(2), new TimeSlot(3), new TimeSlot(4)};
        //TODO: This information should be read from database or input API or data file
        Candidate candidate = new Candidate("Candidate 1");
        candidate.addSlot(time_slots[0])
                .addSlot(time_slots[2])
                .addSkill(skills_collection[0])
                .addSkill(skills_collection[2]);
        //TODO: This information should be read from database or input API or data file
        Interviewer interviewer1 = new Interviewer("Interviewer 1");
        interviewer1.addSlot(time_slots[1])
                .addSlot(time_slots[3])
                .addSkill(skills_collection[1])
                .addSkill(skills_collection[2]);

        Interviewer interviewer2 = new Interviewer("Interviewer 2");
        interviewer2.addSlot(time_slots[2])
                .addSkill(skills_collection[0])
                .addSkill(skills_collection[2]);
        //TODO: should get automatically populated as a result of reading database
        List<Interviewer> interviewers = new ArrayList<Interviewer>();
        interviewers.add(interviewer1);
        interviewers.add(interviewer2);

        List<Interviewer> reserved = interviewers.stream().reduce( new ArrayList<Interviewer>(),
                (list, interviewer) -> {
                    TimeSlot slot = interviewer.canTakeInterview(candidate.skills, candidate.available_slots);
                    if(slot != null) {
                        interviewer.calendarManager.scheduleSlot(candidate, slot);
                        list.add(interviewer);
                    }
                    return list;
                }, (list1, list2) -> {
                        list1.addAll(list2);
                        return list1;
                    });
        System.out.println("Got " + reserved.size() + " interviewers and slots");
    }
}

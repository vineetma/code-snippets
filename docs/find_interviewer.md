# Overview
This code captures the actors involved in setting up an interview and build automation 
for searching, freeing, reserving of slots using the best matched skills

# Components
## Classes
* Person, Engineer, Candidate and Interviewer
* FindInterviewer -- Main class
* CalendarManager -- like assistance to Interviewer to work with it's availability

## Core Methods
* canTakeInterview -- This is enquired from interviewer with given slots of candidates. Calendar Manager 
goes into action to figure out from available slots and returns the answer with the slot. It does not reserve
* scheduleInterview -- It books the slot and removes the slot from available_slots


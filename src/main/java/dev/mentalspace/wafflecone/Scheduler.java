package dev.mentalspace.wafflecone;

import java.util.ArrayList;

import dev.mentalspace.wafflecone.databaseobject.Preference;
import dev.mentalspace.wafflecone.todo.Todo;

public class Scheduler {
    /**
     * Rounds up a time in epoch milliseconds to the next 5 min value (i.e. rounds
     * 03:42:55 to 03:45:00).
     * 
     * @param time - epoch time in milliseconds
     * @return time rounded up to the next 5 min value
     */
    private static long fiveMinForward(long time) {
        if (time % 300000 == 0) {
            return time;
        }
        return (time / 300000 + 1) * 300000;
    }

    /**
     * Rounds down to the next 5 min value (i.e. rounds 03:42:55 to 03:40:00).
     * 
     * @param time - epoch time in milliseconds
     * @return time rounded up to the next 5 min value
     */
    private static long fiveMinBack(long time) {
        return (time / 300000) * 300000;
    }

    /**
     * Schedules start times for todos within the time block.
     * 
     * @param start - start of the time block.
     * @param end   - end of the time block.
     * @param todos - list of todos in priority order.
     */
    private static ArrayList<Todo> scheduleBlock(long start, long end, Todo[] todos) {
        ArrayList<Todo> setTodos = new ArrayList<Todo>();
        boolean fitFound = true;
        long blockLength = end - start;

        while (start < end && fitFound) {
            fitFound = false;
            for (int j = 0; j < todos.length; j++) {
                // need a default value for start time of todos
                if (todos[j].projectedStartTime == null && todos[j].plannedTime <= blockLength) {

                    // need to be able to set start time of each todo
                    todos[j].projectedStartTime = start;
                    // need a method to get planned time?
                    setTodos.add(todos[j]);
                    start = fiveMinForward(start + todos[j].plannedTime.longValue());
                    blockLength = end - start;
                    fitFound = true;
                }
            }
        }
        return setTodos;
    }

    /**
     * Schedules todo events for the period of time specified. If a todo does not
     * fit in the schedule, start time will not be set.
     * 
     * @param pref       - student-inputted preference
     * @param todos      - array of todos for the day in order of priority. Start
     *                   times must be null.
     * @param eventStart - array of start times for events in epoch milliseconds in
     *                   chronological ascending order.
     * @param eventEnd   - array of start times for events in epoch milliseconds in
     *                   chronological ascending order.
     * @param start      - the earliest a user can start on todos in epoch
     *                   milliseconds.
     * @param end        - the latest a user can finish todos in epoch milliseconds.
     * @return an Arraylist of todos with the projectedStartTime scheduled in
     *         chronological order.
     */
    public static ArrayList<Todo> scheduleASAP(Preference pref, Todo[] todos, long[] eventStart, long[] eventEnd,
            long start, long end) {
        long blockStart = fiveMinForward(start);
        int index = 0;
        long blockEnd = eventStart[index];
        long timeBlock = blockEnd - blockStart;
        ArrayList<Todo> setTodos = new ArrayList<Todo>();

        for (int i = 0; i < todos.length; i++) {
            long time = todos[i].plannedTime;
            todos[i].plannedTime = (long) ((todos[i].plannedTime / pref.breakFrequency) * pref.breakLength) + time;
        }

        // schedules for the whole day
        while (blockStart < end && index <= eventStart.length) {
            // While loop makes sure that the free block is greater than 0.
            while (timeBlock <= 0) {
                blockStart = fiveMinForward(eventEnd[index]);
                index++;
                if (index >= eventStart.length) {
                    blockEnd = end;
                } else {
                    blockEnd = eventStart[index];
                    if (blockEnd > end) {
                        blockEnd = end;
                    }
                }
                timeBlock = blockEnd - blockStart;
            }

            setTodos.addAll(scheduleBlock(blockStart, blockEnd, todos));

            // moves on to the next free block
            if (index < eventStart.length) {
                blockStart = fiveMinForward(eventEnd[index]);
            }

            index++;

            if (index >= eventStart.length) {
                blockEnd = end;
            } else {
                blockEnd = eventStart[index];
                if (blockEnd > end) {
                    blockEnd = end;
                }
            }
            timeBlock = blockEnd - blockStart;
        }
        return setTodos;
    }

    /**
     * Schedules todos to be as late as possible according to the end time
     * designated by the user. Todos that do not fit will not be scheduled.
     * 
     * @param pref       - student-inputted preference
     * @param todos      - array of todos for the day in order of priority. Start
     *                   times must be null.
     * @param eventStart - array of start times for events in epoch milliseconds in
     *                   chronological ascending order.
     * @param eventEnd   - array of start times for events in epoch milliseconds in
     *                   chronological ascending order.
     * @param start      - the earliest a user can start on todos in epoch
     *                   milliseconds.
     * @param end        - the latest a user can finish todos in epoch milliseconds.
     * @return an Arraylist of todos with the projectedStartTime scheduled in
     *         chronological order.
     */
    public static ArrayList<Todo> scheduleALAP(Preference pref, Todo[] todos, long[] eventStart, long[] eventEnd,
            long start, long end) {
        ArrayList<Todo> setTodos = scheduleASAP(pref, todos, eventStart, eventEnd, start, end);
        ArrayList<Long> starts = new ArrayList<Long>();
        ArrayList<Long> ends = new ArrayList<Long>();
        int[] indexes = new int[setTodos.size()];
        int eventIndex = 0;
        int todoIndex = 0;
        long eStart = eventStart[eventIndex];
        long tStart = setTodos.get(eventIndex).projectedStartTime;;

        // takes records the start and end times of each todo or event in separate
        // ArrayLists. Times are in chronological ascending order.
        for (int i = 0; i < eventStart.length + setTodos.size(); i++) {

            // makes sure that if one list is exhausted, the loop will only take the
            // start/end times from the other list
            if (eventIndex >= eventStart.length) {
                eStart = Long.MAX_VALUE;
            } else {
                eStart = eventStart[eventIndex];
            }

            if (todoIndex >= setTodos.size()) {
                tStart = Long.MAX_VALUE;
            } else {
                tStart = setTodos.get(todoIndex).projectedStartTime;
            }
            // if the next todo comes first, then add the start/end times of the todo to the
            // Arraylists. Otherwise, add the start/end times of the next event.

            if (tStart < eStart) {
                starts.add(tStart);
                ends.add(tStart + setTodos.get(todoIndex).plannedTime);
                indexes[todoIndex] = i;
                todoIndex++;
            } else {
                if (eventStart[eventIndex] > end) {
                    starts.add(end);
                    ends.add(end);
                    break;
                } else {
                    starts.add(eventStart[eventIndex]);
                    ends.add(eventEnd[eventIndex]);
                }
                eventIndex++;
            }
        }

        // Adds an event that starts and ends at the end time.
        if (eventEnd[eventEnd.length - 1] < end) {
            starts.add(end);
            ends.add(end);
        }

        long blockStart = setTodos.get(setTodos.size() - 1).projectedStartTime;
        long blockEnd = starts.get(indexes[setTodos.size() - 1] + 1);
        long timeBlock = blockEnd - blockStart;
        Todo current;

        for (int j = setTodos.size() - 1; j >= 0; j--) {

            current = setTodos.get(j);
            if (current.projectedStartTime + current.plannedTime != end) {
                blockStart = current.projectedStartTime;
                blockEnd = starts.get(indexes[j] + 1);
                timeBlock = blockEnd - blockStart;

                // shifts todos down as far as possible
                for (int h = indexes[j] + 1; h < starts.size(); h++) {

                    // checks whether or not there is enough room to move the todo down
                    if (timeBlock > current.plannedTime) {
                        current.projectedStartTime = fiveMinBack(blockEnd - current.plannedTime);

                        starts.add(h, current.projectedStartTime);
                        ends.add(h, current.projectedStartTime + current.plannedTime);
                        starts.remove(indexes[j]);
                        ends.remove(indexes[j]);
                    }

                    blockStart = ends.get(h);
                    if (h + 1 < starts.size()) {
                        blockEnd = starts.get(h + 1);
                    }
                    timeBlock = blockEnd - blockStart;
                }

            }
        }

        // Orders the todos in chronological order
        for (int x = 0; x < setTodos.size(); x++) {
            for (int y = x + 1; y < setTodos.size(); y++) {
                if (setTodos.get(x).projectedStartTime > setTodos.get(y).projectedStartTime) {
                    setTodos.add(y + 1, setTodos.get(x));
                    setTodos.remove(x);

                }
            }
        }

        return setTodos;
    }
}

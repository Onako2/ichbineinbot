package rs.onako2;

import java.util.List;

// Class that handles time conversion from seconds to years, months, days, etc.
public class TimeConverter {

    public Integer years(Long timeDifference, List<String> list) {
        if (timeDifference >= 31536000L) {
            Integer years = Math.toIntExact(timeDifference / 31536000L);

            if (years == 1) {
                list.add(years + " Jahr");
            } else {
                list.add(years + " Jahre");
            }
            return years;
        } else {
            return 0;
        }
    }

    public Integer months(Long timeDifference, List<String> list) {
        if (timeDifference >= 2678400L) {
            Integer months = Math.toIntExact(timeDifference / 2678400L);

            if (months == 1) {
                list.add(months + " Monat");
            } else {
                list.add(months + " Monate");
            }
            return months;
        } else {
            return 0;
        }
    }

    public Integer days(Long timeDifference, List<String> list) {

        if (timeDifference >= 86400L) {
            Integer days = Math.toIntExact(timeDifference / 86400L);

            if (days == 1) {
                list.add(days + " Tag");
            } else {
                list.add(days + " Tage");
            }
            return days;
        } else {
            return 0;
        }
    }

    public Integer hours(Long timeDifference, List<String> list) {
        if (timeDifference >= 3600L) {
            Integer hours = Math.toIntExact(timeDifference / 3600L);

            if (hours == 1) {
                list.add(hours + " Stunde");
            } else {
                list.add(hours + " Stunden");
            }
            return hours;
        } else {
            return 0;
        }
    }

    public Integer minutes(Long timeDifference, List<String> list) {
        if (timeDifference >= 60L) {
            Integer minutes = Math.toIntExact(timeDifference / 60L);

            if (minutes == 1) {
                list.add(minutes + " Minute");
            } else {
                list.add(minutes + " Minuten");
            }
            return minutes;
        } else {
            return 0;
        }
    }

    public Integer seconds(Long timeDifference, List<String> list) {
        if (timeDifference > 0L) {
            Integer seconds = Math.toIntExact(timeDifference);
            if (seconds == 1) {
                list.add(seconds + " Sekunde");
            } else {
                list.add(seconds + " Sekunden");
            }
            return seconds;
        } else {
            return 0;
        }
    }

    public String getTime(List<String> list, long timeDifference) {

        
        timeDifference -= 31536000L * years(timeDifference, list);
        timeDifference -= 2678400L * months(timeDifference, list);
        timeDifference -= 86400L * days(timeDifference, list);
        timeDifference -= 3600L * hours(timeDifference, list);
        timeDifference -= 60L * minutes(timeDifference, list);
        seconds(timeDifference, list);

        System.out.println(list);
        return list.toString().replace("[", "").replace("]", "").replace(",", "");
    }
}

package ir.jin724.videochat.util;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class DateConverter {

    private static int[] currentDate = null;
    private static int[] buf1 = new int[12];
    private static int[] buf2 = new int[12];

    private static DateConverter instance = null;

    private DateConverter() {
    }

    public static DateConverter getInstance() {

        if (instance == null) {
            instance = new DateConverter();
        }
        return instance;
    }

    public String getCurrentDate2() {
        return new SimpleDateFormat("yyyyMMdd_HHMM", Locale.getDefault()).format(new Date());
    }

    public String currentDate3() {
        return new SimpleDateFormat("yyyy-MM-DD:HH:MM", Locale.getDefault()).format(new Date());
    }

    private int[] getCurrentDate() {
        if (currentDate == null) {
            currentDate = new int[4];
            String currentTime = getCurrentDate2();
            Timber.e("time %s", currentTime);
            currentDate[0] = Integer.parseInt(currentTime.substring(0, 4));
            currentDate[1] = Integer.parseInt(currentTime.substring(4, 6));
            currentDate[2] = Integer.parseInt(currentTime.substring(6, 8));
            currentDate[3] = Integer.parseInt(currentTime.substring(9, 11));
            buf1[0] = 0;
            buf1[1] = 31;
            buf1[2] = 59;
            buf1[3] = 90;
            buf1[4] = 120;
            buf1[5] = 151;
            buf1[6] = 181;
            buf1[7] = 212;
            buf1[8] = 243;
            buf1[9] = 273;
            buf1[10] = 304;
            buf1[11] = 334;
            buf2[0] = 0;
            buf2[1] = 31;
            buf2[2] = 60;
            buf2[3] = 91;
            buf2[4] = 121;
            buf2[5] = 152;
            buf2[6] = 182;
            buf2[7] = 213;
            buf2[8] = 244;
            buf2[9] = 274;
            buf2[10] = 305;
            buf2[11] = 335;
        }
        return currentDate;
    }

    private int calculateOffset(int y, int m, int d) {
        if ((y % 4) != 0) {
            return buf1[m - 1] + d;
        } else {
            return buf2[m - 1] + d;
        }
    }

    private String getCurrentShamsidate(int y, int m, int d) {

        MiladiDate md = new MiladiDate(y, m, d);
        SolarCalendar sc = new SolarCalendar(md);

        return sc.date + " " + sc.strMonth + " " + (sc.year + "").substring(2);
    }

    public String convert(String date) {

        String n = date.substring(0, 10);
        int y = Integer.parseInt(n.substring(0, 4));
        int m = Integer.parseInt(n.substring(5, 7));
        int d = Integer.parseInt(n.substring(8));
        return getCurrentShamsidate(y, m, d);
    }


    public String convert2(String date) {
        Timber.e("Converter %s %d" , date , date.length());
        int offset1 = 0;
        int offset2 = 0;
        int[] c = getCurrentDate();
        int y = Integer.parseInt(date.substring(0, 4));
        int m = Integer.parseInt(date.substring(5, 7));
        int d = Integer.parseInt(date.substring(8, 10));
        int h = Integer.parseInt(date.substring(11, 13));
        if (y == c[0]) {
            int dm = c[1] - m;
            if (dm == 0) {
                offset1 = c[2] - d;
                if (offset1 == 0) {
                    int dh = c[3] - h;
                    if (dh > 0) {
                        //  dh hours ago
                        return nHoursAgo(dh);
                    } else {
                        // 11/15/2016 a moment ago
                        return "چند لحظه پیش";
                    }
                } else {
                    // offset1 days ago
                    return nDaysAgo(offset1, y, m, d);
                }
            } else {
                if (dm == 1) {
                    offset1 = c[2] + (30 - d);
                } else {
                    offset1 = (dm - 1) * 30 + c[2] + (30 - d);
                }
                // if (offset1 <= 30) {
                // offset1 days ago
                return nDaysAgo(offset1, y, m, d);
               /* } else {
                    //  offset1 months ago
                    return   nMonthsAgo(offset1);
                }*/

            }
        } else if (y < c[0]) {
            offset1 = calculateOffset(y, m, d);
            offset1 = 365 - offset1;
            offset2 = calculateOffset(c[0], c[1], c[2]);
            offset1 += offset2;
          /*  if (offset1 > 30) {
                return  nMonthsAgo(offset1);
            } else {*/
            return nDaysAgo(offset1, y, m, d);
            // }
            //  offset1 days ago
        }
        return "";
    }

    private String nHoursAgo(int hour) {
        switch (hour) {
            case 1:
                return "یک ساعت پیش";
            case 2:
                return "2 ساعت پیش";
            case 3:
                return "3 ساعت پیش";
            default:
                return "چند ساعت پیش";
        }


    }

    private String nDaysAgo(int day, int y, int m, int d) {
        int week = day / 7;
        if (day > 7) {
            MiladiDate md = new MiladiDate(y, m, d);
            SolarCalendar sc = new SolarCalendar(md);
           /* switch (week) {
                case 1:
                    return "یک هفته پیش";
                case 2:
                    return "2 هفته پیش";
                case 3:
                    return "3 هفته پیش";
                case 4:
                    return "4 هفته پیش";
            }*/
            return sc.date + " " + sc.strMonth + " " + sc.year;
        } else {
            switch (day) {
                case 1:
                    return "دیروز";
                case 2:
                    return "2 روز پیش";
                case 3:
                    return "3 روز پیش";
                case 4:
                    return "4 روز پیش";
                case 5:
                    return "5 روز پیش";
                case 6:
                    return "6 روز پیش";
                case 7:
                    return "یک هفته پیش";
            }
        }
        return "";
    }

    private String nMonthsAgo(int day) {
        int m = day / 30;
        switch (m) {
            case 1:
                return "یک ماه پیش";
            case 2:
                return "2 ماه پیش";
            case 3:
                return "3 ماه پیش";
            default:
                return "چند ماه پیش";
        }
    }

    private class SolarCalendar {
        String strWeekDay = "";
        String strMonth = "";
        int date;
        int month;
        int year;

        SolarCalendar(MiladiDate md) {
            calcSolarCalendar(md);
        }


        private void calcSolarCalendar(MiladiDate md) {
            int ld;
            int miladiYear = md.getYear() + 1900;
            int miladiMonth = md.getMonth() + 1;
            int miladiDate = md.getDate();
            int WeekDay = md.getDay();
            int[] buf1 = new int[12];
            int[] buf2 = new int[12];
            buf1[0] = 0;
            buf1[1] = 31;
            buf1[2] = 59;
            buf1[3] = 90;
            buf1[4] = 120;
            buf1[5] = 151;
            buf1[6] = 181;
            buf1[7] = 212;
            buf1[8] = 243;
            buf1[9] = 273;
            buf1[10] = 304;
            buf1[11] = 334;
            buf2[0] = 0;
            buf2[1] = 31;
            buf2[2] = 60;
            buf2[3] = 91;
            buf2[4] = 121;
            buf2[5] = 152;
            buf2[6] = 182;
            buf2[7] = 213;
            buf2[8] = 244;
            buf2[9] = 274;
            buf2[10] = 305;
            buf2[11] = 335;
            if ((miladiYear % 4) != 0) {
                date = buf1[miladiMonth - 1] + miladiDate;
                if (date > 79) {
                    date = date - 79;
                    if (date <= 186) {
                        if (date % 31 == 0) {
                            month = date / 31;
                            date = 31;
                        } else {
                            month = (date / 31) + 1;
                            date = (date % 31);
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;
                        if (date % 30 == 0) {
                            month = (date / 30) + 6;
                            date = 30;
                        } else {
                            month = (date / 30) + 7;
                            date = (date % 30);
                        }
                        year = miladiYear - 621;
                    }
                } else {
                    if ((miladiYear > 1996) && (miladiYear % 4) == 1) {
                        ld = 11;
                    } else {
                        ld = 10;
                    }
                    date = date + ld;
                    if (date % 30 == 0) {
                        month = (date / 30) + 9;
                        date = 30;
                    } else {
                        month = (date / 30) + 10;
                        date = (date % 30);
                    }
                    year = miladiYear - 622;
                    year = miladiYear - 622;
                }
            } else {
                date = buf2[miladiMonth - 1] + miladiDate;
                if (miladiYear >= 1996) {
                    ld = 79;
                } else {
                    ld = 80;
                }
                if (date > ld) {
                    date = date - ld;
                    if (date <= 186) {
                        if (date % 31 == 0) {
                            month = (date / 31);
                            date = 31;
                        } else {
                            month = (date / 31) + 1;
                            date = (date % 31);
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;
                        if (date % 30 == 0) {
                            month = (date / 30) + 6;
                            date = 30;
                        } else {
                            month = (date / 30) + 7;
                            date = (date % 30);
                        }
                        year = miladiYear - 621;
                    }
                } else {
                    date = date + 10;
                    if (date % 30 == 0) {
                        month = (date / 30) + 9;
                        date = 30;
                    } else {
                        month = (date / 30) + 10;
                        date = (date % 30);
                    }
                    year = miladiYear - 622;
                }
            }
            switch (month) {
                case 1:
                    strMonth = "فروردین";
                    break;
                case 2:
                    strMonth = "اردیبهشت";
                    break;
                case 3:
                    strMonth = "خرداد";
                    break;
                case 4:
                    strMonth = "تیر";
                    break;
                case 5:
                    strMonth = "مرداد";
                    break;
                case 6:
                    strMonth = "شهریور";
                    break;
                case 7:
                    strMonth = "مهر";
                    break;
                case 8:
                    strMonth = "آبان";
                    break;
                case 9:
                    strMonth = "آذر";
                    break;
                case 10:
                    strMonth = "دی";
                    break;
                case 11:
                    strMonth = "بهمن";
                    break;
                case 12:
                    strMonth = "اسفند";
                    break;
            }
            switch (WeekDay) {
                case 0:
                    strWeekDay = "یکشنبه";
                    break;
                case 1:
                    strWeekDay = "دوشنبه";
                    break;
                case 2:
                    strWeekDay = "سه شنبه";
                    break;
                case 3:
                    strWeekDay = "چهار شنبه";
                    break;
                case 4:
                    strWeekDay = "پنج شنبه";
                    break;
                case 5:
                    strWeekDay = "جمعه";
                    break;
                case 6:
                    strWeekDay = "شنبه";
                    break;
            }
        }
    }

    private class MiladiDate {

        int year;
        int month;
        int date;
        int day;

        public MiladiDate(int y, int m, int d) {
            SET(y, m, d);
        }

        public void SET(int y, int m, int d) {
            setYear(y - 1900);
            setMonth(m - 1);
            setDay(d);
            setDate(d);
        }


        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }


    public String convertSeconds(float second) {
        int rounded = Math.round(second) / 60;
        if (rounded == 0) {
            rounded = 1;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("(خواندن: ");
        builder.append(rounded);
        builder.append(" دقیقه)");
        return builder.toString();
    }
}



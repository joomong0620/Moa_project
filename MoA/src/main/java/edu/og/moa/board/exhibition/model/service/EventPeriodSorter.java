package edu.og.moa.board.exhibition.model.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.EventPeriod;
import edu.og.moa.board.exhibition.model.dto.Exhibition;

public class EventPeriodSorter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String checkEventPeriod(String period, String timeToCompare) { // timeToCompare: "2025-10-01"형식
        if (period == null || period.trim().isEmpty()) return "nullPeriod";
       
        // parsing 방법: regex => seems better
        // 정규식: 날짜 형식만 추출 (YYYY-MM-DD)
        // String regex = "\\d{4}-\\d{2}-\\d{2}"; // 2025-10-8 이슈
        String regex = "\\b(?:\\d{2}|\\d{4})-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|[12][0-9]|3[01])\\b";
        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(period);

        List<String> dates = new ArrayList<>();
        while (matcher.find()) {
            dates.add(matcher.group());
        }
        String startVar = null;
        String endVar = null;
        if (dates.size() >= 2) {
            startVar = dates.get(0);
            endVar = dates.get(1);
        } else {
            return "invalidPeriodFormat"; // 날짜 regex 실패경우
        }

        LocalDate startDate = LocalDate.parse(startVar.trim(), formatter);
        LocalDate endDate = LocalDate.parse(endVar.trim(), formatter);
        LocalDate compareDate = LocalDate.parse(timeToCompare.trim(), formatter);

        if (compareDate.isBefore(startDate)) { // pastEvent, futureEvent, currentEvent
            return "futureEvent";
        } else if (compareDate.isAfter(endDate)) {
            return "pastEvent";
        } else {
            return "currentEvent";
        }
    }

    // 상태 할당
    public static void applyStatusToItems(List<EventPeriod> items, String timeToCompare) {
        for (EventPeriod item : items) {
            String status = checkEventPeriod(item.getEventPeriod(), timeToCompare);
            item.setEventStatus(status);
        }
    }
}
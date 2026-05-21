package com.sirma.backend.service.csv;

import com.sirma.backend.domain.ParseOutcome;
import com.sirma.backend.service.date.DateParserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Validates a single CSV row and returns ParseOutcome.Valid with parsed
 * fields, or ParseOutcome.Invalid with the reason of the first failure.
 */
@Component
public class RowValidator {

    private final DateParserService dateParser;

    public RowValidator(DateParserService dateParser) {
        this.dateParser = dateParser;
    }

    public ParseOutcome validate(long lineNumber, String rawLine, String[] columns) {
        if (columns == null || columns.length != 4) {
            int got = columns == null ? 0 : columns.length;
            return new ParseOutcome.Invalid(lineNumber, rawLine, "Expected 4 columns, got " + got);
        }

        int empId;
        try {
            empId = Integer.parseInt(columns[0].trim());
        } catch (NumberFormatException e) {
            return new ParseOutcome.Invalid(lineNumber, rawLine, "EmpID is not an integer: '" + columns[0] + "'");
        }

        int projectId;
        try {
            projectId = Integer.parseInt(columns[1].trim());
        } catch (NumberFormatException e) {
            return new ParseOutcome.Invalid(lineNumber, rawLine, "ProjectID is not an integer: '" + columns[1] + "'");
        }

        String dateFromRaw = columns[2] == null ? "" : columns[2].trim();
        Optional<LocalDate> dateFromOpt = dateParser.parse(dateFromRaw);
        if (dateFromOpt.isEmpty()) {
            if (dateParser.isNullSentinel(dateFromRaw)) {
                return new ParseOutcome.Invalid(lineNumber, rawLine, "DateFrom is NULL - required");
            }
            return new ParseOutcome.Invalid(lineNumber, rawLine, "DateFrom unparsable: '" + dateFromRaw + "'");
        }
        LocalDate dateFrom = dateFromOpt.get();

        String dateToRaw = columns[3] == null ? "" : columns[3].trim();
        Optional<LocalDate> dateToOpt = dateParser.parse(dateToRaw);
        LocalDate dateTo;
        if (dateToOpt.isEmpty()) {
            if (dateParser.isNullSentinel(dateToRaw)) {
                dateTo = LocalDate.now();
            } else {
                return new ParseOutcome.Invalid(lineNumber, rawLine, "DateTo unparsable: '" + dateToRaw + "'");
            }
        } else {
            dateTo = dateToOpt.get();
        }

        if (dateFrom.isAfter(dateTo)) {
            return new ParseOutcome.Invalid(lineNumber, rawLine,
                    "DateFrom (" + dateFrom + ") > DateTo (" + dateTo + ")");
        }

        return new ParseOutcome.Valid(projectId, empId, dateFrom, dateTo);
    }
}
package com.sirma.backend.service.csv;

import com.sirma.backend.domain.ParseOutcome;
import org.springframework.stereotype.Component;

@Component
public class CsvRowParser {

    private final RowValidator validator;

    public CsvRowParser(RowValidator validator) {
        this.validator = validator;
    }

    public ParseOutcome parseRow(long lineNumber, String[] columns) {
        String rawLine = columns == null ? "" : String.join(",", columns);
        return validator.validate(lineNumber, rawLine, columns);
    }
}

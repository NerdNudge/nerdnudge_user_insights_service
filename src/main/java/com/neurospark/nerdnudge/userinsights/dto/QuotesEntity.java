package com.neurospark.nerdnudge.userinsights.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuotesEntity {
    private String quotesId;
    private String quote;
    private String author;
    private String authorCredentials;
    private String quoteFetchDay;
}

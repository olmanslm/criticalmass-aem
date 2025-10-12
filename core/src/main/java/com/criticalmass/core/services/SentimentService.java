package com.criticalmass.core.services;

import com.criticalmass.core.services.dto.SentimentResult;

public interface SentimentService {
    SentimentResult analyze(String text);
}

package com.management.jupiter.repository.ai;

import java.util.List;

public interface AiProvider {
    List<String> generateNames(int total, String theme);
}

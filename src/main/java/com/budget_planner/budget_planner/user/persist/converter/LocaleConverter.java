package com.budget_planner.budget_planner.user.persist.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;

@Converter(autoApply = false)
public class LocaleConverter implements AttributeConverter<Locale, String> {

    @Override
    public String convertToDatabaseColumn(Locale locale) {
        return locale != null ? locale.toLanguageTag() : null;
    }

    @Override
    public Locale convertToEntityAttribute(String string) {
        return string != null ? Locale.forLanguageTag(string) : null;
    }
}

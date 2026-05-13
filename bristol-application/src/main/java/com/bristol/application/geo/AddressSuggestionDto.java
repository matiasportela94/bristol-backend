package com.bristol.application.geo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddressSuggestionDto {
    String description;
    String mainText;
    String secondaryText;
    String placeId;
}

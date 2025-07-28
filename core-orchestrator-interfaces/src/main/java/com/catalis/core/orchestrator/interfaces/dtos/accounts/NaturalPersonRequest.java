package com.catalis.core.orchestrator.interfaces.dtos.accounts;

import lombok.Builder;

@Builder
public record NaturalPersonRequest(
        // Required
        int userTypeId,
        
        // Required
        int specifiedUSPerson,
        
        // Required & unique
        String email,
        
        String address1,
        
        String postcode,
        
        String city,
        
        String country,
        
        String phone,
        
        String firstname,
        
        String lastname,
        
        String birthdate,
        
        String birthcity,
        
        String birthcountry,
        
        String nationality,
        
        String secondNationality,
        
        String occupation,
        
        String incomeRange,
        
        int politicallyExposed,
        
        String accessTag
) {}
package org.example.mealprepgenerator.dto;



import java.util.List;

public record FoodWasteResponse(
        List<Clearance> clearances,
        Store store
) {

    public record Clearance(
            Offer offer,
            Product product
    ) {}

    public record Offer(
            String currency,
            double newPrice,
            double originalPrice,
            double percentDiscount,
            int stock,
            String endTime
    ) {}

    public record Product(
            String description,
            String ean,
            String image
    ) {}

    public record Store(
            String name,
            String id,
            String brand,
            Address address
    ) {}

    public record Address(
            String city,
            String street,
            String zip,
            String country
    ) {}
}


package com.capstone.quotesapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quotes")
public class Quote {
    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quoteID")
    private Long id;

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    @Column(name = "quote_string")
    private String quote;

}

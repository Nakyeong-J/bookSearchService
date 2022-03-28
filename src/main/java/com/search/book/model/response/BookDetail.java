package com.search.book.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDetail {

    private String title;

    private String contents;

    private String url;

    private String isbn;

    private SimpleDateFormat dateTime;

    private List<String> authors;

    private String publisher;

    private List<String> translators;

    private Integer price;

    @JsonProperty(value = "sale_price")
    private Integer salePrice;

    private String thumbnail;

    private String status;
}
